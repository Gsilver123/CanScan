package com.example.canscan;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
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

        initializeViewVariables();
        setOnClickListeners();

        setBarcodeDetector();
        setCameraSource();

        requestPermissionForCamera();
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
                initializeCameraSource(this);
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
                mCameraSource.release();
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
                finish();
            }
            else {
                Log.d(BARCODE, "Barcode didn't scan");
                Toast.makeText(this,
                        "Barcode didn't scan properly. Please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
