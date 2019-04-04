package com.example.canscan;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private TextView mNewUserTextView;
    private Button mLoginButton;

    private boolean mCanLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mCanLogIn = true;

        initializeViewVariables();
        setOnClickListeners();
        initializeDataBaseStitchAndVerifyCredentials();
    }

    private void initializeDataBaseStitchAndVerifyCredentials() {
        final StitchAppClient client =
                Stitch.initializeDefaultAppClient("canscandb-cgwjo");

        final RemoteMongoClient mongoClient =
                client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

        final RemoteMongoCollection<Document> coll =
                mongoClient.getDatabase("CanScanUsers").getCollection("Users");

        client.getAuth().loginWithCredential(new AnonymousCredential()).continueWithTask(
                task -> {
                    if (!task.isSuccessful()) {
                        Log.e("STITCH", "Login failed!");
                        throw task.getException();
                    }

                    final Document updateDoc = new Document(
                            "user_name",
                            "Hello"
                    );

                    updateDoc.put("number", 42);
                    return coll.updateOne(
                            null, updateDoc, new RemoteUpdateOptions().upsert(true)
                    );
                }
        ).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Log.e("STITCH", "Update failed!");
                throw task.getException();
            }
            List<Document> docs = new ArrayList<>();
            return coll
                    .find(new Document("owner_id", client.getAuth().getUser().getId()))
                    .limit(100)
                    .into(docs);
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("STITCH", "Found docs: " + task.getResult().toString());
                return;
            }
            Log.e("STITCH", "Error: " + task.getException().toString());
            task.getException().printStackTrace();
        });
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

                break;
            case R.id.new_user_textView_clickable:
                startActivityFromLogin(CreateUserActivity.class);
                break;
            default:
                Toast.makeText(this, "Action not supported", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void startActivityFromLogin(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        finish();
    }
}
