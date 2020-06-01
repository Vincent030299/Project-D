package com.example.pathfindingvisualization;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

import static android.widget.GridLayout.ALIGN_BOUNDS;

public class CreateMap extends AppCompatActivity {
    private GridLayout mapGrid;
    private ArrayList<Double> blocks = new ArrayList<>();
    private double squareWidth;
    private int rows, cols;
    private Button openMappingActivity;
    private DatabaseHelper dbHelper;
    private double height,width;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_creator);
        //initializing views and variables
        mapGrid = findViewById(R.id.map_create_grid);
        openMappingActivity = findViewById(R.id.go_to_map);
        dbHelper = new DatabaseHelper(getApplicationContext());
        rows = 30;
        cols = 16;
        //setting up the grid layout
        mapGrid.setAlignmentMode(ALIGN_BOUNDS);
        mapGrid.setRowOrderPreserved(false);
        mapGrid.setRotationX(180.0f);
        mapGrid.requestLayout();
        //calculating the right width and height of cells
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = (double) ((displaymetrics.heightPixels*0.87) /rows);
        width = (double) (displaymetrics.widthPixels*0.99/cols);

        //drawing grid cells
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols ; j++){
                CreateCell(R.drawable.square,i,j);
            }
        }
        //saving blocks
        openMappingActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dbHelper.addBlocks(blocks)){
                    Toast.makeText(getApplicationContext(),"Map created successfully",Toast.LENGTH_SHORT).show();
                    Intent openActivity = new Intent(getApplicationContext(),MainActivity.class);
                    openActivity.putExtra("blocks",blocks);
                    startActivity(openActivity);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Could not create map... try again",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    //creating grid cell
    private void CreateCell(int img, final int row, final int col){
        ImageView oImageView = new ImageView(this);
        final double viewId = UniqueNumber(row,col);
        oImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(blocks.contains(viewId)){
                    blocks.remove(viewId);
                    CreateCell(R.drawable.square_white,row,col);
                }
                else if(!blocks.contains(viewId)){
                    blocks.add(viewId);
                    CreateCell(R.drawable.square_block,row,col);
                }
            }
        });
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = (int) width;
        layoutParams.height = (int) height;
        layoutParams.columnSpec = GridLayout.spec(col);
        layoutParams.rowSpec = GridLayout.spec(row);
        layoutParams.setMargins(0,0,0,0);
        oImageView.setImageResource(img);
        oImageView.setLayoutParams(layoutParams);
        mapGrid.addView(oImageView);
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
}
