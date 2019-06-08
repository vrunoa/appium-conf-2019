package me.vrunoa.appiumconftalk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private EditText userEditText;
    private EditText pwdEditText;
    private TextView alertText;

    private String SUPER_SECRET_USER = "admin";
    private String SUPER_SECRET_PWD = "admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        setTitle(R.string.welcome);

        alertText = findViewById(R.id.alertText);
        userEditText = findViewById(R.id.userEditText);
        userEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                alertText.setVisibility(View.INVISIBLE);
            }
        });

        pwdEditText = findViewById(R.id.pwdEditText);
        pwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                alertText.setVisibility(View.INVISIBLE);
            }
        });

        Button enterBtt = findViewById(R.id.enterBtt);
        enterBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLogin();
            }
        });
    }

    private void validateLogin() {
        String user = userEditText.getText().toString();
        String pwd = pwdEditText.getText().toString();
        if (user.equalsIgnoreCase("") || pwd.equalsIgnoreCase("")) {
            pwdEditText.getText().clear();
            userEditText.getText().clear();
            alertText.setText(R.string.please_complete_all_the_fields);
            alertText.setVisibility(View.VISIBLE);
            return;
        }
        if (!user.equalsIgnoreCase(SUPER_SECRET_USER) && !pwd.equalsIgnoreCase(SUPER_SECRET_PWD)) {
            userEditText.getText().clear();
            pwdEditText.getText().clear();
            alertText.setText(R.string.wrong_user_pass);
            alertText.setVisibility(View.VISIBLE);
            return;
        }
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
