package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class FocusObjectFinder 
{
	private final static double sColorSimilarity = 1.2;
	private final static double sAreaSimilarity = 1.1;
	private final static double sAreaDelta = 1.1;
	
	
	private ObjectDetector mObjectDetector = new ObjectDetector();
	private int nDetectedObjects = 0;
	
	/*
	 * Returns a FocusTarget based on the previous focus
	 */
	public FocusedObject findFocusTarget(Mat rgbaFrame, FocusedObject previousFocus)
	{
		ArrayList<Rect> allBoundingRects = mObjectDetector.detectObjects(rgbaFrame);
		nDetectedObjects = allBoundingRects.size();
		
		for(Rect r : allBoundingRects)
		{
			CleenrUtils.drawRect(CLEENRBrain.outputFrame, r, new Scalar(255,0,0));
		}
		
		if(previousFocus != null)
		{
			FocusedObject newFocus = findBestMatch(previousFocus, allBoundingRects, rgbaFrame);
			if(newFocus != null)
				return newFocus;
		}
		return findBiggestObject(rgbaFrame, allBoundingRects);
	}
	
	private FocusedObject findBestMatch(FocusedObject oldFocusObject, ArrayList<Rect> allBoundingRects, Mat fullRGBAImage) {
		Rect matchingRect = findMatchingRect(oldFocusObject, allBoundingRects, fullRGBAImage);
		
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
