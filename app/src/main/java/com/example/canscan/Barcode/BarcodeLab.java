package com.example.canscan.Barcode;

import android.util.Log;

import com.example.canscan.User.UserLab;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.canscan.DataBaseUtils.BARCODES;
import static com.example.canscan.DataBaseUtils.PASSWORD;
import static com.example.canscan.DataBaseUtils.POINTS;
import static com.example.canscan.DataBaseUtils.STITCH;
import static com.example.canscan.DataBaseUtils.USER_ID;
import static com.example.canscan.DataBaseUtils.USER_NAME;
import static com.example.canscan.DataBaseUtils.getMongoCollection;
import static com.example.canscan.DataBaseUtils.getStitchClient;

public class BarcodeLab {

    private static BarcodeLab sBarcodeLab;

    private ArrayList<String> mBarcodes = new ArrayList<>();

    public static BarcodeLab get() {
        if (sBarcodeLab == null) {
            sBarcodeLab = new BarcodeLab();
        }

        return sBarcodeLab;
    }

    private BarcodeLab() { }

    public boolean addBarcodeToList(String barcode) {
        if (!mBarcodes.contains(barcode)) {
            mBarcodes.add(barcode);
            return true;
        }
        return false;
    }

    public void updateDatabaseWithCurrentListAndPoints() {
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

            updateDoc.append(USER_NAME, UserLab.get().getCurrentUser().getUsername());
            updateDoc.append(PASSWORD, UserLab.get().getCurrentUser().getPassword());
            updateDoc.append(POINTS, UserLab.get().getCurrentUser().getScore());
            updateDoc.append(BARCODES, mBarcodes);

            return getMongoCollection().updateOne(
                    null, updateDoc, new RemoteUpdateOptions().upsert(true)
            );
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(STITCH,
                        "Found docs: " + Objects.requireNonNull(task.getResult()).toString());


                return;
            }
            Log.e(STITCH, "Error: " + Objects.requireNonNull(task.getException()).toString());
            task.getException().printStackTrace();
        });
    }

    public void pullBarcodeListFromDatabase() {
        getStitchClient().getAuth().loginWithCredential(
                new AnonymousCredential()).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Log.e(STITCH, "Update failed!");
                throw Objects.requireNonNull(task.getException());
            }
            List<Document> docs = new ArrayList<>();

            return getMongoCollection()
                    .find(new Document(USER_ID, Objects.requireNonNull(getStitchClient()
                            .getAuth().getUser()).getId()))
                    .limit(1)
                    .into(docs);
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(STITCH,
                        "Found docs when pulling from database: " + Objects.requireNonNull(task.getResult()).toString());

                try {
                    retrieveBarcodeListAndPointsFromDocument(task.getResult());
                }
                catch (JSONException | NullPointerException exception) {
                    Log.d(STITCH, "Cannot parse JSON Object");
                }

                return;
            }
            Log.e(STITCH, "Error: " + Objects.requireNonNull(task.getException()).toString());
            task.getException().printStackTrace();
        });
    }

    private void retrieveBarcodeListAndPointsFromDocument(List<Document> documentList) throws JSONException, NullPointerException {
        if (documentList.get(0) == null) {
            return;
        }

        JSONObject jsonObject = new JSONObject(documentList.get(0).toJson());
        JSONArray jsonArray = jsonObject.getJSONArray(BARCODES);
        Log.d(STITCH, jsonArray.toString());

        UserLab.get().getCurrentUser().setScore(Integer.parseInt(jsonObject.get(POINTS).toString()));

        mBarcodes = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            mBarcodes.add(jsonArray.get(i).toString());
        }
    }
}
