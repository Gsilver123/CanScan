package com.example.canscan.Home;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.canscan.Barcode.BarcodeLab;
import com.example.canscan.DataBaseUtils;
import com.example.canscan.R;
import com.example.canscan.User.UserLab;

import static com.example.canscan.DataBaseUtils.STITCH;
import static com.example.canscan.DataBaseUtils.getMongoCollection;
import static com.example.canscan.DataBaseUtils.getStitchClient;
import com.example.canscan.User.UserLab.DatabaseObserver;

public class HomeActivity extends AppCompatActivity implements DatabaseObserver {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main_screen);

        if (getStitchClient() == null && getMongoCollection() == null) {
            DataBaseUtils.initializeDatabase();
            Log.d(STITCH, "ReInitialize Database");
        }

        UserLab.get().registerDatabaseObserver(this);
        UserLab.get().updateCurrentUserFromDatabase();
        startHomeFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UserLab.get().removeDatabaseObserver(this);
    }

    private void startHomeFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, new HomeFragment());
        transaction.commit();
    }

    @Override
    public void notifyObserverUserCreatedFromDatabase(boolean shouldPullFromDatabase) {
        if (shouldPullFromDatabase) {
            BarcodeLab.get().pullBarcodeListFromDatabase();
        }
    }
}
