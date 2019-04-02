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

    private static FragmentManager sSupportManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initializeViewVariables();
        setOnClickListeners();

        sSupportManager = getSupportFragmentManager();
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
                startFragment(new CreateUserFragment(), sSupportManager);
                break;
            default:
                Toast.makeText(this, "Action not supported", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void maybeLogInAndRetrieveLoginCredentials() {
        boolean canLogIn = true;

        if (TextUtils.isEmpty(mUsernameEditText.getText().toString())) {
            mUsernameEditText.setError("Required");
            canLogIn = false;
        }
        if (TextUtils.isEmpty(mPasswordEditText.getText().toString())) {
            mPasswordEditText.setError("Required");
            canLogIn = false;
        }

        if(!canLogIn) {
            return;
        }

        if (mUsernameEditText.getText().toString() == UserLab.get().getUser().getUsername()
                && mPasswordEditText.getText().toString() == UserLab.get().getUser().getPassword()) {

            startFragment(new HomeFragment(), getSupportFragmentManager());
        }
        else {
            Toast.makeText(this, "Incorrect Login", Toast.LENGTH_SHORT).show();
        }
    }

    private static void startFragment(Fragment fragment, FragmentManager manager) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment_container, fragment);
        transaction.commit();
    }

    public static void startFragment(Fragment fragment) {
        startFragment(fragment, sSupportManager);
    }
}
