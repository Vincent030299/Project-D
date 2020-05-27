package com.example.swisscom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ChatFragment extends Fragment {

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        getActivity().setTitle("Questions");
        return inflater.inflate(R.layout.fragment_chat, container, false);

    }

}
