package com.arlahiru.tsart.textdetection;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.arlahiru.tsart.MainActivity;

import java.util.ArrayList;
import java.util.List;

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
        Log.i(TAG, imagePath + " : " + boundingBoxes);
        for(String sbox: boundingBoxes){
            int[] ibox = convertStringCordinatesToIntArray(sbox);
            if(ibox != null){
                //topx, topy, bottomx, bottomy - adjust topy with 10 points upward to detect dots in the tamil text
                cnvs.drawRect(ibox[0], ibox[1]-15,ibox[0]+ibox[2],ibox[1]+ibox[3]+15, paint);
            }
        }
        return mutableBitmap;
    }

    public List<Rect> getTextLocations(String imagePath){

        List<Rect> textLocationList = new ArrayList<Rect>(0);
        String[] boundingBoxes = mainActivity.ccvSwtDetectwords(imagePath);
        for(String sbox: boundingBoxes){
            int[] box = convertStringCordinatesToIntArray(sbox);
            //topx, topy, bottomx, bottomy
            Rect rect = new Rect(box[0],box[1]-15,box[0]+box[2],box[1]+box[3]+15);
            textLocationList.add(rect);

        }
        return textLocationList;
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
