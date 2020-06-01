package com.example.pathfindingvisualization;

import androidx.annotation.Dimension;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_LOW;
import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM;
import static android.hardware.SensorManager.SENSOR_STATUS_UNRELIABLE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.GridLayout.ALIGN_BOUNDS;
import static android.widget.GridLayout.BASELINE;
import static android.widget.GridLayout.CENTER;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private GridLayout gridLayout;
    private List<CustomNode> path;
    private int currentRow,currentCol;
    private int lastKnownRow = 10000,lastKnownCol = 10000;
    double squarewidth;
    double height,width;
    private float azimuth,pitch,roll;
    private HashMap<Long,int[]> teslaCoordinates = new HashMap<>();
    private float[] floatOrientationMatrix = new float[9];
    private Button saveMap,startMapping;
    private double tesla;
    private double lastTeslaVal = 0.0;
    private double stepsAmount = 0.0;
    private boolean isMapping = false;
    private final float alpha = (float) 0.8000;
    private float[] gravity = new float[3];
    private float[] magnetic = new float[3];
    private TextView teslaText;
    private boolean isFirstMapping = true;
    private boolean isalreadyMapped = false;
    private ArrayList<? extends Double> blocksArray;
    private DatabaseHelper dbHelper;
    private boolean isSensorReliable = true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initializing views and variables
        gridLayout = findViewById(R.id.grid_view);
        saveMap = findViewById(R.id.save_map);
        startMapping = findViewById(R.id.start_mapping);
        teslaText = findViewById(R.id.teslaVal);
        int rows = 30;
        int cols = 16;
        //setting up grid layout
        gridLayout.setRowOrderPreserved(false);
        gridLayout.setRotationX(180.0f);
        gridLayout.requestLayout();
        //Calculating the right width and height of a cell
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = (double) ((displaymetrics.heightPixels*0.88) /rows);
        width = (double) (displaymetrics.widthPixels*0.99/cols);
        //setting up astar array and blocks array
        CustomNode initialNode = new CustomNode(4, 0);
        CustomNode finalNode = new CustomNode(23, 13);
        Astar aStar = new Astar(rows, cols, initialNode, finalNode);
        blocksArray = getIntent().getExtras().getParcelableArrayList("blocks");
        dbHelper = new DatabaseHelper(getApplicationContext());
//        aStar.setBlocks(blocksArray);
//        path = aStar.findPath();
        for(int i = 0; i < gridLayout.getRowCount();i++){
            for(int j = 0; j < gridLayout.getColumnCount();j++){
                    CreateCell(R.drawable.square,i,j);
//                else if(i == finalNode.getRow() && j == finalNode.getCol()){
//                    CreateCell(R.drawable.square_end,i,j);
//                }
//                else{
//                    CreateCell(R.drawable.square,i,j);
//                }

            }
        }
        //drawing blocks
        for(int i = 0; i < blocksArray.size();i++){
            int[] coordinates = DepairNumber(blocksArray.get(i));
            CreateCell(R.drawable.square_block,coordinates[0],coordinates[1]);
        }
        //initializing sensors
        final SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        final Sensor magnetometerReading = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        final Sensor stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        final Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener((SensorEventListener) this,magnetometerReading,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener((SensorEventListener) this,stepDetector,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener((SensorEventListener) this,accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        //saving magnetic mappings
        saveMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dbHelper.addMagneticMapping(teslaCoordinates)){
                    Log.d("mapped points", "amount of point mapped " + teslaCoordinates.size());
                    Toast.makeText(getApplicationContext(),"saved mappings to database",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"failed to save mappings to database",Toast.LENGTH_SHORT).show();
                }

//                if(teslaCoordinates.get(Math.round(tesla)) == null) {
//                    teslaCoordinates.put(Math.round(tesla),new int[]{currentRow,currentCol});
//                }
            }
        });
        //start or stop mapping magnetic values
        startMapping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMapping){
                    startMapping.setText("Start mapping");
                    isMapping = false;
                    lastTeslaVal = 0.0;
                }
                else {
                    startMapping.setText("Stop mapping");
                    isMapping = true;
                }
            }
        });
