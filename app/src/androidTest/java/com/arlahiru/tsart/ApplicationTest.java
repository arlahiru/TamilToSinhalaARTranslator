package com.arlahiru.tsart;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.arlahiru.tsart.overlay.kmean.Cluster;
import com.arlahiru.tsart.overlay.kmean.KMeans;

import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @SmallTest
    public void testEqualSignInString(){
        String tst="dfgdfg=dfgdfg";
        assertTrue(tst.contains("="));
    }

    @SmallTest
    public void testKMean() {
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image3);
        int sw= (int)(bitmap.getWidth()*0.25);
        int sh= (int)(bitmap.getHeight()*0.25);
        Bitmap sbitmap = Tools.createScaledBitmap(bitmap, sw, sh, Tools.ScalingLogic.CROP);
        KMeans kmeans = new KMeans(sbitmap);
        kmeans.init();
        List<Cluster> clusters =kmeans.calculate();
        for(Cluster c: clusters){
            Log.d("ApplicationTest",c.getCentroid().toString());
        }
        assertNotNull(clusters);
    }
}