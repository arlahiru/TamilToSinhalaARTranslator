package com.arlahiru.tsart.overlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.arlahiru.tsart.AugmentedTextBox;
import com.arlahiru.tsart.util.ImageUtil;

import java.util.List;

/**
 * Created by lahiru on 12/26/15.
 */
public class ArOverlayService {

    public Bitmap getOverlaidImage(Bitmap originalImage,List<AugmentedTextBox> augmentedTextBoxList){

        Bitmap mutableBitmap = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        //draw text boxes on the translated image
        for(AugmentedTextBox box: augmentedTextBoxList){
            Bitmap overlayBox = ImageUtil.getOverlayTextBox(box);
            canvas.drawBitmap(overlayBox,box.getTextLocation().left,box.getTextLocation().top,null);
        }
        return mutableBitmap;
    }

}
