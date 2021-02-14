package com.jek.saur0n3ye;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.rectangle;

public class BookSpine {

    private Rect   location;
    private Mat    slice;
    private String info;

    public BookSpine (Rect location, Mat slice) {
        this.location = location;
        this.slice = slice;
        this.extractInfo();
    }

    // TODO (AUSTIN): apply ocr on this.slice and write result into this.info
    public void extractInfo(){
        this.info = "This is a book!";
    }

    public void draw(Mat dest) {

        rectangle( dest, this.location.tl(),this.location.br(),AppUtils.RED, 2);
        Imgproc.putText(dest, info,
                new Point(this.location.tl().x,this.location.tl().y-5),
                Core.FONT_HERSHEY_COMPLEX, 0.25, AppUtils.WHITE);

    }

}
