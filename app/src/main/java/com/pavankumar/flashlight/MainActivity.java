package com.pavankumar.flashlight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button FlashLight;
    private final int CAMERA_REQUEST_CODE=2;
    boolean hasCameraFlash = false;
    private boolean isFlashOn=false;
    private  TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getBatteryPercentage();


        hasCameraFlash = getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        FlashLight =  (Button)findViewById(R.id.Flash);
        t1=(TextView)findViewById(R.id.tv);

        FlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermission(Manifest.permission.CAMERA,CAMERA_REQUEST_CODE);

            }
        });
    }

    private void flashLight() {
        if (hasCameraFlash) {
            if (isFlashOn) {
                FlashLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.poweron));
                flashLightOff();
                isFlashOn=false;
            } else {
                FlashLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.poweroff));
                flashLightOn();
                isFlashOn=true;
            }
        } else {
            Toast.makeText(MainActivity.this, "No flash available on your device",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
        }
    }

    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
        }
    }

    private void askPermission(String permission,int requestCode) {
        if (ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
            // We Dont have permission
            ActivityCompat.requestPermissions(this,new String[]{permission},requestCode);

        }else {
            // We already have permission do what you want
            flashLight();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasCameraFlash = getPackageManager().
                            hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                    Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show();
                    flashLight();

                } else {
                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
                }
                break;
        }




    }




    private void getBatteryPercentage() {


        BroadcastReceiver batteryLevel = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level= -1;
                if (currentLevel >= 0 && scale > 0) {
                    level = (currentLevel * 100) / scale;
                }
                t1.setText("Battery Percentage "+level + "%");
            }
        };

        IntentFilter batteryLevelFilter = new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevel, batteryLevelFilter);

    }




}
