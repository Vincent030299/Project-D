package com.example.swisscom;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_LOW;
import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM;
import static android.hardware.SensorManager.SENSOR_STATUS_UNRELIABLE;
import static android.view.Gravity.CENTER;

public class StoreFragment extends Fragment implements SensorEventListener {
    private static final int PHYISCAL_ACTIVITY = 1;
    private float[] gravity = new float[3];
    private float[] magnetic = new float[3];
    private double tesla;
    private double numOfSteps;
    private final float alpha = (float) 0.8000;
    private boolean isSensorReliable = true;
    private int currentPos = R.drawable.current_pos;
    private GridLayout storeMap;
    int width,height;
    private ArrayList<int[]> blocksArray = new ArrayList<>();
    private HashMap<Long,int[]> teslaMappings = new HashMap<>();
    private boolean locationFound = false;
    private String productName;
    private int[] startingLocation = new int[2];
    private CustomNode initialNode;
    private List<CustomNode> path;
    private int pathDrawable = R.drawable.path_cell;
    private int destinationCell = R.drawable.destination_cell;
    private ArrayList<String> directions = new ArrayList<String>();
    private int i = 1;
    private TextToSpeech textToSpeech;
    private int[] currentLocation;
    private boolean reachedDestination = false;
    private boolean stepDetectorIsReliable = true;
    private SensorManager sensorManager;

    public StoreFragment(String productName) {
        this.productName = productName;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        getActivity().setTitle("Swisscom");
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        storeMap = view.findViewById(R.id.map_grid);
        directions.clear();

        int rows = 30;
        int cols = 30;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = (int) ((displaymetrics.heightPixels*0.86) /30);
        width = (int) (displaymetrics.widthPixels*0.99/16);

        //rotating mapgrid
        storeMap.setRowOrderPreserved(false);
        storeMap.setRotationX(180.0f);
        storeMap.requestLayout();
        DatabaseHelper db = new DatabaseHelper(this.getContext());
        Cursor blocks = db.getBlocks();
        Cursor magneticMappings = db.getMappings();
        while (blocks.moveToNext()){
            blocksArray.add(DepairNumber(Double.parseDouble(blocks.getString(0))));
        }
        while (magneticMappings.moveToNext()){
            teslaMappings.put(Long.parseLong(magneticMappings.getString(0)),new int[]{Integer.parseInt(magneticMappings.getString(1)),Integer.parseInt(magneticMappings.getString(2))});
        }
        for(int i = 0; i < storeMap.getRowCount();i++){
            for(int j = 0; j < storeMap.getColumnCount();j++){
                    CreateCell(R.drawable.cell_placeholder,i,j,width,height);
            }
        }

        sensorManager = (SensorManager) this.getContext().getSystemService(Context.SENSOR_SERVICE);
        final Sensor magnetometerReading = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        final Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        final Sensor stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this,magnetometerReading,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,stepDetector,SensorManager.SENSOR_DELAY_FASTEST);

        return view;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(isSensorReliable){
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                // Isolate the force of gravity with the low-pass filter.
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
            }
            else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                magnetic[0] =  event.values[0];
                magnetic[1] =  event.values[1];
                magnetic[2] = event.values[2];

                float[] R = new float[9];
                float[] I = new float[9];

                //get rotation matrix of the device
                SensorManager.getRotationMatrix(R, I, gravity, magnetic);
                float[] A_D = event.values.clone();
                float[] A_W = new float[3];
                A_W[0] = R[0] * A_D[0] + R[1] * A_D[1] + R[2] * A_D[2];
                A_W[1] = R[3] * A_D[0] + R[4] * A_D[1] + R[5] * A_D[2];
                A_W[2] = R[6] * A_D[0] + R[7] * A_D[1] + R[8] * A_D[2];

                //calculate tesla value using the rotation matrix
                tesla = Math.sqrt((A_W[0]*A_W[0]) + (A_W[1]*A_W[1]) + (A_W[2]*A_W[2]));
                //detects user's current location
                if(!locationFound){
                    if(teslaMappings.containsKey(Math.round(tesla))){
                        startingLocation = teslaMappings.get(Math.round(tesla));
                        initialNode = new CustomNode(startingLocation[0],startingLocation[1]);
                        Astar aStar = new Astar(30, 16, initialNode, productName);
                        aStar.setBlocks(blocksArray);
                        path = aStar.findPath("W");
                        directions = aStar.getStringDirections();
                        textToSpeech = new TextToSpeech(this.getContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int i) {
                                if (i != TextToSpeech.ERROR) {
                                    textToSpeech.setLanguage(Locale.ENGLISH);
                                    if(directions.size()>=1)
                                        textToSpeech.speak(directions.get(0) + " and " + directions.get(1),TextToSpeech.QUEUE_FLUSH,null);
                                    else
                                        textToSpeech.speak("You have reached your destination",TextToSpeech.QUEUE_ADD,null);
                                }
                            }
                        });

