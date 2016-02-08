package com.arlahiru.tsart.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.arlahiru.tsart.AugmentedTextBox;

/**
 * Created by lahiru on 11/2/15.
 */
public class ImageUtil {

    public static Bitmap getTextImage(String text, int width, int height) {
        final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Paint paint = new Paint();
        final Canvas canvas = new Canvas(bmp);

        canvas.drawColor(Color.WHITE);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(48.0f);
        canvas.drawText(text, width / 2, height / 2, paint);

        return bmp;
    }

    public static Bitmap getOverlayTextBox(AugmentedTextBox box) {
        int width = 0;
        if(box.getTargetText().length() > box.getSourceText().length()){
            int letterWidth = box.getTextLocation().width()/box.getSourceText().length();
            width = letterWidth*box.getTargetText().length();
        }else{
            width = box.getTextLocation().width();
        }
        final Bitmap bmp = Bitmap.createBitmap(width, box.getTextLocation().height(), Bitmap.Config.ARGB_8888);
        final Paint paint = new Paint();
        final Canvas canvas = new Canvas(bmp);
        //set bg color
        canvas.drawColor(Color.rgb(box.getBackgroundColor().getR(), box.getBackgroundColor().getG(), box.getBackgroundColor().getB()));
        //set fg color
        paint.setColor(Color.rgb(box.getForegroundColor().getR(), box.getForegroundColor().getG(), box.getForegroundColor().getB()));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(box.getBoxHeight());
        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(box.getTargetText(), 0, box.getTargetText().length(), bounds);
        int x = 0; //(bmp.getWidth() - bounds.width())/2;
        int y = (bmp.getHeight() + bounds.height())/2;

        canvas.drawText(box.getTargetText(), x, y, paint);

        return bmp;

    }
}
