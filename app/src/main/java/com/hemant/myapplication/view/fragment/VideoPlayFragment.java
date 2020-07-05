package com.hemant.myapplication.view.fragment;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.hemant.myapplication.R;
import com.hemant.myapplication.databinding.FragmentVideoBinding;
import com.hemant.myapplication.utils.Utils;
import com.hemant.myapplication.view.CameraActivity;

public class VideoPlayFragment extends Fragment {
    private static final String TAG = VideoPlayFragment.class.getCanonicalName();
    FragmentVideoBinding videoplaybinding;
    private String mOutputFilePath;
    CameraActivity cameraActivity;

    int Measuredwidth = 0;
    int Measuredheight = 0;

    public VideoPlayFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getScreenWidthHeight();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        videoplaybinding = DataBindingUtil.inflate(inflater, R.layout.fragment_video, container, false);

        mOutputFilePath = cameraActivity.cameraFragment.mOutputFilePath;

        Log.e("TAG", "onCreateView VideoPlayFragment: " + mOutputFilePath);

        prepareViews();
        videoplaybinding.mVideoView.start();


        boolean isFilePresent = Utils.isFilePresent(cameraActivity, "storage.json");
        if (isFilePresent) {
            String jsonString = Utils.read(cameraActivity, "storage.json");
            Log.e(TAG, "onCreate: " + jsonString);
            //do the json parsing here and do the rest of functionality of app
            videoplaybinding.tvJson.setText(jsonString);
        }

        return videoplaybinding.getRoot();
    }

    private void prepareViews() {
        videoplaybinding.mVideoView.setVisibility(View.VISIBLE);
//        videoplaybinding.mPlayVideo.setVisibility(View.VISIBLE);
        setMediaForRecordVideo();
    }

    private void setMediaForRecordVideo() {
        // Set media controller
        videoplaybinding.mVideoView.setMediaController(new MediaController(getActivity()));
        videoplaybinding.mVideoView.requestFocus();
        videoplaybinding.mVideoView.setVideoPath(mOutputFilePath);
        videoplaybinding.mVideoView.seekTo(100);
        videoplaybinding.mVideoView.setLayoutParams(new RelativeLayout.LayoutParams(Measuredwidth, Measuredheight/2));
        videoplaybinding.mVideoView.setOnCompletionListener(mp -> {
            // Reset player
            /*videoplaybinding.mVideoView.setVisibility(View.GONE);
            videoplaybinding.mPlayVideo.setVisibility(View.GONE);*/
        });
    }

    public void getScreenWidthHeight() {
        Point size = new Point();
        WindowManager w = cameraActivity.getWindowManager();

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        cameraActivity = (CameraActivity) context;
    }
}
