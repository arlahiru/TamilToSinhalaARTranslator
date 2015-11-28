package com.arlahiru.tsart.ocr;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.arlahiru.tsart.AppConstants;
import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by lahiru on 10/28/15.
 */
public class TesseractOCRService {

    private static final String TAG = "TesseractOCRService";
    // You should have the trained data file in assets folder
    // You can get them at:
    // http://code.google.com/p/tesseract-ocr/downloads/list
    public static final String lang = "tam";

    public TesseractOCRService() {
    }

    public String recognizeTexts(Bitmap bitmap){
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(AppConstants.DATA_PATH, lang);

        //blacklist garbage characters
        //baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "");
        baseApi.setImage(bitmap);

        String recognizedText = baseApi.getUTF8Text();

        baseApi.end();

        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.

        Log.v(TAG, "OCRED TEXT: " + recognizedText);
        //remove garbage chars
        recognizedText = recognizedText.replaceAll("[\\]\\[\']", "");
        recognizedText = recognizedText.trim();
        return recognizedText;
    }
}
