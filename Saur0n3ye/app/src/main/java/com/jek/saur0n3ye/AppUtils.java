package com.jek.saur0n3ye;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.ArrayList;

public class AppUtils {
    // CONSTANTS

    public static final Scalar GREEN  = new Scalar(0,    255,    0,      100);
    public static final Scalar RED    = new Scalar(255,  0,      0,      100);
    public static final Scalar BLUE   = new Scalar(0,    0,      255,    100);
    public static final Scalar YELLOW = new Scalar(255,  255,    0,      100);
    public static final Scalar WHITE  = new Scalar(255,  255,    255,    100);
    public static final Scalar BLACK  = new Scalar(0,    0,      0,      100);
    public static final Scalar PURPLE = new Scalar(148,  0,      211,    100);

    public static final int frameW = 640 - 1;
    public static final int frameH = 360 - 1;

    private static Mat blank = new Mat( AppUtils.frameH+1, AppUtils.frameW+1,
            CvType.CV_8UC4, Scalar.all(0));

    public static Mat getBlankFrame(){ return blank.clone(); }

    public static Mat getAlphaMask(Mat in){
        ArrayList<Mat> mChannels = new ArrayList<>();
        Core.split(in, mChannels);
        return mChannels.get(3);
    }

}
