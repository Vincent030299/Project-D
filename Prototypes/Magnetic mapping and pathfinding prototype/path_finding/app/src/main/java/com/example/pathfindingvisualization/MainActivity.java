package com.example.pathfindingvisualization;

import androidx.annotation.Dimension;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.circuitwall.ml.algorithm.test.evolution.TestAlgorithm;

import java.util.HashMap;
import java.util.List;

import static android.widget.GridLayout.ALIGN_BOUNDS;

public class MainActivity extends AppCompatActivity {
    GridLayout gridLayout;
    List<CustomNode> path;
    int currentRow,currentCol;
    int lastKnownRow,lastKnownCol;
    double squarewidth;
    double height,width;
    private float azimuth,pitch,roll;
    private HashMap<Double,int[]> teslaCoordinates = new HashMap<>();
    private float[] floatOrientationMatrix = new float[9];
    private Button mapToCell,stopMapping;
    double tesla,tesla2;
    private double stepsAmount = 0.0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridLayout = findViewById(R.id.grid_view);
        mapToCell = findViewById(R.id.map_to_cell);
        stopMapping = findViewById(R.id.stop_mapping);
        stopMapping.setEnabled(false);
        gridLayout.setAlignmentMode(ALIGN_BOUNDS);
        gridLayout.setRowOrderPreserved(false);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        double areaOfSquare = (displaymetrics.heightPixels * displaymetrics.widthPixels)/600.0;
        squarewidth = Math.sqrt(areaOfSquare);
        CustomNode initialNode = new CustomNode(4, 0);
        CustomNode finalNode = new CustomNode(23, 13);
        int rows = 30;
        int cols = 20;
        Astar aStar = new Astar(rows, cols, initialNode, finalNode);
        int[][] blocksArray = new int[][]
                {{0,0},{1,0},{2,0},{6,0},{7,0},{8,0},{9,0},{10,0},{11,0},{12,0}
                ,{13,0},{14,0},{15,0},{16,0},{17,0},{18,0},{19,0},{20,0},{21,0},{22,0}
                ,{23,0},{24,0},{25,0},{26,0},{27,0},{28,0},{29,0},{10,6},{10,7},{10,8}
                ,{10,9},{11,6},{11,7},{11,8},{11,9},{12,6},{12,7},{12,8},{12,9},{13,6}
                ,{13,7},{13,8},{13,9},{14,6},{14,7},{14,8},{14,9},{15,6},{15,7},{15,8}
                ,{15,9},{16,6},{16,7},{16,8},{16,9},{17,6},{17,7},{17,8},{17,9},{18,6}
                ,{18,7},{18,8},{18,9},{0,15},{1,15},{2,15},{3,15},{4,15},{5,15},{6,15},{7,15},{8,15},{9,15},{10,15},{11,15},{12,15}
                ,{13,15},{14,15},{15,15},{16,15},{17,15},{18,15},{19,15},{20,15},{21,15},{22,15}
                ,{23,15},{24,15},{25,15},{26,15},{27,15},{28,15},{29,15}
                ,{0,1},{0,2},{0,3},{0,4},{0,5},{0,6},{0,7},{0,8},{0,9},{0,10},{0,11},{0,12},{0,13},{0,14}
                ,{29,1},{29,2},{29,3},{29,4},{29,5},{29,6},{29,7},{29,8},{29,9},{29,10},{29,11},{29,12},{29,13},{29,14}};
        aStar.setBlocks(blocksArray);
//        path = aStar.findPath();
        for(int i = 0; i < gridLayout.getRowCount();i++){
            for(int j = 0; j < gridLayout.getColumnCount();j++){
//                if(i == initialNode.getRow() && j == initialNode.getCol()){
//                    CreateCell(R.drawable.square_start,i,j);
//                }
//                else if(i == finalNode.getRow() && j == finalNode.getCol()){
//                    CreateCell(R.drawable.square_end,i,j);
//                }
//                else{
//                    CreateCell(R.drawable.square,i,j);
//                }
                CreateCell(R.drawable.square,i,j);
            }
        }
        for(int i = 0; i < blocksArray.length;i++){
            CreateCell(R.drawable.square_block,blocksArray[i][0],blocksArray[i][1]);
        }


