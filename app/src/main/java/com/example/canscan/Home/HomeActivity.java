package com.example.canscan.Home;

import android.os.Bundle;
import android.os.Handler;
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

public class HomeActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main_screen);

        if (getStitchClient() == null && getMongoCollection() == null) {
            DataBaseUtils.initializeDatabase();
            Log.d(STITCH, "ReInitialize Database");
        }

        UserLab.get().updateCurrentUserFromDatabase();
        new Handler().postDelayed(() ->
                BarcodeLab.get().pullBarcodeListFromDatabase(), 1000);

        startHomeFragment();
    }

    private void startHomeFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, new HomeFragment());
        transaction.commit();
    }
}
