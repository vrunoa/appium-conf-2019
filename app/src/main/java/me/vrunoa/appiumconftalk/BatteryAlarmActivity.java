package me.vrunoa.appiumconftalk;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BatteryAlarmActivity extends AppCompatActivity {

    private PowerConnectionReceiver powerListener;
    private static Button stopBtt;
    private static TextView batteryPercent, batteryAlarmText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battery_activity);

        setTitle(R.string.battery_charger_alarm);

        batteryPercent = findViewById(R.id.batteryPercent);
        batteryAlarmText = findViewById(R.id.batteryAlarmText);
        stopBtt = findViewById(R.id.stopBtt);

        addReceiver();
    }

    private void addReceiver() {
        powerListener = new PowerConnectionReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(powerListener, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(powerListener);
    }

    private static void setBatteryState(float batteryPct) {
        batteryPercent.setText(String.valueOf((int) (batteryPct*100))+"%");
        if (batteryPct == 1) {
            batteryAlarmText.setVisibility(View.VISIBLE);
            stopBtt.setVisibility(View.VISIBLE);
        } else {
            batteryAlarmText.setVisibility(View.INVISIBLE);
            stopBtt.setVisibility(View.INVISIBLE);
        }
    }


    public static class PowerConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = level / (float)scale;
            setBatteryState(batteryPct);
        }
    }

}
