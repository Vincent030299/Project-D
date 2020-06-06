package com.example.swisscom;

import android.database.Cursor;
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
    private ArrayList<Double> blocksArray = new ArrayList<>();

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
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StoreFragment(productName)).commit();
            }
        });

        return fragmentProduct;

    }

}
