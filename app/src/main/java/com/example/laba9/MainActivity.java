package com.example.laba9;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements
        SensorEventListener {
    private ListView sensorsText;
    ArrayAdapter<String> adapter;
    private TextView accelerometerText;
    private TextView luxText;
    private SensorManager sensorManager;
    private Sensor luxSensor;
    private Sensor accelSensor;
    private float[] valuesAccel = new float[3];
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorsText = findViewById(R.id.sensors);
        accelerometerText = findViewById(R.id.accelerometerText);
        luxText = findViewById(R.id.luxText);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        luxSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        List<Sensor> deviceSensors =
                sensorManager.getSensorList(Sensor.TYPE_ALL);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());

        for (int i = 0; i < deviceSensors.size(); i++)
        {
            String name = deviceSensors.get(i).getName();
            int end = name.indexOf(' ');

            adapter.add(end != -1 ? name.substring(end) : name);
        }

        adapter.notifyDataSetChanged();
        sensorsText.setAdapter(adapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        sensorManager.registerListener(this, accelSensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this, luxSensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        timer = new Timer();

        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(() ->
                {
                    accelerometerText.setText("");
                    accelerometerText.append("Ускорение\nX: " + String.format("%.3f",valuesAccel[0]) +
                            "\nY: " + String.format("%.3f",valuesAccel[1]) + "\nZ: " + String.format("%.3f",valuesAccel[2]));
                });
            }
        };

        timer.schedule(task, 0, 1000);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);

        timer.cancel();

        accelerometerText.setText("");
        luxText.setText("");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            for (int i = 0; i < 3; i++)
            {
                valuesAccel[i] = event.values[i];
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            luxText.setText("Свет \n" + event.values[0]);
        }
    }
}