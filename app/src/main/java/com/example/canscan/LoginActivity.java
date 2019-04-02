package com.example.canscan;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private TextView mNewUserTextView;
    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initializeViewVariables();

        setOnClickListeners();
    }

    private void initializeViewVariables() {
        mUsernameEditText = findViewById(R.id.username_editText);
        mPasswordEditText = findViewById(R.id.password_editText);
        mLoginButton = findViewById(R.id.login_btn);
        mNewUserTextView = findViewById(R.id.new_user_textView_clickable);
    }

    private void setOnClickListeners() {
        mLoginButton.setOnClickListener(this);
        mNewUserTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                maybeLogInAndRetrieveLoginCredentials();
                break;
            case R.id.new_user_textView_clickable:
                startFragment(new CreateUserFragment());
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

    public void startFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
