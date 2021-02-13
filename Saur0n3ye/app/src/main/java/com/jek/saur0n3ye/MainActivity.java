package com.jek.saur0n3ye;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity implements
        CameraBridgeViewBase.CvCameraViewListener2 {

    // camera ind 0: back cam; camera ind 1: selfie cam
    private int                     currentCamera = 0;

    private CameraBridgeViewBase    cameraBridgeViewBase;
    private BaseLoaderCallback      baseLoaderCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // remove UI elements
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);  // remove title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // remove notification bar

        setContentView(R.layout.activity_main);

        OpenCVLoader.initDebug(); // start opencv

        //  Request Camera permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this, Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(
                        MainActivity.this,new String[]{Manifest.permission.CAMERA},1);
            }
        }

        // start camera bridge view base
        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.CameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        cameraBridgeViewBase.setCameraIndex(currentCamera);
        cameraBridgeViewBase.setMaxFrameSize(AppUtils.frameW + 1, AppUtils.frameH + 1);

        // set up base loader
        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);
                switch(status){
                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        cameraBridgeViewBase.enableFpsMeter();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat baseFrame = inputFrame.rgba();

        return baseFrame;
    }

    @Override protected void onResume() { super.onResume();
        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"Load Failed!", Toast.LENGTH_SHORT).show();
        } else { baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS); } }
    @Override protected void onPause() { super.onPause();
        if (cameraBridgeViewBase != null){ cameraBridgeViewBase.disableView(); } }
    @Override protected void onDestroy() { super.onDestroy();
        if (cameraBridgeViewBase != null){ cameraBridgeViewBase.disableView(); } }
    @Override public void onCameraViewStarted(int width, int height) { }
    @Override public void onCameraViewStopped() { }




}
