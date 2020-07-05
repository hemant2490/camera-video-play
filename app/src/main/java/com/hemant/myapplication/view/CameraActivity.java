package com.hemant.myapplication.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hemant.myapplication.R;
import com.hemant.myapplication.databinding.ActivityCameraBinding;
import com.hemant.myapplication.view.fragment.CameraFragment;
import com.hemant.myapplication.view.fragment.VideoPlayFragment;


public class CameraActivity extends AppCompatActivity implements CameraFragment.onStopEventListener {

    private static final String TAG = CameraActivity.class.getCanonicalName();
    ActivityCameraBinding activityCameraBinding;

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    public int duration = 0;
    public CameraFragment cameraFragment = new CameraFragment();
    public VideoPlayFragment videoFragment = new VideoPlayFragment();
    String status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCameraBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera);

        if (getIntent().hasExtra("duration")) {
            duration = Integer.parseInt(getIntent().getStringExtra("duration"));
        }

        try {
            getSupportActionBar().hide();
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            if (null == savedInstanceState) {
                setFragment();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setFragment() {

        if (status.equalsIgnoreCase("stop")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, videoFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, cameraFragment).commit();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(CameraActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");

    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void stopEvent(String s) {
        Log.e(TAG, "stopEvent: " + s);
        status = s;
        setFragment();
    }
}