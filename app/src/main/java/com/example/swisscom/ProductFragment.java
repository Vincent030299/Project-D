package com.example.swisscom;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;


public class ProductFragment extends Fragment {

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        Bundle arguments = this.getArguments();
        String productName = arguments.getString("productName");
        Integer productImage = arguments.getInt("productImage");
        getActivity().setTitle(productName);

        ViewGroup fragmentProduct = (ViewGroup) inflater.inflate(R.layout.fragment_product, container, false);

        ImageView productImageView = fragmentProduct.findViewById(R.id.product_image2);

        productImageView.setImageResource(productImage);


        return fragmentProduct;

    }

}
