package com.example.swisscom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.annotation.Dimension;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Build;
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


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.GridLayout.ALIGN_BOUNDS;
import static android.widget.GridLayout.BASELINE;
import static android.widget.GridLayout.CENTER;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private List<CustomNode> path;
    private int currentRow,currentCol;
    private int lastKnownRow = 10000,lastKnownCol = 10000;
    double squarewidth;
    double height,width;
    private ArrayList<? extends Double> blocksArray;
    private int PHYISCAL_ACTIVITY = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CatalogFragment()).commit();
        BottomNavigationView bottom_nav = findViewById(R.id.bottom_nav);
        bottom_nav.setOnNavigationItemSelectedListener(nav_listener);
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PHYISCAL_ACTIVITY);
            }
        }
//        for(int i = 0; i < gridLayout.getRowCount();i++){
//            for(int j = 0; j < gridLayout.getColumnCount();j++){
//                if(i == initialNode.getRow() && j == initialNode.getCol()){
//                    CreateCell(R.drawable.square_start,i,j);
//                }
//                else if(i == finalNode.getRow() && j == finalNode.getCol()){
//                    CreateCell(R.drawable.square_end,i,j);
//                }
//                else{
//                    CreateCell(R.drawable.square,i,j);
//                }
//                //CreateCell(R.drawable.square,i,j);
//            }
//        }
//        for(int i = 0; i < blocksArray.length;i++){
//            CreateCell(R.drawable.square_block,blocksArray[i][0],blocksArray[i][1]);
//        }
   }

    private BottomNavigationView.OnNavigationItemSelectedListener nav_listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selected_fragment = null;

                    switch (item.getItemId()){
                        case R.id.nav_store:
                            selected_fragment = new StoreFragment("OPPO Find X2 Pro");
                            break;
                        case R.id.nav_catalog:
                            selected_fragment = new CatalogFragment();
                            break;
                        case R.id.nav_chat:
                            selected_fragment = new ChatFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected_fragment).addToBackStack(null).commit();

                    return true;
                }
            };


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}
