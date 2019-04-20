package com.example.canscan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.canscan.Home.HomeActivity;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;

import org.bson.Document;

import java.util.Arrays;
import java.util.Objects;

import static com.example.canscan.DataBaseUtils.BARCODES;
import static com.example.canscan.DataBaseUtils.BIKE;
import static com.example.canscan.DataBaseUtils.METRO;
import static com.example.canscan.DataBaseUtils.PASSWORD;
import static com.example.canscan.DataBaseUtils.POINTS;
import static com.example.canscan.DataBaseUtils.STITCH;
import static com.example.canscan.DataBaseUtils.TICKETS;
import static com.example.canscan.DataBaseUtils.TOTAL_POINTS;
import static com.example.canscan.DataBaseUtils.USER_ID;
import static com.example.canscan.DataBaseUtils.USER_NAME;
import static com.example.canscan.DataBaseUtils.ZIPCODE;
import static com.example.canscan.DataBaseUtils.getMongoCollection;
import static com.example.canscan.DataBaseUtils.getStitchClient;

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

        if (getWindow() != null) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
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
                createUserInDatabase();
                break;
            default:
                Toast.makeText(this, "Action not supported", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void createUserInDatabase() {
        if (!areFieldsFilled()) {
            return;
        }

        getStitchClient().getAuth().loginWithCredential(
                new AnonymousCredential()).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Log.e("STITCH", "Login failed!");
                throw Objects.requireNonNull(task.getException());
            }

            final Document updateDoc = new Document(
                    USER_ID,
                    Objects.requireNonNull(task.getResult()).getId()
            );

            updateDoc.append(USER_NAME, mUsernameEditText.getText().toString().toLowerCase());
            updateDoc.append(PASSWORD, mPasswordEditText.getText().toString().toLowerCase());
            updateDoc.append(TOTAL_POINTS, 0);
            updateDoc.append(POINTS, 0);
            updateDoc.append(METRO, 0);
            updateDoc.append(BIKE, 0);
            updateDoc.append(TICKETS, 0);
            updateDoc.append(ZIPCODE, 0);
            updateDoc.append(BARCODES, Arrays.asList());

            return getMongoCollection().updateOne(
                    null, updateDoc, new RemoteUpdateOptions().upsert(true)
            );
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(STITCH,
                        "Found docs: " + Objects.requireNonNull(task.getResult()).toString());

                startActivityFromCreateUserActivity(HomeActivity.class);

                return;
            }
            Log.e(STITCH, "Error: " + Objects.requireNonNull(task.getException()).toString());
            task.getException().printStackTrace();
        });
    }

    private boolean areFieldsFilled() {
        boolean canLogIn = true;

        if (TextUtils.isEmpty(mUsernameEditText.getText().toString())) {
            mUsernameEditText.setError("Required");
            canLogIn = false;
        }
        if (TextUtils.isEmpty(mPasswordEditText.getText().toString())) {
            mPasswordEditText.setError("Required");
            canLogIn = false;
        }

        return maybeRemoveExtraSpaceAndAllowLogin(canLogIn);
    }

    private boolean maybeRemoveExtraSpaceAndAllowLogin(boolean canLogIn) {
        if (!canLogIn) {
            return false;
        }

        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        for (int i = 0; i < username.length(); i++) {
            if (username.charAt(i) == ' ') {
                canLogIn = false;
                mUsernameEditText.setError("Only Letters, Numbers and Symbols");
            }
        }

        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) == ' ') {
                canLogIn = false;
                mPasswordEditText.setError("Only Letters, Numbers and Symbols");
            }
        }

        if (password.length() < 5) {
            mPasswordEditText.setError("Password must be longer than 5 characters");
            canLogIn = false;
        }

        return canLogIn;
    }

    private void startActivityFromCreateUserActivity(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        finish();
    }
}
