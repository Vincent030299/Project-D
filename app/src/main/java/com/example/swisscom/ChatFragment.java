package com.example.swisscom;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ChatFragment extends Fragment {

    TextView queryResult;
    RecyclerView recyclerView;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        getActivity().setTitle("Questions");


        ViewGroup fragmentChat = (ViewGroup) inflater.inflate(R.layout.fragment_chat, container, false);

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.dialogflow.com/v1/")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

        List<ResponseMessage> responseMessageList;
        EditText textInput = fragmentChat.findViewById(R.id.textInput);

        recyclerView = fragmentChat.findViewById(R.id.conversation);

        responseMessageList = new ArrayList<>();
        MessageAdapter messageAdapter = new MessageAdapter(responseMessageList);

        ResponseMessage message2 = new ResponseMessage("Hi I'm Swissy! You can ask me any question about our services or products. What can I help you with today? \uD83E\uDD16 ", false);
        responseMessageList.add(message2);
        messageAdapter.notifyDataSetChanged();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(messageAdapter);
        Button sendButton = fragmentChat.findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResponseMessage message = new ResponseMessage(textInput.getText().toString(), true);
                responseMessageList.add(message);
                messageAdapter.notifyDataSetChanged();

                DialogflowApi dialogflowApi = retrofit.create(DialogflowApi.class);

                String query = textInput.getText().toString();
                textInput.getText().clear();

                InputMethodManager imm = (InputMethodManager) getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(((Activity) getContext()).getWindow()
                        .getCurrentFocus().getWindowToken(), 0);

                Post post = new Post("en", query, "12345", "Amsterdam");

                Call<Post> call = dialogflowApi.createPost(post);

                call.enqueue(new Callback<Post>() {
                                 @Override
                                 public void onResponse(Call<Post> call, Response<Post> response) {
                                     if (!response.isSuccessful()){
                                         return;
                                     }

                                     Post postResponse = response.body();

                                     String content = "";
                                     content += postResponse.getResult().getFulfillment().getSpeech();

                                     ResponseMessage message2 = new ResponseMessage(content, false);
                                     responseMessageList.add(message2);
                                     messageAdapter.notifyDataSetChanged();
                                     if(!isViewable()){
                                         recyclerView.smoothScrollToPosition(messageAdapter.getItemCount()-1);
                                     }
                                 }

                                 @Override
                                 public void onFailure(Call<Post> call, Throwable t) {

                                 }
                             }
                );

                recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v,
                                               int left, int top, int right, int bottom,
                                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if (bottom < oldBottom) {
                            recyclerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.smoothScrollToPosition(
                                            recyclerView.getAdapter().getItemCount() - 1);
                                }
                            }, 100);
                        }
                    }
                });
            }
        });

        return fragmentChat;
    }

    public boolean isViewable(){
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int positionOfViewableItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        int itemCount = recyclerView.getAdapter().getItemCount();
        return (positionOfViewableItem>=itemCount);
    }

}