                        Log.d("test",directions.toString());
                        CreateCell(currentPos,startingLocation[0],startingLocation[1],50,50);
                        for(int i = 1; i < path.size() - 1;i++){
                            CreateCell(pathDrawable,path.get(i).getRow(),path.get(i).getCol(),40,40);
                        }
                        CreateCell(destinationCell,path.get(path.size() -1).getRow(),path.get(path.size() - 1).getCol(),50,50);
                        locationFound = true;
                    }
                }


            }
            if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR && locationFound){
                numOfSteps = numOfSteps + event.values[0];
                String number = directions.get(i).replaceAll("\\D+","");
                Log.d("number", number);

                if((i >= directions.size() - 1 && !reachedDestination) || (directions.size() == 0) ){
                    textToSpeech.speak("You have reached your destination",TextToSpeech.QUEUE_FLUSH,null);
                    reachedDestination = true;
                }
                if(!number.equals("")){
                    if (numOfSteps == Integer.parseInt(number) && i < directions.size() - 1) {
                        textToSpeech.speak(directions.get(i + 1), TextToSpeech.QUEUE_FLUSH, null);
                        i++;
                    }
                }
                if(number.equals("")){
                    i++;
                }

            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if((sensor.getType() == Sensor.TYPE_ACCELEROMETER || sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) && (accuracy == SENSOR_STATUS_ACCURACY_LOW || accuracy == SENSOR_STATUS_UNRELIABLE)){
            isSensorReliable = false;
            Toast.makeText(this.getContext(),"Please calibrate your phone by moving it in an 8 shape",Toast.LENGTH_SHORT).show();
        }
        if(sensor.getType() == Sensor.TYPE_STEP_DETECTOR && accuracy == SENSOR_STATUS_ACCURACY_LOW){
            stepDetectorIsReliable = false;
        }
        if(sensor.getType() == Sensor.TYPE_STEP_DETECTOR && (accuracy == SENSOR_STATUS_ACCURACY_HIGH || accuracy == SENSOR_STATUS_ACCURACY_MEDIUM)){
            stepDetectorIsReliable = true;
        }
        else if((sensor.getType() == Sensor.TYPE_ACCELEROMETER && accuracy == SENSOR_STATUS_ACCURACY_HIGH ) || ( sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && accuracy == SENSOR_STATUS_ACCURACY_MEDIUM)){
            isSensorReliable = true;
        }

    }
    private void CreateCell(int img, final int row, final int col,int widthVal, int heightVal){
        ImageView oImageView = new ImageView(this.getContext());
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.setGravity(CENTER);
        layoutParams.width = (int) widthVal;
        layoutParams.height = (int) heightVal;
        layoutParams.columnSpec = GridLayout.spec(col);
        layoutParams.rowSpec = GridLayout.spec(row);
        layoutParams.setMargins(0,0,0,0);
        oImageView.setImageResource(img);
        oImageView.setLayoutParams(layoutParams);
        storeMap.addView(oImageView);
    }
    private static double UniqueNumber(int a,int b){
        //Cantors pairing function only works for positive integers
        if (a > -1 || b > -1) {
            //Creating an array of the two inputs for comparison later
            int[] input = {a, b};

            //Using Cantors paring function to generate unique number
            long result = (long) (0.5 * (a + b) * (a + b + 1) + b);

            /*Calling depair function of the result which allows us to compare
             the results of the depair function with the two inputs of the pair
             function*/
            if (Arrays.equals(DepairNumber(result), input)) {
                return result; //Return the result
            } else {
                return -1; //Otherwise return rouge value
            }
        } else {
            return -1; //Otherwise return rouge value
        }
    }
    private static int[] DepairNumber(double z){
        long t = (int) (Math.floor((Math.sqrt(8 * z + 1) - 1) / 2));
        int x = (int) (t * (t + 3) / 2 - z);
        int y = (int) (z - t * (t + 1) / 2);
        return new int[]{x, y};
    }

    @Override
    public void onStop() {
        super.onStop();
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