        final SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        final Sensor magnetometerReading = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        final Sensor stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        tesla2 = 0.0;
        SensorEventListener magnetometerListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                    azimuth = Math.round(event.values[0]);
                    pitch = Math.round(event.values[1]);
                    roll = Math.round(event.values[2]);

                    tesla = Math.sqrt((azimuth*azimuth) + (pitch*pitch) + (roll*roll));
                    Log.d("tesla", "tesla is " + String.valueOf(tesla));
                    Log.d("tesla", "tesla2 is " + String.valueOf(tesla2));
                    Log.d("tesla", "rounded tesla is " + String.valueOf(Math.ceil(tesla)));
                    int[] currentLoc = teslaCoordinates.get(tesla);
                    if(currentLoc != null){
                        lastKnownRow = currentLoc[0];
                        lastKnownCol = currentLoc[1];
                        CreateCell(R.drawable.square_path,currentLoc[0],currentLoc[1]);
                    }
//                if(Math.round(tesla) < Math.round(tesla2 + 5) && Math.round(tesla) > Math.round(tesla2 - 5))
//                CreateCell(R.drawable.square_start,20,4);
                    else{
                        CreateCell(R.drawable.square,lastKnownRow,lastKnownCol);
                    }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.d("accuracy", "onAccuracyChanged: " + String.valueOf(accuracy));
            }
        };

        SensorEventListener stepCounterListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                stepsAmount++;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(magnetometerListener,magnetometerReading,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(stepCounterListener,stepDetector,SensorManager.SENSOR_DELAY_NORMAL);

        mapToCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),String.valueOf(Math.round(tesla)),Toast.LENGTH_SHORT).show();

                if(!teslaCoordinates.containsKey(tesla))
                    teslaCoordinates.put(tesla,new int[]{currentRow,currentCol});
//                if(!teslaCoordinates.containsKey(Math.round(tesla + 1)))
//                    teslaCoordinates.put(Math.round(tesla + 1),new int[]{currentRow,currentCol});
//                if(!teslaCoordinates.containsKey(Math.round(tesla + 2)))
//                    teslaCoordinates.put(Math.round(tesla + 2),new int[]{currentRow,currentCol});
//                if(!teslaCoordinates.containsKey(Math.round(tesla + 3)))
//                    teslaCoordinates.put(Math.round(tesla + 3),new int[]{currentRow,currentCol});
//                if(!teslaCoordinates.containsKey(Math.round(tesla + 4)))
//                    teslaCoordinates.put(Math.round(tesla + 4),new int[]{currentRow,currentCol});
//                if(!teslaCoordinates.containsKey(Math.round(tesla + 5)))
//                    teslaCoordinates.put(Math.round(tesla + 5),new int[]{currentRow,currentCol});
//                if(!teslaCoordinates.containsKey(Math.round(tesla - 1)))
//                    teslaCoordinates.put(Math.round(tesla - 1),new int[]{currentRow,currentCol});
//                if(!teslaCoordinates.containsKey(Math.round(tesla - 2)))
//                    teslaCoordinates.put(Math.round(tesla - 2 ),new int[]{currentRow,currentCol});
//                if(!teslaCoordinates.containsKey(Math.round(tesla - 3)))
//                    teslaCoordinates.put(Math.round(tesla - 3),new int[]{currentRow,currentCol});
//                if(!teslaCoordinates.containsKey(Math.round(tesla - 4)))
//                    teslaCoordinates.put(Math.round(tesla - 4),new int[]{currentRow,currentCol});
//                if(!teslaCoordinates.containsKey(Math.round(tesla - 5)))
//                    teslaCoordinates.put(Math.round(tesla - 5),new int[]{currentRow,currentCol});
            }
        });
        stopMapping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
//        for(int i = 1; i < path.size() - 1; i++){
//            CreateCell(R.drawable.square_path,path.get(i).getRow(),path.get(i).getCol());
//        }
    }
    private void CreateCell(int img, final int row, final int col){
        ImageView oImageView = new ImageView(this);
        oImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCol = col;
                currentRow = row;
            }
        });
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = (int) Math.round(squarewidth);
        layoutParams.height = (int) Math.round(squarewidth);
        layoutParams.columnSpec = GridLayout.spec(col);
        layoutParams.rowSpec = GridLayout.spec(row);
        layoutParams.setMargins(0,0,0,0);
        oImageView.setImageResource(img);
        oImageView.setLayoutParams(layoutParams);
        gridLayout.addView(oImageView);
    }
}
