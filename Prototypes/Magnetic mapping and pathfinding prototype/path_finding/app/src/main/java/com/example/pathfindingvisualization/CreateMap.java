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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_creator);
        mapGrid = findViewById(R.id.map_create_grid);
        openMappingActivity = findViewById(R.id.go_to_map);
        dbHelper = new DatabaseHelper(getApplicationContext());
        mapGrid.setAlignmentMode(ALIGN_BOUNDS);
        mapGrid.setRowOrderPreserved(false);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        double areaOfSquare = (displaymetrics.heightPixels * displaymetrics.widthPixels)/600.0;
        squareWidth = Math.sqrt(areaOfSquare);
        rows = 30;
        cols = 16;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols ; j++){
                CreateCell(R.drawable.square,i,j);
            }
        }
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
    private void CreateCell(int img, final int row, final int col){
        ImageView oImageView = new ImageView(this);
        final double viewId = UniqueNumber(row,col);
        oImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(blocks.contains(viewId)){
                    blocks.remove(viewId);
                    CreateCell(R.drawable.square,row,col);
                }
                else if(!blocks.contains(viewId)){
                    blocks.add(viewId);
                    CreateCell(R.drawable.square_block,row,col);
                }
            }
        });
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = (int) Math.round(squareWidth);
        layoutParams.height = (int) Math.round(squareWidth);
        layoutParams.columnSpec = GridLayout.spec(col);
        layoutParams.rowSpec = GridLayout.spec(row);
        layoutParams.setMargins(0,0,0,0);
        oImageView.setImageResource(img);
        oImageView.setLayoutParams(layoutParams);
        mapGrid.addView(oImageView);
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
}
