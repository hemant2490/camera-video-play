package com.hemant.myapplication.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.hemant.myapplication.R;
import com.hemant.myapplication.databinding.FragmentCameraBinding;
import com.hemant.myapplication.view.CameraActivity;


public class CameraFragment extends CameraVideoFragment {
    private static final String TAG = CameraFragment.class.getCanonicalName();
    FragmentCameraBinding binding;
    public String mOutputFilePath;

    CameraActivity cameraActivity;
    long durationInMills;

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false);

        try {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startRecordingVideo();

                    try {
                        //Receive out put file here
                        mOutputFilePath = getCurrentFile().getAbsolutePath();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, 1000);


        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("TAG", "onCreateView: " + cameraActivity.duration);
        durationInMills = (cameraActivity.duration + 2) * 1000;
        new CountDownTimer(durationInMills, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.mRecordVideo.setText((int) (millisUntilFinished / 1000) + "sec left");

                Log.e(TAG, "onTick: " + millisUntilFinished);
            }

            @Override
            public void onFinish() {

//                Log.e(TAG, "onFinish: "+mIsRecordingVideo);
//                if (mIsRecordingVideo) {
                try {
                    stopRecordingVideo();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                stopEventListener.stopEvent("stop");
//                }
            }
        }.start();


        return binding.getRoot();
    }

    @Override
    public int getTextureResource() {
        return R.id.mTextureView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public interface onStopEventListener {
        public void stopEvent(String s);
    }

    onStopEventListener stopEventListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            stopEventListener = (onStopEventListener) context;
            cameraActivity = (CameraActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onStopEventListener");
        }
    }

}
