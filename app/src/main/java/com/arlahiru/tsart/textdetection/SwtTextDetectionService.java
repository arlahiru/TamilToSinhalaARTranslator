package com.arlahiru.tsart.textdetection;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.arlahiru.tsart.MainActivity;

/**
 * Created by lahiru on 11/29/15.
 */
public class SwtTextDetectionService {

    private static final String TAG = "SwtTextDetectionService";

    private MainActivity mainActivity;

    public SwtTextDetectionService(MainActivity mainActivity) {

        this.mainActivity = mainActivity;
    }

    public Bitmap getInputImageWithBoundingBoxes(String imagePath){

        String[] boundingBoxes = mainActivity.ccvSwtDetectwords(imagePath);
        Bitmap bmp=BitmapFactory.decodeFile(imagePath);
        Bitmap mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Canvas cnvs=new Canvas(mutableBitmap);
        Paint paint=new Paint();
        paint.setColor(Color.RED);
        cnvs.drawBitmap(mutableBitmap, 0, 0, null);
        for(String sbox: boundingBoxes){
            int[] ibox = convertStringCordinatesToIntArray(sbox);
            if(ibox != null){
                //topx, topy, bottomx, bottomy
                cnvs.drawRect(ibox[0], ibox[1],ibox[0]+ibox[2],ibox[1]+ibox[3], paint);
            }
        }
        return mutableBitmap;
    }

    private int[] convertStringCordinatesToIntArray(String sbox){

        int[] ibox = null;
        String[] values = sbox.split(",");
        if(values.length == 4) {
            ibox = new int[4];
            ibox[0] = Integer.valueOf(values[0]);
            ibox[1] = Integer.valueOf(values[1]);
            ibox[2] = Integer.valueOf(values[2]);
            ibox[3] = Integer.valueOf(values[3]);
        }
        return ibox;
    }
}
