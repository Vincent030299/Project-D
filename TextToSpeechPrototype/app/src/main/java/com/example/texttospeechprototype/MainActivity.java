package com.example.texttospeechprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView meters;
    TextToSpeech textToSpeech;
    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        final Sensor stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener((SensorEventListener) this,stepDetector,SensorManager.SENSOR_DELAY_NORMAL);

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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            textToSpeech.speak("YUP YUP YUP YUP", TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
