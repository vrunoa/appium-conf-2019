package me.vrunoa.appiumconftalk;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SMSLoginActivity extends AppCompatActivity {

    private IncomingSms smsListener;
    private static EditText code1, code2, code3, code4;
    private static final int REQ_SMS_PERMISSION = 3;
    private static final String API_NUMBER = "2020";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_activity);

        setTitle(R.string.validate_ur_account);

        code1 = findViewById(R.id.smsCode1);
        code2 = findViewById(R.id.smsCode2);
        code3 = findViewById(R.id.smsCode3);
        code4 = findViewById(R.id.smsCode4);

        final View enterPhoneContainer = findViewById(R.id.enterPhoneContainer);
        final View validCodeContainer = findViewById(R.id.validCodeContainer);
        Button confirmBtt = findViewById(R.id.confirmBtt);
        confirmBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterPhoneContainer.setVisibility(View.GONE);
                validCodeContainer.setVisibility(View.VISIBLE);
            }
        });

        ask4permissions();
    }

    private void ask4permissions() {
        if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)){
            addReceiver();
        }else{
            ActivityCompat.requestPermissions(
                    SMSLoginActivity.this,
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
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
        smsListener = new IncomingSms();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsListener, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsListener);
    }

    private static void fillCode(String num, String message) {
        if (num.equals(API_NUMBER)) {
            code1.setText(message.substring(0,1));
            code2.setText(message.substring(1,2));
            code3.setText(message.substring(2,3));
            code4.setText(message.substring(3,4));
        }
    }

    public static class IncomingSms extends BroadcastReceiver {
        final SmsManager sms = SmsManager.getDefault();

        public void onReceive(Context context, Intent intent) {

            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();
                        fillCode(senderNum, message);

                    }
                }

            } catch (Exception e) {
                Log.i("SMS Error", e.toString());
            }
        }
    }

}
