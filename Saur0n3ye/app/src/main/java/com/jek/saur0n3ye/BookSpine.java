package com.jek.saur0n3ye;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.CvType.CV_8UC4;
import static org.opencv.imgproc.Imgproc.rectangle;

import android.graphics.Bitmap;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;


public class BookSpine {

    public Rect   location;
    private Mat    slice;
    private String info;

    public BookSpine (Rect location, Mat slice) {
        this.location = location;
        this.slice = slice;
        this.info = "asdfasdfasd";
        this.extractInfo();
    }

    // TODO (AUSTIN): apply ocr on this.slice and write result into this.info
    public void extractInfo(){
        text_from_image(this.slice);
        Mat rotated = new Mat();
        Core.rotate(this.slice, rotated, Core.ROTATE_90_COUNTERCLOCKWISE);
        text_from_image(rotated);
    }

    public void text_from_image(Mat crop){
        TextRecognizer recognizer = TextRecognition.getClient();

        crop.convertTo(crop, CV_8UC4);
        Bitmap temp = Bitmap.createBitmap(crop.cols(), crop.rows(), Bitmap.Config.RGB_565);

        Utils.matToBitmap(crop, temp);
        InputImage image = InputImage.fromBitmap(temp, 0);

        recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
                        String concat = "";
                        for (Text.TextBlock block : visionText.getTextBlocks()) {
                            concat += " "+block.getText();
                        }
                        set_text(concat);
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("ERROR", "OCR Failed.");
                            }
                        });
    }

    public void set_text(String text){
       this.info += text;
   }

    public void draw(Mat dest, int delta) {

        rectangle( dest, this.location.tl(),this.location.br(),AppUtils.RED, 2);
        Imgproc.putText(dest, this.info,
                new Point(this.location.tl().x,this.location.tl().y-5+delta),
                Core.FONT_HERSHEY_COMPLEX, 0.35, AppUtils.WHITE);

    }

}
