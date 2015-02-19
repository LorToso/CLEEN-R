package com.cleenr.cleen_r.objectDetection;

import org.opencv.core.Size;

public class DetectionParameters {
    int nMinimumObjectSize;
    int nMaximumObjectSize;
    int nSaturationThreshold;
    int nDarknessThreshold;


    public DetectionParameters(Size frameSize) {
        this((int) frameSize.width, (int) frameSize.height);
    }

    public DetectionParameters(int frameWidth, int frameHeight) {
        int frameArea = frameWidth * frameHeight;
        nMinimumObjectSize = (int) (0.005 * frameArea);
        nMaximumObjectSize = frameArea/2 ;
        nSaturationThreshold = 100;
        nDarknessThreshold = 25;
    }

    public void lower() {
        nMinimumObjectSize *= 0.9;
        nSaturationThreshold *= 0.9;
    }

    public void increase() {
        nMinimumObjectSize *= 1.11;
        nSaturationThreshold *= 1.11;
    }
}
