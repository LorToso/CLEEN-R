package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class FocusObjectFinder 
{
	private final static double sColorSimilarity = 1.1;
	private final static double sAreaSimilarity = 1.1;
	private final static double sAreaDelta = 1.1;
	
	
	private ObjectDetector mObjectDetector = new ObjectDetector();
	private int nDetectedObjects = 0;
	
	/*
	 * Returns a FocusTarget based on the previous focus
	 */
	public FocusedObject findFocusTarget(CvCameraViewFrame inputFrame, FocusedObject previousFocus)
	{
		ArrayList<Rect> allBoundingRects = mObjectDetector.detectObjects(inputFrame);
		nDetectedObjects = allBoundingRects.size();
		
		if(previousFocus != null)
		{
			FocusedObject newFocus = findBestMatch(previousFocus, allBoundingRects, inputFrame.rgba());
			// if(newFocus == null) refocus();
			return newFocus;
		}
		return findBiggestObject(inputFrame.rgba(), allBoundingRects);
	}
	
	private FocusedObject findBestMatch(FocusedObject focusedObject, ArrayList<Rect> allBoundingRects, Mat fullRGBAImage) {
		Rect matchingRect = findMatchingRect(focusedObject, allBoundingRects, fullRGBAImage);
		
		if(matchingRect == null)
			return null;
		return FocusedObject.createFromFullImage(fullRGBAImage, matchingRect);
	}
	private Rect findMatchingRect(FocusedObject focusedObject, ArrayList<Rect> allBoundingRects, Mat fullRGBAImage) {
		// Critera
		// 1. Contains old center
		// 3. Has a similar color
		// 2. Size is not too different
		ArrayList<Rect> candidates = new ArrayList<Rect>(allBoundingRects);
		if(candidates.size()==0)
			return null;
		if(candidates.size()==1)
			return candidates.get(0);
		
		FocusedObject.filterContainingPoint(candidates, focusedObject.getCenter(), sAreaDelta);
		if(candidates.size()==0)
			return null;
		if(candidates.size()==1)
			return candidates.get(0);
		
		FocusedObject.filterSimilarColor(candidates, focusedObject.getMeanColor(), fullRGBAImage, sColorSimilarity);
		if(candidates.size()==0)
			return null;
		if(candidates.size()==1)
			return candidates.get(0);
		
		FocusedObject.filterSimilarSize(candidates, focusedObject.getRect(), sAreaSimilarity);
		if(candidates.size()==0)
			return null;
		if(candidates.size()==1)
			return candidates.get(0);
			
		// Else is > 1 object. Whatever	
		return candidates.get(0);
	}
	private FocusedObject findBiggestObject(Mat rgbaImage, ArrayList<Rect> allBoundingRects)
	{
		Rect focusedObject = CleenrUtils.getBiggestRect(allBoundingRects);	
		return FocusedObject.createFromFullImage(rgbaImage, focusedObject);
	}

	public void lowerCriteria() {
		mObjectDetector.lowerDetectionCriteria();
	}
	public void increaseCriteria() {
		mObjectDetector.increaseDetectionCriteria();
	}
	
}
