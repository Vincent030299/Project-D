package com.example.texttospeechprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView meters;
    TextToSpeech textToSpeech;
    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<String>();
        list.add("2 steps forward");
        list.add("Turn left and walk 3 steps forward");
        list.add("Turn right and walk 1 step forward");

        meters = findViewById(R.id.meters);

        meters.setText(list.get(0));

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                    textToSpeech.speak(list.get(0), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }
}
