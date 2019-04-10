package com.example.canscan;

import android.util.Log;

import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.canscan.DataBaseUtils.PASSWORD;
import static com.example.canscan.DataBaseUtils.POINTS;
import static com.example.canscan.DataBaseUtils.STITCH;
import static com.example.canscan.DataBaseUtils.USER_ID;
import static com.example.canscan.DataBaseUtils.USER_NAME;
import static com.example.canscan.DataBaseUtils.getMongoCollection;
import static com.example.canscan.DataBaseUtils.getStitchClient;

class UserLab {

    private User mCurrentUser;

    private static UserLab sUserLab;
    private ArrayList<DatabaseObserver> mDatabaseObservers = new ArrayList<>();

    static UserLab get() {
        if (sUserLab == null) {
            sUserLab = new UserLab();
        }
        return sUserLab;
    }

    private UserLab() { }

    User getCurrentUser() {
        return mCurrentUser;
    }

    void updateCurrentUserFromDatabase() {

        getStitchClient().getAuth().loginWithCredential(new AnonymousCredential()).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Log.e(STITCH, "Update failed!");
                throw Objects.requireNonNull(task.getException());
            }
            List<Document> docs = new ArrayList<>();

            Log.d(STITCH, String.valueOf(Objects.requireNonNull(getStitchClient().getAuth().getUser()).getId()));

            return getMongoCollection()
                    .find(new Document(USER_ID, getStitchClient().getAuth().getUser().getId()))
                    .limit(100)
                    .into(docs);
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(STITCH, "Found User");
                try {
                    updateCurrentUserFromJson(Objects.requireNonNull(task.getResult()));
                }
                catch (JSONException exception) {
                    Log.d(STITCH, exception.getLocalizedMessage());
                }
                return;
            }
            Log.e(STITCH, "Error: " + Objects.requireNonNull(task.getException()).toString());
            task.getException().printStackTrace();
        });
    }

    private void updateCurrentUserFromJson(List<Document> userDocument) throws JSONException {
        if (userDocument.get(0) != null) {
            JSONObject jsonObject = new JSONObject(userDocument.get(0));

            mCurrentUser = new User.Builder()
                    .setUsername(jsonObject.get(USER_NAME).toString())
                    .setPassword(jsonObject.get(PASSWORD).toString())
                    .setScore(Integer.parseInt(jsonObject.get(POINTS).toString()))
                    .create();

            notifyObserversDatabaseCompletedTasks();
        }
        else {
            Log.d(STITCH, "Document is empty");
        }
    }

    void registerDatabaseObserver(DatabaseObserver databaseObserver) {
        if (!mDatabaseObservers.contains(databaseObserver)) {
            mDatabaseObservers.add(databaseObserver);
        }
    }

    void removeDatabaseObserver(DatabaseObserver databaseObserver) {
        mDatabaseObservers.remove(databaseObserver);
    }

    private void notifyObserversDatabaseCompletedTasks() {
        for (DatabaseObserver databaseObserver : mDatabaseObservers) {
            databaseObserver.notifyDatabaseCompletedTasks();
        }
    }

    interface DatabaseObserver {
        void notifyDatabaseCompletedTasks();
    }
}
