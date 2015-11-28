package com.arlahiru.tsart;

import android.content.res.AssetManager;
import android.util.Log;

import com.arlahiru.tsart.ocr.TesseractOCRService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lahiru on 10/30/15.
 */
public class Initializer {

    private static final String TAG = "Initializer";

    private MainActivity mainActivity;

    public Initializer(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void initialize(){
        initTessData();
        initCacheFile();
    }

    private void initTessData(){
        String[] paths = new String[] { AppConstants.DATA_PATH, AppConstants.DATA_PATH + "tessdata/" };

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.d(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.d(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }

        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization
        if (!(new File(AppConstants.DATA_PATH + "tessdata/" + TesseractOCRService.lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = mainActivity.getAssets();
                InputStream in = assetManager.open("tessdata/" + TesseractOCRService.lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(AppConstants.DATA_PATH
                        + "tessdata/" + TesseractOCRService.lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.d(TAG, "Copied " + TesseractOCRService.lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + TesseractOCRService.lang + " traineddata " + e.toString());
            }
        }
    }

    private void initCacheFile(){
        try{
            String[] paths = new String[] { AppConstants.DATA_PATH, AppConstants.DATA_PATH + "cache/" };
            for (String path : paths) {
                File dir = new File(path);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        Log.d(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                        return;
                    } else {
                        Log.d(TAG, "Created directory " + path + " on sdcard");
                    }
                }

            }
            File cacheFile = new File(AppConstants.DATA_PATH + "cache/cache.txt");
            if(!cacheFile.exists()){
                cacheFile.createNewFile();
            }
        }catch(Exception e){
            Log.e(TAG, "Was unable to create " + AppConstants.DATA_PATH + "cache/cache.txt" + e.toString());
        }
    }

}
