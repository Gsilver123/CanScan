package com.example.canscan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateUserActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mCancelButton;
    private Button mAcceptButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);

        initializeViewVariables();
        setOnClickListeners();
    }

    private void initializeViewVariables() {
        mUsernameEditText = findViewById(R.id.new_user_username_textView);
        mPasswordEditText = findViewById(R.id.new_user_password_textView);
        mCancelButton = findViewById(R.id.new_user_cancel_btn);
        mAcceptButton = findViewById(R.id.new_user_accept_btn);
    }

    private void setOnClickListeners() {
        mCancelButton.setOnClickListener(this);
        mAcceptButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.new_user_cancel_btn:
                startActivityFromCreateUserActivity(LoginActivity.class);
                break;
            case R.id.new_user_accept_btn:
                maybeLaunchHomeFragment();
                break;
            default:
                Toast.makeText(this, "Action not supported", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void maybeLaunchHomeFragment() {
        if (UserUtils.maybeCreateUserAndLaunchActivity(mUsernameEditText, mPasswordEditText)) {
            startActivityFromCreateUserActivity(HomeActivity.class);
        }
    }

    private void startActivityFromCreateUserActivity(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        finish();
    }
}
