package com.cleenr.cleen_r.objectDetection;

import java.util.ArrayList;
import java.util.Iterator;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.cleenr.cleen_r.CleenrImage;
import com.cleenr.cleen_r.focusObject.FocusObject;

import static com.cleenr.cleen_r.focusObject.FocusObject.createFromContours;


public class ObjectDetector {

    private DetectionParameters mDetectionParameters;

    private Mat mHierarchy = new Mat();
    private Mat mStrongColors = new Mat();
    private Mat mDarkColors = new Mat();

    public ObjectDetector() {
        mDetectionParameters = new DetectionParameters(0, 0);
    }


    public ArrayList<FocusObject> detectObjects() {
        CleenrImage image = CleenrImage.getInstance();
        if (image.didFrameSizeChange())
            mDetectionParameters = new DetectionParameters(image.getFrameSize());

        Mat strongButNotDarkPixels = prepareImageForDetection();

        ArrayList<MatOfPoint> contours = findContours(strongButNotDarkPixels);
        filterObjectsBySize(contours);

        return createFromContours(contours);
    }


    private Mat prepareImageForDetection() {
        CleenrImage image = CleenrImage.getInstance();
        image.detectStrongColors(mStrongColors, mDetectionParameters.nSaturationThreshold);
        image.detectDarkColors(mDarkColors, mDetectionParameters.nDarknessThreshold);

        return mStrongColors.mul(mDarkColors);
    }

    private void filterObjectsBySize(ArrayList<MatOfPoint> contours) {
        Iterator<MatOfPoint> iterator =  contours.iterator();
        while (iterator.hasNext()) {
            MatOfPoint contour = iterator.next();
            Rect r = Imgproc.boundingRect(contour);

            if (r.area() > mDetectionParameters.nMinimumObjectSize
                    &&
                r.area() < mDetectionParameters.nMaximumObjectSize)
                continue;

            iterator.remove();
        }
    }

    /*
     * Finds contours of all objects in the image
     */
    public ArrayList<MatOfPoint> findContours(Mat image) {
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Imgproc.blur(image, image, new Size(3, 3));
        Imgproc.threshold(image, image, 150, 255, Imgproc.THRESH_BINARY);
        Imgproc.findContours(image, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        return contours;
    }

}
