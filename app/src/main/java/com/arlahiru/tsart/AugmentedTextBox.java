package com.arlahiru.tsart;

import android.graphics.Rect;

import com.arlahiru.tsart.overlay.kmean.Pixel;

/**
 * Created by lahiru on 12/26/15.
 */
public class AugmentedTextBox {

    private Rect textLocation;
    private String sourceText;
    private String targetText;
    private int boxHeight;
    private Pixel foregroundColor;
    private Pixel backgroundColor;

    public AugmentedTextBox(Rect textLocation, String sourceText, String targetText) {
        this.textLocation = textLocation;
        this.sourceText = sourceText;
        this.targetText = targetText;
        this.boxHeight = getHeight();
    }

    public String getTargetText() {
        return targetText;
    }

    public void setTargetText(String targetText) {
        this.targetText = targetText;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public Pixel getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(Pixel foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public Pixel getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Pixel backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Rect getTextLocation() {
        return textLocation;
    }

    public void setTextLocation(Rect textLocation) {
        this.textLocation = textLocation;
    }

    public int getBoxHeight() {
        return boxHeight;
    }

    private int getHeight(){
        //return height of the box
        return textLocation.height();
    }
}
