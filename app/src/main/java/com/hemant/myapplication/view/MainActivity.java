package com.hemant.myapplication.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import com.google.gson.Gson;
import com.hemant.myapplication.utils.CountDownAnimation;
import com.hemant.myapplication.R;
import com.hemant.myapplication.utils.Utils;
import com.hemant.myapplication.databinding.ActivityMainBinding;
import com.hemant.myapplication.model.DataTime;

public class MainActivity extends AppCompatActivity implements CountDownAnimation.CountDownListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getCanonicalName();
    ActivityMainBinding activityMainBinding;
    private CountDownAnimation countDownAnimation;

    MediaPlayer mp;
    int Measuredwidth = 0;
    int Measuredheight = 0;

    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1003;
    private String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,};
    private boolean cameraAccepted, storageAccepted, record_audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.btnSubmit.setOnClickListener(this);

        getScreenWidthHeight();
    }

    private void initCountDownAnimation() {
        countDownAnimation = new CountDownAnimation(activityMainBinding.tvText, getStartCount());
        countDownAnimation.setCountDownListener(this);
    }

    private int getStartCount() {
        return Integer.parseInt(activityMainBinding.etDelay.getText().toString());
    }

    private void startCountDownAnimation() {
        // Customizable animation
        // Use a set of animations
        Animation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f,
                0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        countDownAnimation.setAnimation(animationSet);

        // Customizable start count
        countDownAnimation.setStartCount(getStartCount());

        countDownAnimation.start();
    }

    @Override
    public void onCountDownEnd(CountDownAnimation animation) {

        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        intent.putExtra("duration", activityMainBinding.etDuration.getText().toString());
        startActivity(intent);

        try {
            stopSound();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCountDown(CountDownAnimation animation, int mCount) {
        Log.e(TAG, "onCountDown: " + mCount);

        if (mCount < 4) {
            try {
                stopSound();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                new Runnable() {
                    @Override
                    public void run() {
                        playSound();
                    }

                    public void playSound() {
                        mp = MediaPlayer.create(MainActivity.this, (R.raw.beep_sound));
                        mp.start();
                    }
                }.run();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void stopSound() {
        if (mp != null && mp.isPlaying()) {
            mp.release();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                submit();
                break;
        }
    }

    public void submit() {
        if (!Utils.validateValue(activityMainBinding.etDelay, getResources().getString(R.string.fill_delay))) {
            return;
        } else if (!Utils.validateValue(activityMainBinding.etDuration, getResources().getString(R.string.fill_duration))) {
            return;
        } else {
            Utils.hideKeyboard(MainActivity.this);

            activityMainBinding.llMain.setVisibility(View.GONE);

            initCountDownAnimation();
            startCountDownAnimation();

            saveDataIntoFile();
        }
    }

    public void saveDataIntoFile() {
        boolean isFileCreated = Utils.create(MainActivity.this, "storage.json", convertDataIntoJson());
        if (isFileCreated) {
            //proceed with storing the data  or show ui
            Log.e(TAG, "saveDataIntoFile: Save Data Successfully");
//            Utils.showToast(MainActivity.this, "Save Data Successfully");
        } else {
            //show error or try again.
        }
    }

    public String convertDataIntoJson() {
        /*Either use commented code or below uncommented working same*/

        /*Map<String, String> data = new HashMap<>();
        data.put("delay", activityMainBinding.etDelay.getText().toString());
        data.put("duration", activityMainBinding.etDuration.getText().toString());
        data.put("date_time", activityMainBinding.etDelay.getText().toString());
        data.put("width", activityMainBinding.etDelay.getText().toString());
        data.put("height", activityMainBinding.etDelay.getText().toString());

        JSONObject jsonData = new JSONObject(data);
        return jsonData.toString();*/


        DataTime dataTime = new DataTime();
        dataTime.setDelay(activityMainBinding.etDelay.getText().toString());
        dataTime.setDuration(activityMainBinding.etDuration.getText().toString());
        dataTime.setDate(Utils.getCurrentTimeStamp());
        dataTime.setWidth("" + Measuredwidth);
        dataTime.setHeight("" + Measuredheight);

        Gson gson = new Gson();
        return gson.toJson(dataTime);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");

        if (!hasPermissions(MainActivity.this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        } else {
//            handler.postDelayed(runnable, SPLASH_TIME_OUT);
        }

        activityMainBinding.llMain.setVisibility(View.VISIBLE);
    }

    public void getScreenWidthHeight() {
        Point size = new Point();
        WindowManager w = getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            w.getDefaultDisplay().getSize(size);
            Measuredwidth = size.x;
            Measuredheight = size.y;
        } else {
            Display d = w.getDefaultDisplay();
            Measuredwidth = d.getWidth();
            Measuredheight = d.getHeight();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                try {
                    cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    record_audio = grantResults[3] == PackageManager.PERMISSION_GRANTED;

//                    handler.postDelayed(runnable, 0);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}