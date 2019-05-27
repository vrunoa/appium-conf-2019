package me.vrunoa.appiumconftalk;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class LightSensorActivity extends AppCompatActivity  implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor light;
    private View mainView;
    private TextView textView;
    private ActionBar actionBar;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mainView = findViewById(R.id.sensorContainer);
        textView = findViewById(R.id.sensorText);

        actionBar = getSupportActionBar();
    }

    private void setViewBackgroundColor(float lightValue) {
        int textColorResource = R.color.text_dark;
        int containerColorResource = R.color.container_light;
        int barColor = R.color.colorPrimary;
        if (lightValue < 5000){
            textColorResource = R.color.text_light;
            containerColorResource = R.color.container_dark;
            barColor = R.color.colorPrimaryDark;

        }
        actionBar.setBackgroundDrawable(
                new ColorDrawable(
                        ContextCompat.getColor(
                                LightSensorActivity.this,
                                barColor
                    )
                )
        );

        int containerColor = ContextCompat.getColor(
                LightSensorActivity.this,
                containerColorResource
        );
        String containerContent = String.format("#%06X", 0xFFFFFF & containerColor);
        mainView.setContentDescription(containerContent);
        mainView.setBackgroundColor(containerColor);

        int textColor = ContextCompat.getColor(
                LightSensorActivity.this,
                textColorResource
        );
        String textContent = String.format("#%06X", 0xFFFFFF & textColor);
        textView.setContentDescription(textContent);
        textView.setTextColor(textColor);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float lightValue = event.values[0];
        // Do something with this sensor data.
        setViewBackgroundColor(lightValue);
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
