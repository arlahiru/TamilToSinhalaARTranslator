package com.arlahiru.tsart.overlay.kmean;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lahiru on 11/8/15.
 *
 */

public class Pixel {

    private int r = 0;
    private int g = 0;
    private int b = 0;
    private int cluster_number = 0;

    public Pixel(int r, int g, int b)
    {
        this.r= r;
        this.g= g;
        this.b= b;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public void setR(int r) {
        this.r = r;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setB(int b) {
        this.b = b;
    }

    public void setCluster(int n) {
        this.cluster_number = n;
    }

    public int getCluster() {
        return this.cluster_number;
    }

    //Calculates the distance between two pixels(Euclidean distance)
    protected static double distance(Pixel p, Pixel centroid) {
        return Math.sqrt(Math.pow((centroid.r - p.r), 2) + Math.pow((centroid.g - p.g), 2)+ Math.pow((centroid.b - p.b), 2));
    }

    //Creates random pixel
    protected static Pixel createRandomPixel() {
        Random random = new Random();
        int r = (int)(255 * random.nextDouble());
        int g = (int)(255 * random.nextDouble());
        int b = (int)(255 * random.nextDouble());
        return new Pixel(r,g,b);
    }

    public String toString() {
        return "("+r+","+g+","+b+")";
    }
}
