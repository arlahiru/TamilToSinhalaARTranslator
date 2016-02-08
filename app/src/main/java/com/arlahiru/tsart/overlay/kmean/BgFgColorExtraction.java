package com.arlahiru.tsart.overlay.kmean;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;

import com.arlahiru.tsart.AugmentedTextBox;
import com.arlahiru.tsart.overlay.kmean.Cluster;
import com.arlahiru.tsart.overlay.kmean.KMeans;
import com.arlahiru.tsart.overlay.kmean.Pixel;

import java.sql.SQLOutput;
import java.util.List;

/**
 * Created by lahiru on 12/26/15.
 */
public class BgFgColorExtraction {

    public void setBgAndFgColors(Bitmap originalImage, Bitmap textRegion, AugmentedTextBox box){

        KMeans kmeans = new KMeans(textRegion);
        kmeans.init();
        List<Cluster> clusters =kmeans.calculate();
        System.out.println("Cluster size = " + clusters.size());

        //we get pixel from orginal image which is 2 points outside the text region and assume its a bg pixel. are we lucky???
        Rect textRect = box.getTextLocation();
        try {
            int orginalBgColor = originalImage.getPixel(textRect.left - 2, textRect.top - 2);
            Pixel originalBgPixel = new Pixel(Color.red(orginalBgColor),Color.green(orginalBgColor),Color.blue(orginalBgColor));

            Cluster cluster1 = clusters.get(0);
            Cluster cluster2 = clusters.get(1);

            double distance1 = Pixel.distance(cluster1.getCentroid(),originalBgPixel);
            double distance2 = Pixel.distance(cluster2.getCentroid(), originalBgPixel);

            if(distance1<distance2) {
                box.setBackgroundColor(cluster1.getCentroid());
                box.setForegroundColor(cluster2.getCentroid());
            }else {
                box.setForegroundColor(cluster1.getCentroid());
                box.setBackgroundColor(cluster2.getCentroid());
            }
        }catch (IllegalArgumentException e){
            //x,y pixels are invalid
            e.printStackTrace();
            box.setBackgroundColor(new Pixel(255,255, 255));
            box.setForegroundColor(new Pixel(0, 0, 0));
        }
    }


}