//        for(int i = 1; i < path.size() - 1; i++){
//            CreateCell(R.drawable.square_path,path.get(i).getRow(),path.get(i).getCol());
//        }
    }
    //draw cell
    private void CreateCell(int img, final int row, final int col){
        ImageView oImageView = new ImageView(this);
        oImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCol = col;
                currentRow = row;
            }
        });
        LayoutParams layoutParams = new LayoutParams();
        layoutParams.width = (int) width;
        layoutParams.height = (int) height;
        layoutParams.columnSpec = GridLayout.spec(col);
        layoutParams.rowSpec = GridLayout.spec(row);
        layoutParams.setMargins(0,0,0,0);
        oImageView.setImageResource(img);
        oImageView.setLayoutParams(layoutParams);
        gridLayout.addView(oImageView);
    }

    //cantor pairing function
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

    //cantor depairing function
    private static int[] DepairNumber(double z){
        long t = (int) (Math.floor((Math.sqrt(8 * z + 1) - 1) / 2));
        int x = (int) (t * (t + 3) / 2 - z);
        int y = (int) (z - t * (t + 1) / 2);
        return new int[]{x, y};
    }
    //px to dp converter
    public float convertPxToDp(Context context, double px) {
        return (float) (px / context.getResources().getDisplayMetrics().density);
    }

    //data from sensors is here
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(isSensorReliable){
            //getting accelerometer values
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                // Isolate the force of gravity with the low-pass filter.
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
            }
            //getting magneto meter values
            else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
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
                teslaText.setText(String.valueOf(Math.round(tesla)));
                Log.d("Field", "\nX :" + A_W[0] + "\nY :" + A_W[1] + "\nZ :" + A_W[2] + "\nTesla :" + tesla);
                if(isMapping){
                    //check if the tesla value is mapped
                    if(teslaCoordinates.get(Math.round(tesla)) == null ) {
                        //check if there are no big value fluctuations
                        if((Math.round(tesla) - lastTeslaVal) < 2 && (Math.round(tesla) - lastTeslaVal) > -2){
                            Log.d("vals", "inserted the following values : \nTesla " + Math.round(tesla) + "\nRow " + currentRow + "\nColumn " + currentCol);
//                        Toast.makeText(getApplicationContext(),"Started mapping",Toast.LENGTH_SHORT).show();
                            teslaCoordinates.put(Math.round(tesla),new int[]{currentRow,currentCol});
                            lastTeslaVal = Math.round(tesla);
                            isalreadyMapped = true;
                        }
                        //if mapping the first value
                        else if(lastTeslaVal == 0.0){
                            Log.d("vals", "inserted the following values : \nTesla " + Math.round(tesla) + "\nRow " + currentRow + "\nColumn " + currentCol);
//                        Toast.makeText(getApplicationContext(),"Started mapping",Toast.LENGTH_SHORT).show();
                            teslaCoordinates.put(Math.round(tesla),new int[]{currentRow,currentCol});
                            lastTeslaVal = Math.round(tesla);
                            isalreadyMapped = true;
                        }

                    }
                }
            }
        }
        //when not mapping magnetic values
        if((!isMapping) && isalreadyMapped){
            //keep track of last known location
            int[] currentLoc = teslaCoordinates.get(Math.round(tesla));
            if(currentLoc != null) {
                Log.d("last known location", "last known location is " + lastKnownRow + " " + lastKnownCol);
                if ( lastKnownRow != currentLoc[0] && lastKnownCol != currentLoc[1]) {
                    Log.d("Mapping", "the program is mapping ");
                    if(lastKnownRow < 10000 && lastKnownCol < 10000)
                        CreateCell(R.drawable.square, lastKnownRow, lastKnownCol);
                    CreateCell(R.drawable.square_path, currentLoc[0], currentLoc[1]);
                    lastKnownRow = currentLoc[0];
                    lastKnownCol = currentLoc[1];
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if((sensor.getType() == Sensor.TYPE_ACCELEROMETER || sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) && (accuracy == SENSOR_STATUS_ACCURACY_LOW || accuracy == SENSOR_STATUS_UNRELIABLE)){
            isSensorReliable = false;
            Toast.makeText(getApplicationContext(),"Please calibrate your phone by moving it in an 8 shape",Toast.LENGTH_SHORT).show();
        }
        else if((sensor.getType() == Sensor.TYPE_ACCELEROMETER && accuracy == SENSOR_STATUS_ACCURACY_HIGH ) || ( sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && accuracy == SENSOR_STATUS_ACCURACY_MEDIUM)){
            isSensorReliable = true;
        }
    }
}
