package com.example.texttospeechprototype;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int PHYISCAL_ACTIVITY = 1;
    TextView meters,stepCount;
    TextToSpeech textToSpeech;
    List<String> list;
    double numOfSteps = 0.0;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PHYISCAL_ACTIVITY);
            }
        }
        final SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        final Sensor stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener((SensorEventListener) this,stepDetector,SensorManager.SENSOR_DELAY_NORMAL);
        list = new ArrayList<String>();
        list.add("Walk 5 steps forward");
        list.add("Turn left and walk 10 steps forward");
        list.add("Turn right and walk 10 steps forward");

        meters = findViewById(R.id.meters);
        stepCount = findViewById(R.id.steps_count);

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
            Log.d("step", ""+ sensorEvent.values[0]);
            numOfSteps = numOfSteps + sensorEvent.values[0];
            stepCount.setText(String.valueOf(numOfSteps));
            switch((int) numOfSteps){
                case 5:
                    textToSpeech.speak(list.get(1), TextToSpeech.QUEUE_FLUSH, null);
                    meters.setText(list.get(1));
                    break;
                case 15:
                    textToSpeech.speak(list.get(2), TextToSpeech.QUEUE_FLUSH, null);
                    meters.setText(list.get(2));
                    break;
                case 25:
                    textToSpeech.speak("Finished", TextToSpeech.QUEUE_FLUSH, null);
                    meters.setText("Done");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
