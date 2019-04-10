package com.example.canscan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.canscan.DataBaseUtils.PASSWORD;
import static com.example.canscan.DataBaseUtils.STITCH;
import static com.example.canscan.DataBaseUtils.USER_ID;
import static com.example.canscan.DataBaseUtils.USER_NAME;
import static com.example.canscan.DataBaseUtils.getStitchClient;
import static com.example.canscan.DataBaseUtils.getMongoCollection;

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

        if (getStitchClient() == null && getMongoCollection() == null) {
            DataBaseUtils.initializeDatabase();
        }

        if (getWindow() != null) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
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
                retrieveDataFromDatabaseAndMaybeLaunchHomeActivity();
                break;
            case R.id.new_user_textView_clickable:
                startActivityFromLogin(CreateUserActivity.class);
                break;
            default:
                Toast.makeText(this, "Action not supported", Toast.LENGTH_SHORT).show();
                break;
        }
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

        return canLogIn;
    }

    private void retrieveDataFromDatabaseAndMaybeLaunchHomeActivity() {
        if (!areFieldsFilled()) {
            return;
        }

        getStitchClient().getAuth().loginWithCredential(new AnonymousCredential()).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Log.e(STITCH, "Update failed!");
                throw Objects.requireNonNull(task.getException());
            }
            List<Document> docs = new ArrayList<>();

            return getMongoCollection()
                    .find(new Document(USER_ID, getStitchClient().getAuth().getUser().getId()))
                    .limit(100)
                    .into(docs);
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(STITCH,
                        "Found docs: " + Objects.requireNonNull(task.getResult()).toString());

                try {
                    compareUserInputToDatabase(task.getResult());
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }

                return;
            }
            Log.e(STITCH, "Error: " + Objects.requireNonNull(task.getException()).toString());
            task.getException().printStackTrace();
        });
    }

    private void compareUserInputToDatabase(List<Document> documents) throws JSONException {
        JSONObject json = new JSONObject(documents.get(0).toJson());

        if (!(mUsernameEditText.getText().toString().equals(json.get(USER_NAME)))
            || !(mPasswordEditText.getText().toString().equals(json.get(PASSWORD)))) {

            Toast.makeText(this, "Username - Password incorrect", Toast.LENGTH_SHORT).show();
        }
        else {
            startActivityFromLogin(HomeActivity.class);
        }
    }

    private void startActivityFromLogin(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        finish();
    }
}
