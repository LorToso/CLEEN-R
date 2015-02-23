package com.cleenr.cleen_r.focusObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import com.cleenr.cleen_r.CleenrImage;
import com.cleenr.cleen_r.CleenrUtils;
import com.cleenr.cleen_r.objectDetection.ObjectDetector;

public class FocusObjectDetector {

    private ObjectDetector mObjectDetector = new ObjectDetector();

    /*
     * Returns a FocusTarget based on the previous focus
     */
    public FocusObject findFocusTarget(FocusObject previousFocus) {
        CleenrImage mFrame = CleenrImage.getInstance();
        ArrayList<FocusObject> detectedObjects = mObjectDetector.detectObjects();


        CleenrUtils.drawFocusObjects(mFrame.mOutputFrame, detectedObjects, new Scalar(255, 0, 0));

        FocusObject newFocus = findBestMatch(previousFocus, detectedObjects);

        if (newFocus == null)
            return findBestBigSquare(detectedObjects);

        return newFocus;

    }


    /*
        Returns the more squary Focusobject of the two biggest detected Objects
    */
    private FocusObject findBestBigSquare(ArrayList<FocusObject> detectedObjects) {
        SortDetectedObjects(detectedObjects);

        switch (detectedObjects.size())
        {
            case 0:
                return new NoFocus();
            case 1:
                return detectedObjects.get(0);
            default:
                break;
        }

        FocusObject biggestObject = detectedObjects.get(detectedObjects.size()-1);
        FocusObject secondBiggestObject = detectedObjects.get(detectedObjects.size()-2);

        Rect biggestRect = biggestObject.getRect();
        Rect secondBiggestRect = secondBiggestObject.getRect();
        double topRatio =       Math.max(biggestRect.width,biggestRect.height)/Math.min(biggestRect.width,biggestRect.height);
        double secondRatio =    Math.max(secondBiggestRect.width,secondBiggestRect.height)/Math.min(secondBiggestRect.width,secondBiggestRect.height);

        if(topRatio > secondRatio)
            return secondBiggestObject;
        return biggestObject;
    }

    private void SortDetectedObjects(ArrayList<FocusObject> detectedObjects) {
        Collections.sort(detectedObjects, new Comparator<FocusObject>() {
            @Override
            public int compare(FocusObject lhs, FocusObject rhs) {
                return Double.valueOf(lhs.getRect().area()).compareTo(rhs.getRect().area());
            }
        });
    }

    private FocusObject findBestMatch(FocusObject previousFocus, ArrayList<FocusObject> detectedObjects) {

        if (previousFocus == null)
            return null;

        for (FocusObject detectedObject : detectedObjects) {
            boolean areSimilar;
            areSimilar = previousFocus.haveSimilarPosition(detectedObject);
            areSimilar &= previousFocus.haveSimilarColor(detectedObject);
            areSimilar &= previousFocus.haveSimilarSize(previousFocus);
            if (areSimilar)
                return detectedObject;
        }
        return null;
    }
/*
    private FocusObject findBiggestObject(ArrayList<FocusObject> allFocusObjects) {
        int maxIndex = -1;
        double maxArea = -1;
        int index = 0;
        for (FocusObject object : allFocusObjects) {
            double area = object.getRect().area();
            if (area > maxArea) {
                maxArea = area;
                maxIndex = index;
            }
            index++;
        }

        if (maxIndex == -1)
            return new NoFocus();

        return allFocusObjects.get(maxIndex);
    }*/

}
