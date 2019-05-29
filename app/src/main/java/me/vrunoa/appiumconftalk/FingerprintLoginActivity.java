package me.vrunoa.appiumconftalk;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class FingerprintLoginActivity extends AppCompatActivity {

    private static final int REQ_FINGERPRINT_PERMISSION = 1;
    private static final String KEY_NAME = "test_key";
    private KeyguardManager mKeyguardManager;
    private FingerprintManager mFingerPrintManager;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private FingerprintManager.CryptoObject mCryptoObj;
    private boolean backdoor = false;
    private TextView fingerprintText;

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_activity);
        fingerprintText = findViewById(R.id.fingerprintMessage);
        checkFingerprints();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void showGoodFingerprintMessage() {
        fingerprintText.setText(R.string.you_re_authenticated);
        fingerprintText.setVisibility(View.VISIBLE);
        fingerprintText.setTextColor(getResources().getColor(R.color.success));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(FingerprintLoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, 1500);

    }

    private void showBadFingerprintMessage() {
        fingerprintText.setText(R.string.failed_to_auth_fingerprint);
        fingerprintText.setVisibility(View.VISIBLE);
        fingerprintText.setTextColor(getResources().getColor(R.color.error));
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private class FingerprintListener extends FingerprintManager.AuthenticationCallback {

        private FingerprintManager.CryptoObject cryptoObject;

        public FingerprintListener(FingerprintManager.CryptoObject cryptoObject) {
            super();
            this.cryptoObject = cryptoObject;
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            if(result.getCryptoObject().equals(this.cryptoObject)) {
                showGoodFingerprintMessage();
            } else {
                showBadFingerprintMessage();
            }
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            showBadFingerprintMessage();
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            showBadFingerprintMessage();
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkFingerprints() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_FINGERPRINT}, REQ_FINGERPRINT_PERMISSION);
            return;
        }
        mKeyguardManager = getSystemService(KeyguardManager.class);
        mFingerPrintManager = getSystemService(FingerprintManager.class);

        if(!backdoor) {
            Toast.makeText(FingerprintLoginActivity.this, R.string.checking_secure_n_permissions, Toast.LENGTH_SHORT).show();
            if (!mKeyguardManager.isKeyguardSecure()) {
                Toast.makeText(FingerprintLoginActivity.this, R.string.go_2_settings, Toast.LENGTH_LONG).show();
                return;
            }
            if (!mFingerPrintManager.hasEnrolledFingerprints()) {
                Toast.makeText(FingerprintLoginActivity.this, R.string.go_2_settings, Toast.LENGTH_LONG).show();
                return;
            }
        }
        createKey();
        showFingerPrintDialog();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showFingerPrintDialog() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
            Cipher lu = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            lu.init(Cipher.ENCRYPT_MODE, key);

            mCryptoObj = new FingerprintManager.CryptoObject(lu);
            FingerprintListener listener = new FingerprintListener(mCryptoObj);
            mFingerPrintManager.authenticate(mCryptoObj, new CancellationSignal(),
                    0, listener, null);

        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(FingerprintLoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createKey() {
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyStore.load(null);
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            mKeyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            mKeyGenerator.generateKey();

        } catch (Exception e) {
            Toast.makeText(FingerprintLoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_FINGERPRINT_PERMISSION &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(FingerprintLoginActivity.this, R.string.must_accept_fingerprint_permission, Toast.LENGTH_LONG).show();
            return;
        }
        checkFingerprints();
    }
}