package com.jek.saur0n3ye;

public class AppUtils {
    // CONSTANTS

    public static final int frameW = 640 - 1;
    public static final int frameH = 360 - 1;


    public static double clamp (double in, double min, double max){
        return (in < min) ? min : (in > max) ? max : in;
    }

}
