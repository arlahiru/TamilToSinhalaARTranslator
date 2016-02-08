package com.arlahiru.tsart.overlay.kmean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.arlahiru.tsart.MainActivity;
import com.arlahiru.tsart.R;

import java.util.ArrayList;
import java.util.List;

/*
 * KMeans.java ; Cluster.java ; Pixel.java
 *
 * Solution implemented by DataOnFocus
 * www.dataonfocus.com
 * 2015
 */

public class KMeans {

    //Number of Clusters. This metric should be related to the number of pixels
    private int NUM_CLUSTERS = 2;
    //Number of Points
    private int NUM_POINTS = 15;

    private List<Pixel> pixels;
    private List<Cluster> clusters;
    private Bitmap originalImage;

    public KMeans(Bitmap originalImage) {
        this.originalImage = originalImage;
        this.pixels = new ArrayList();
        this.clusters = new ArrayList();
    }

    public static void main(String[] args) {

    }

    //Initializes the process
    public void init() {
        //load pixels from the image here
        int w=originalImage.getWidth();
        int h=originalImage.getHeight();
        for(int i=0;i<w;i++)
            for(int j=0;j<h;j++){
                //color is a non-premultiplied ARGB value
                int color = originalImage.getPixel(i,j);
                int r=(color>>16) & 0xff;
                int g=(color>>8) & 0xff;
                int b=(color) & 0xff;
                Pixel newPixel = new Pixel(r,g,b);
                pixels.add(newPixel);
            }

        //Create Clusters
        //Set Random Centroids
        for (int i = 0; i < NUM_CLUSTERS; i++) {
            Cluster cluster = new Cluster(i);
            Pixel centroid = Pixel.createRandomPixel();
            cluster.setCentroid(centroid);
            clusters.add(cluster);
        }

        //Print Initial state
        //plotClusters();
    }

    private void plotClusters() {
        for (int i = 0; i < NUM_CLUSTERS; i++) {
            Cluster c = clusters.get(i);
            c.plotCluster();
        }
    }

    //The process to calculate the K Means, with iterating method.
    public List<Cluster> calculate() {
        boolean finish = false;
        int iteration = 0;

        // Add in new data, one at a time, recalculating centroids with each new one.
        while(!finish) {
            //Clear cluster state
            clearClusters();

            List<Pixel> lastCentroids = getCentroids();

            //Assign pixels to the closer cluster
            assignCluster();

            //Calculate new centroids.
            calculateCentroids();

            iteration++;

            List<Pixel> currentCentroids = getCentroids();

            //Calculates total distance between new and old Centroids
            double distance = 0;
            for(int i = 0; i < lastCentroids.size(); i++) {
                distance += Pixel.distance(lastCentroids.get(i), currentCentroids.get(i));
            }
/*            System.out.println("#################");
            System.out.println("Iteration: " + iteration);
            System.out.println("Centroid distances: " + distance);
            plotClusters();*/

            if(distance == 0) {
                finish = true;
            }
        }
        plotClusters();
        return clusters;
    }

    private void clearClusters() {
        for(Cluster cluster : clusters) {
            cluster.clear();
        }
    }

    private List getCentroids() {
        List centroids = new ArrayList(NUM_CLUSTERS);
        for(Cluster cluster : clusters) {
            Pixel aux = cluster.getCentroid();
            Pixel pixel = new Pixel(aux.getR(),aux.getG(),aux.getB());
            centroids.add(pixel);
        }
        return centroids;
    }

    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min = max;
        int cluster = 0;
        double distance = 0.0;

        for(Pixel pixel : pixels) {
            min = max;
            for(int i = 0; i < NUM_CLUSTERS; i++) {
                Cluster c = clusters.get(i);
                distance = Pixel.distance(pixel, c.getCentroid());
                if(distance < min){
                    min = distance;
                    cluster = i;
                }
            }
            pixel.setCluster(cluster);
            clusters.get(cluster).addPixel(pixel);
        }
    }

    private void calculateCentroids() {
        for(Cluster cluster : clusters) {
            int sumR = 0;
            int sumG = 0;
            int sumB = 0;
            List<Pixel> list = cluster.getPixels();
            int n_points = list.size();

            for(Pixel pixel : list) {
                sumR += pixel.getR();
                sumG += pixel.getG();
                sumB += pixel.getB();
            }

            Pixel centroid = cluster.getCentroid();
            if(n_points > 0) {
                int newR = sumR / n_points;
                int newG = sumG / n_points;
                int newB= sumB / n_points;
                centroid.setR(newR);
                centroid.setG(newG);
                centroid.setB(newB);
            }
        }
    }
}
