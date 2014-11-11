package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.SeekBar;

public class CLEENRBrain 
{
	private final static double sColorSimilarity = 1.1;
	private final static double sAreaSimilarity = 1.1;
	private final static double sAreaDelta = 1.1;

	private Context mContext;
	
	private ObjectDetector mObjectDetector = new ObjectDetector();
	private FocusedObject mFocusedObject = new FocusedObject();
	
	private int nDetectedObjects = 0;
	
	private int nFrameWidth = 0;
	private int nFrameHeight = 0;
	
	
	
	public CLEENRBrain(Context context)
	{
		mContext = context;
	}

    
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Mat outputFrame = inputFrame.rgba();
		nFrameWidth = outputFrame.cols();
		nFrameHeight = outputFrame.rows();

		//autoAdjust();
		//applySeekBarParameters();
			
		ArrayList<Rect> allBoundingRects = mObjectDetector.detectObjects(inputFrame);
		nDetectedObjects = allBoundingRects.size();
		
		if(mFocusedObject != null)
		{
			FocusedObject newFocus = findBestMatch(mFocusedObject, allBoundingRects, inputFrame.rgba());
			// if(newFocus == null) refocus();
			focusObject(newFocus);
		}
		else
			focusObject(focusBiggestObject(inputFrame.rgba(), allBoundingRects));
		
		CleenrUtils.drawRect(outputFrame, mFocusedObject);
		
		return outputFrame;
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
		
		FocusedObject.filterContainingPoint(candidates, focusedObject.mCenter, sAreaDelta);
		if(candidates.size()==0)
			return null;
		if(candidates.size()==1)
			return candidates.get(0);
		
		FocusedObject.filterSimilarColor(candidates, focusedObject.getMeanColor(), fullRGBAImage, sColorSimilarity);
		if(candidates.size()==0)
			return null;
		if(candidates.size()==1)
			return candidates.get(0);
		
		FocusedObject.filterSimilarSize(candidates, focusedObject.mArea, sAreaSimilarity);
		if(candidates.size()==0)
			return null;
		if(candidates.size()==1)
			return candidates.get(0);
			
		// Else is > 1 object. Whatever	
		return candidates.get(0);
	}

	private FocusedObject focusBiggestObject(Mat rgbaImage, ArrayList<Rect> allBoundingRects)
	{
		Rect focusedObject = CleenrUtils.getBiggestRect(allBoundingRects);	
		return FocusedObject.createFromFullImage(rgbaImage, focusedObject);
	}
	private void focusObject(FocusedObject focusedObject)
	{
		if(focusedObject != null)
			Log.d("FOCUSED OBJECT", "New Focus - Center: " + focusedObject.mCenter + " Color: " + focusedObject.getMeanColor());
		mFocusedObject = focusedObject;
	}
	
	private void applySeekBarParameters()
	{
		SeekBar objectSizeBar 	= (SeekBar) ((Activity) mContext).findViewById(R.id.objectSizeBar);
		SeekBar thresholdBar 	= (SeekBar) ((Activity) mContext).findViewById(R.id.saturationThreshholdBar);	

		int saturationThreshold = (int) (((double)thresholdBar.getProgress()/100) * 255);
		int minimumObjectSize 	= (int) (((double)objectSizeBar.getProgress()/100) * nFrameHeight*nFrameWidth/2);
		
		mObjectDetector.getDetectionParameters().nMinimumObjectSize 	= minimumObjectSize;
		mObjectDetector.getDetectionParameters().nSaturationThreshold 	= saturationThreshold;
	}
}
