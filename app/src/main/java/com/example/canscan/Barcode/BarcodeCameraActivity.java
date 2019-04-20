package com.example.canscan.Barcode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.canscan.R;
import com.example.canscan.User.UserLab;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class BarcodeCameraActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String BARCODE = "Barcode";
    private static int MY_CAMERA_PERMISSIONS;

    private SurfaceView mCameraView;
    private ImageButton mTakePictureImageButton;
    private ImageButton mBackImageButton;

    private CameraSource mCameraSource;
    private BarcodeDetector mBarcodeDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_camera_scanner);

        requestPermissionForCamera();

        initializeViewVariables();
        setOnClickListeners();

        setBarcodeDetector();
        setCameraSource();

        initializeBarcodeDetector();
        initializeCameraSource(this);
    }

    private void initializeViewVariables() {
        mCameraView = findViewById(R.id.camera_surfaceView);
        mTakePictureImageButton = findViewById(R.id.take_picture_btn);
        mBackImageButton = findViewById(R.id.back_btn);
    }

    private void setOnClickListeners() {
        mTakePictureImageButton.setOnClickListener(this);
        mBackImageButton.setOnClickListener(this);
    }

    private void setBarcodeDetector() {
        mBarcodeDetector = new BarcodeDetector
                .Builder(this)
                .setBarcodeFormats(Barcode.EAN_8
                        | Barcode.EAN_13
                        | Barcode.CODE_39
                        | Barcode.CODE_93
                        | Barcode.CODE_128
                        | Barcode.CODABAR
                        | Barcode.ITF
                        | Barcode.UPC_A
                        | Barcode.UPC_E)
                .build();
    }

    private void setCameraSource() {
        Display display = ((WindowManager)
                getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        mCameraSource = new CameraSource
                .Builder(this, mBarcodeDetector)
                .setRequestedPreviewSize(metrics.heightPixels, metrics.widthPixels)
                .setAutoFocusEnabled(true)
                .build();
    }

    private void initializeBarcodeDetector() {
        mBarcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

            }
        });
    }

    private void requestPermissionForCamera() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA},
                    MY_CAMERA_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (MY_CAMERA_PERMISSIONS == requestCode) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }

    private void initializeCameraSource(Context context) {
        mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mCameraSource.start(mCameraView.getHolder());
                }
                catch (IOException exception) {
                    Log.d("Camera", exception.getLocalizedMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                try {
                    mCameraSource.release();
                }
                catch (NullPointerException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_picture_btn:
                takePicture();
                break;
            case R.id.back_btn:
                finish();
                break;
            default:
                break;
        }
    }

    private void takePicture() {
        mCameraSource.takePicture(null, bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Barcode> barcodeSparseArray = mBarcodeDetector.detect(frame);

            if (barcodeSparseArray.size() != 0) {
                Barcode barcode = barcodeSparseArray.valueAt(0);
                Log.d(BARCODE, barcode.displayValue);

                if (BarcodeLab.get().addBarcodeToList(barcode.displayValue)) {
                    addPointsFromBarcode(30);
                    finish();
                }
                else {
                    addPointsFromBarcode(3);

                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setMessage("Barcode already scanned")
                            .setNeutralButton("Ok", (dialogInterface, which) -> {
                                dialogInterface.dismiss();
                            })
                            .setOnDismissListener(dialogInterface -> {
                                finish();
                            })
                            .create();

                    dialog.show();
                }
            }
            else {
                Log.d(BARCODE, "Barcode didn't scan");

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setMessage("Barcode didn't scan properly. Please try again")
                        .setNeutralButton("Ok", (dialog1, which) -> {
                            dialog1.dismiss();
                        })
                        .create();

                dialog.show();
            }
        });
    }

    private void addPointsFromBarcode(int addedPoints) {
        UserLab.get().getCurrentUser()
                .setScore(UserLab.get().getCurrentUser().getScore() + addedPoints);
        UserLab.get().getCurrentUser()
                .setTotalScore(UserLab.get().getCurrentUser().getTotalScore() + addedPoints);
        BarcodeLab.get().updateDatabaseWithCurrentListAndPoints();
        UserLab.get().notifyDatabaseObserversUserUpdated(false);
    }
}
