package com.arlahiru.tsart.ocr;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.arlahiru.tsart.AppConstants;
import com.arlahiru.tsart.AugmentedTextBox;
import com.arlahiru.tsart.FocusBoxUtils;
import com.arlahiru.tsart.MainActivity;
import com.arlahiru.tsart.TaSinlatorFacade;
import com.arlahiru.tsart.TaSinlatorFacadeTaskParams;
import com.arlahiru.tsart.Tools;
import com.arlahiru.tsart.overlay.kmean.BgFgColorExtraction;
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

    public String recognizeTexts(byte[] inputData){
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(AppConstants.DATA_PATH, lang);

        //blacklist garbage characters
        //baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "");
        Bitmap sceneImage = BitmapFactory.decodeByteArray(inputData, 0, inputData.length, null);
        baseApi.setImage(sceneImage);

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

    public List<AugmentedTextBox> recognizeTextsAndFgBgColor(byte[] inputData,List<Rect> boundingBoxes,Point CamRes,Point ScrRes){

        List<AugmentedTextBox> augmentedTextBoxList = new ArrayList<AugmentedTextBox>(0);
        BgFgColorExtraction bgFgColorExtraction = new BgFgColorExtraction();
        Bitmap originalSceneImage = BitmapFactory.decodeByteArray(inputData, 0, inputData.length, null);
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        //baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "");
        baseApi.init(AppConstants.DATA_PATH, lang);
        //blacklist garbage characters
        for(Rect rect: boundingBoxes) {
            Bitmap textRegion = Tools.getTextBoxBitmap(CamRes, ScrRes, inputData, rect);
            if(textRegion != null) {
                baseApi.setImage(textRegion);
                String recognizedText = baseApi.getUTF8Text();
                Log.d(TAG, "OCRED TEXT: " + recognizedText);
                // You now have the text in recognizedText var, you can do anything with it.
                // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
                // so that garbage doesn't make it to the display.
                //remove garbage chars
                //recognizedText = recognizedText.replaceAll("[\\]\\[\']", "");
                if (recognizedText != null && !recognizedText.trim().isEmpty()) {
                    AugmentedTextBox augBox = new AugmentedTextBox(rect, recognizedText, null);
                    //set text bg and fg colors
                    bgFgColorExtraction.setBgAndFgColors(originalSceneImage, textRegion, augBox);
                    augmentedTextBoxList.add(augBox);
                }
            }
        }
        baseApi.end();
        return augmentedTextBoxList;
    }

    public List<AugmentedTextBox> recognizeTextsAndFgBgColorWithoutSWT(byte[] inputData,List<Rect> boundingBoxes,Point CamRes,Point ScrRes){

        List<AugmentedTextBox> augmentedTextBoxList = new ArrayList<AugmentedTextBox>(0);
        BgFgColorExtraction bgFgColorExtraction = new BgFgColorExtraction();
        Bitmap originalSceneImage = BitmapFactory.decodeByteArray(inputData, 0, inputData.length, null);
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        //baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "");
        baseApi.init(AppConstants.DATA_PATH, lang);
        //blacklist garbage characters
        for(Rect rect: boundingBoxes) {
            baseApi.setImage(originalSceneImage);
            String recognizedText = baseApi.getUTF8Text();
            Log.d(TAG, "OCRED TEXT: " + recognizedText);
            // You now have the text in recognizedText var, you can do anything with it.
            // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
            // so that garbage doesn't make it to the display.
            //remove garbage chars
            //recognizedText = recognizedText.replaceAll("[\\]\\[\']", "");
            if(recognizedText != null && !recognizedText.trim().isEmpty()) {
                AugmentedTextBox augBox = new AugmentedTextBox(rect, recognizedText, null);
                Bitmap textRegion = Tools.getTextBoxBitmap(CamRes, ScrRes, inputData, rect);
                //set text bg and fg colors
                bgFgColorExtraction.setBgAndFgColors(originalSceneImage, textRegion, augBox);
                augmentedTextBoxList.add(augBox);
            }
        }
        baseApi.end();
        return augmentedTextBoxList;
    }

}
