package com.example.swisscom;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class CatalogFragment extends Fragment {

    public int[] images = {R.drawable.product1,R.drawable.product2,R.drawable.product3,R.drawable.product4,R.drawable.product5,R.drawable.product6,R.drawable.product7,};

    public String[] names = {"OPPO Find X2 Pro", "Samsung Galaxy S20 Ultra 5G", "Samsung Galaxy S20 5G", "iPhone SE", "Apple Watch Series 5 Stainless Steel", "Apple Watch Series 5 Aluminum",
            "Samsung Galaxy Watch Active2 44mm Aluminium"};

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        getActivity().setTitle("Catalog");
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        ListView listView = view.findViewById(R.id.catalog_list);

        CatalogAdapter catalogAdapter = new CatalogAdapter();

        listView.setAdapter(catalogAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                ProductFragment fragment = new ProductFragment();
                Bundle arguments = new Bundle();
                arguments.putString("productName", names[position]);
                arguments.putInt("productImage", images[position]);

                fragment.setArguments(arguments);

                FragmentTransaction Ft = getFragmentManager().beginTransaction();
                Ft.replace(R.id.fragment_container, fragment);
                Ft.addToBackStack(null);
                Ft.commit();
            }
        });

        return view;

    }

    class CatalogAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View catalogList = getLayoutInflater().inflate(R.layout.list_adapter_catalog, null);

            ImageView productImage = catalogList.findViewById(R.id.product_image);
            TextView productName = catalogList.findViewById(R.id.product_name);

            productImage.setImageResource(images[position]);
            productName.setText(names[position]);

            return catalogList;
        }

    }



}
