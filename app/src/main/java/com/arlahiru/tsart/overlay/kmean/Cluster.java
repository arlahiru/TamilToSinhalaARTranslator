package com.arlahiru.tsart.overlay.kmean;


import java.util.ArrayList;
import java.util.List;
/**
 * Created by lahiru on 11/8/15.
 * Reference: http://www.dataonfocus.com/k-means-clustering-java-code/
 */


public class Cluster {

    public List<Pixel> pixels;
    public Pixel centroid;
    public int id;

    //Creates a new Cluster
    public Cluster(int id) {
        this.id = id;
        this.pixels = new ArrayList();
        this.centroid = null;
    }

    public List getPixels() {
        return pixels;
    }

    public void addPixel(Pixel pixel) {
        pixels.add(pixel);
    }

    public void setPixels(List pixels) {
        this.pixels = pixels;
    }

    public Pixel getCentroid() {
        return centroid;
    }

    public void setCentroid(Pixel centroid) {
        this.centroid = centroid;
    }

    public int getId() {
        return id;
    }

    public void clear() {
        pixels.clear();
    }

    public void plotCluster() {
        System.out.println("[Cluster: " + id+"]");
        System.out.println("[Centroid: " + centroid + "]");
/*        System.out.println("[Points: \n");
        for(Pixel p: pixels) {
            System.out.println(p);
        }
        System.out.println("]");*/
    }

}
