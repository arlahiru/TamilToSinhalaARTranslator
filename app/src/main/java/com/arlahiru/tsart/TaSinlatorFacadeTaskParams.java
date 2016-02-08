package com.arlahiru.tsart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by lahiru on 12/26/15.
 */
public class TaSinlatorFacadeTaskParams {

    private byte[] inputData;
    private Bitmap inputImage;
    private Point cameraRes;
    private Point screenRes;
    private String inputImagePath;
    private Bitmap outputImage;
    private String errorMessage;
    private boolean skipSWT;
    private boolean textDetectOnly;

    TaSinlatorFacadeTaskParams(byte[] inputData, Point cameraRes, Point screenRes, String inputImagePath, boolean skipSWT, boolean textDetectOnly) {
        this.inputData = inputData;
        this.cameraRes = cameraRes;
        this.screenRes = screenRes;
        this.inputImagePath = inputImagePath;
        this.inputImage = BitmapFactory.decodeByteArray(inputData, 0, inputData.length, null);
        this.skipSWT = skipSWT;
        this.textDetectOnly = textDetectOnly;
    }

    public byte[] getInputData() {
        return inputData;
    }

    public void setInputData(byte[] inputData) {
        this.inputData = inputData;
    }

    public Point getCameraRes() {
        return cameraRes;
    }

    public void setCameraRes(Point cameraRes) {
        this.cameraRes = cameraRes;
    }

    public Point getScreenRes() {
        return screenRes;
    }

    public void setScreenRes(Point screenRes) {
        this.screenRes = screenRes;
    }

    public String getInputImagePath() {
        return inputImagePath;
    }

    public void setInputImagePath(String inputImagePath) {
        this.inputImagePath = inputImagePath;
    }

    public Bitmap getOutputImage() {
        return outputImage;
    }

    public void setOutputImage(Bitmap outputImage) {
        this.outputImage = outputImage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Bitmap getInputImage() {
        return inputImage;
    }

    public boolean isSkipSWT() {
        return skipSWT;
    }

    public boolean isTextDetectOnly() {
        return textDetectOnly;
    }
}

