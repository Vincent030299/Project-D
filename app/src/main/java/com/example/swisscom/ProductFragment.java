package com.example.swisscom;

import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;


public class ProductFragment extends Fragment {
    private GridLayout gridLayout;
    private List<CustomNode> path;
    private int currentRow,currentCol;
    private int lastKnownRow = 10000,lastKnownCol = 10000;
    double squarewidth;
    double height,width;
    private ArrayList<? extends Double> blocksArray;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        Bundle arguments = this.getArguments();
        final String productName = arguments.getString("productName");
        Integer productImage = arguments.getInt("productImage");

        getActivity().setTitle(productName);

        ViewGroup fragmentProduct = (ViewGroup) inflater.inflate(R.layout.fragment_product, container, false);

        ImageView productImageView = fragmentProduct.findViewById(R.id.product_image2);
        Button pathfinding = fragmentProduct.findViewById(R.id.button);
        productImageView.setImageResource(productImage);
        pathfinding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rows = 30;
                int cols = 16;
                CustomNode initialNode = new CustomNode(1, 12);
                CustomNode finalNode = new CustomNode(11, 11);
                Astar aStar = new Astar(rows, cols, initialNode, productName);
                int[][] blocksArray = new int[][]
                        {{2,11},{3,11},{4,11},{2,12},{3,12},{4,12},{8,11},{9,11},{10,11},{8,12},{9,12},{10,12},{15,4},{14,4},{13,4},{12,4},{11,4},{0,7},{1,7},{2,7},{3,7},{4,7},{5,7},{6,7},{7,7},{8,7},{9,7},{10,7},{10,6},{10,5},{10,4},{16,4},{16,5},{16,6},{16,7},{16,8},{16,9},{16,10},{16,11},{16,12},{16,13},{16,14},{16,15}};;
                aStar.setBlocks(blocksArray);
                path = aStar.findPath("N");
                Log.d("test",path.toString());
            }
        });

        return fragmentProduct;

    }

}
