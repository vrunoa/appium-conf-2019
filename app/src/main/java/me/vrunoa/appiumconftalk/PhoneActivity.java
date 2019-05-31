package me.vrunoa.appiumconftalk;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.Date;

public class PhoneActivity extends AppCompatActivity {

    private static final int REQ_SMS_PERMISSION = 3;
    private PhoneStateReceiver phoneListener;
    private static VideoView videoView;
    private static MediaController mediaController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_not_handled_activity);

        videoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.appium);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        ask4permissions();
    }

    private void ask4permissions() {
        if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)){
            addReceiver();
        }else{
            ActivityCompat.requestPermissions(
                    PhoneActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQ_SMS_PERMISSION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)){
            addReceiver();
        }
    }

    private void addReceiver() {
        phoneListener = new PhoneStateReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(phoneListener, intentFilter);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(phoneListener);
    }


    public static class PhoneStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                videoView.pause();
                mediaController.show(15000);
            } else if(stateStr.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE) && !videoView.isPlaying()) {
                videoView.start();
                mediaController.hide();
            }
        }
    }
}
