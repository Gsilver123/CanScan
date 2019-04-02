package com.example.canscan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mUsernameEditText = findViewById(R.id.username_editText);
        mPasswordEditText = findViewById(R.id.password_editText);
        mLoginButton = findViewById(R.id.login_btn);

        mLoginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                maybeLogInAndRetrieveLoginCredentials();
                break;
            default:
                Toast.makeText(this, "Action not supported", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void maybeLogInAndRetrieveLoginCredentials() {
        boolean canLogIn = true;

        if (TextUtils.isEmpty(mUsernameEditText.getText())) {
            mUsernameEditText.setError("Required");
            canLogIn = false;
        }
        if (TextUtils.isEmpty(mPasswordEditText.getText())) {
            mPasswordEditText.setError("Required");
            canLogIn = false;
        }

        if(!canLogIn) {
            return;
        }
    }
}
