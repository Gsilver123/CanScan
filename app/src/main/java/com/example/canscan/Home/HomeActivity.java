package com.example.canscan.Home;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.canscan.Barcode.BarcodeLab;
import com.example.canscan.DataBaseUtils;
import com.example.canscan.R;
import com.example.canscan.User.UserLab;

import static com.example.canscan.DataBaseUtils.STITCH;
import static com.example.canscan.DataBaseUtils.getMongoCollection;
import static com.example.canscan.DataBaseUtils.getStitchClient;
import com.example.canscan.User.UserLab.DatabaseObserver;
import com.example.canscan.ZipcodeLab;

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
            if (String.valueOf(UserLab.get().getCurrentUser().getZipcode()).length() != 5) {
                setZipcodeAlertDialog();
            }
        }
    }

    private void setZipcodeAlertDialog() {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.profile_update_zipcode_alert_dialog, null, false);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Update Zipcode")
                .setNegativeButton("Later", (dialogInterface, which) -> dialogInterface.dismiss())
                .setPositiveButton("Update", (dialogInterface, which) -> { })
                .create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newZipcode = ((EditText)
                    view.findViewById(R.id.update_profile_editText)).getText().toString();

            if (newZipcode.length() == 5) {
                UserLab.get().getCurrentUser().setZipcode(Integer.parseInt(newZipcode));
                BarcodeLab.get().updateDatabaseWithCurrentListAndPoints();
                dialog.dismiss();

                if (!ZipcodeLab.get().getZipcodePickUpDaysMap().containsKey(Integer.parseInt(newZipcode))) {
                    promptIfZipcodeIsNotInArea();
                }
            }
            else {
                Toast.makeText(this,
                        "Has to be in standard zipcode format (5 numbers)",
                        Toast.LENGTH_SHORT).show();
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    private void promptIfZipcodeIsNotInArea() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Invalid Buffalo Zipcode")
                .setMessage("Your zipcode is not a valid city of Buffalo zipcode." +
                        " If you would like to know your curbside recycle date" +
                        " please change this by navigating to the User Profile page. Otherwise," +
                        " happy recycling!")
                .setNeutralButton("Ok", (dialogInterface, which) -> {
                          dialogInterface.dismiss();
                })
                .create();

        dialog.show();
    }
}
