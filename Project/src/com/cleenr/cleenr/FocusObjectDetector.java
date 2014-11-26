package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

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
			return findBiggestObject(detectedObjects);

		return newFocus;

	}

	private FocusObject findBestMatch(FocusObject previousFocus, ArrayList<FocusObject> detectedObjects) {

		if(previousFocus == null)
			return null;
		
		for(FocusObject detectedObject : detectedObjects)
		{
			boolean areSimilar = true;
			areSimilar &= previousFocus.haveSimilarPosition(detectedObject);
			areSimilar &= previousFocus.haveSimilarColor(detectedObject);
			areSimilar &= previousFocus.haveSimilarSize(previousFocus);
			if(areSimilar)
				return detectedObject;
		}
		return null;
	}

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
		
		if(maxIndex == -1)
			return null;
		
		return allFocusObjects.get(maxIndex);
	}

	public static void filterSimilarColor(ArrayList<Rect> candidates, Scalar focusedColor, Mat fullRGBAImage, double colorSimilarity) {
		
	}
}
