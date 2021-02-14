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
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        CameraBridgeViewBase.CvCameraViewListener2 {

    // camera ind 0: back cam; camera ind 1: selfie cam
    private int                     currentCamera = 0;
    private int                     refreshRate = 15;
    private int                     frame_idx;

    private CameraBridgeViewBase    cameraBridgeViewBase;
    private BaseLoaderCallback      baseLoaderCallback;

    private Mat                     processingFrame;
    private Mat                     canvas;

    private ArrayList<BookSpine>    books;

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

        processingFrame = new Mat();
        canvas          = AppUtils.getBlankFrame();
        books           = new ArrayList();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        frame_idx ++;

        Mat baseFrame = inputFrame.rgba();

        if (frame_idx % refreshRate == 0){

            Imgproc.cvtColor(baseFrame, processingFrame, Imgproc.COLOR_RGBA2GRAY);
            Imgproc.equalizeHist(processingFrame, processingFrame);

            // TODO (BIANCA): apply the spining algorithm here to generate all the spine rectangles

            books.clear();
            for (int i = 0; i < 5; i++){
                Rect dummySpineRect = new Rect( new Point(100 + 60*i,100),
                                                new Point(150 + 60*i,300));
                books.add(new BookSpine(dummySpineRect, baseFrame.submat(dummySpineRect)));
            }

        }

        //wait for ocr to finish before drawing
        if (frame_idx % refreshRate == (int)(refreshRate/2)) {
            canvas.release();
            canvas = AppUtils.getBlankFrame();
            int step = 10;
            int delta = step;
            for (BookSpine bs : books) {
                bs.draw(canvas, (delta -= step));
            }
        }
        canvas.copyTo(baseFrame, AppUtils.getAlphaMask(canvas));

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
