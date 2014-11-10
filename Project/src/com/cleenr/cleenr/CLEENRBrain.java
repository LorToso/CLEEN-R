package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.SeekBar;

public class CLEENRBrain 
{
	private final double colorSimilarity = 1.1;
	private final double areaSimilarity = 1.1;

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
		
		drawRect(outputFrame, mFocusedObject);
		
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
		// 2. Is neither 10% bigger, nor smaller
		ArrayList<Rect> candidates = new ArrayList<Rect>(allBoundingRects);
		if(candidates.size()==0)
			return null;
		if(candidates.size()==1)
			return candidates.get(0);
		
		filterContainingPoint(candidates, focusedObject.mCenter);
		if(candidates.size()==0)
			return null;
		if(candidates.size()==1)
			return candidates.get(0);
		
		filterSimilarColor(candidates, focusedObject.getMeanColor(), fullRGBAImage);
		if(candidates.size()==0)
			return null;
		if(candidates.size()==1)
			return candidates.get(0);
		
		filterSimilarSize(candidates, focusedObject.mArea);
		if(candidates.size()==0)
			return null;
		if(candidates.size()==1)
			return candidates.get(0);
			
		// Else is > 1 object. Whatever	
		return candidates.get(0);
	}
	private void filterSimilarSize(ArrayList<Rect> candidates, Rect focusedArea) {
		for(int i = 0; i < candidates.size(); i++)
		{
			Rect rect = candidates.get(i);
			
			if(	!(
					focusedArea.area()*areaSimilarity > rect.area()
				&&	focusedArea.area()*(2-areaSimilarity) < rect.area()
				))
			{
				candidates.remove(i);
				i--;
			}
			
		}		
	}
	private void filterSimilarColor(ArrayList<Rect> candidates, Scalar focusedColor, Mat fullRGBAImage) {
		double minR = focusedColor.val[0]*(2-colorSimilarity);
		double maxR = focusedColor.val[0]*(2-colorSimilarity);
		double minG = focusedColor.val[1]*(2-colorSimilarity);
		double maxG = focusedColor.val[1]*(2-colorSimilarity);
		double minB = focusedColor.val[2]*(2-colorSimilarity);
		double maxB = focusedColor.val[2]*(2-colorSimilarity);
		
		for(int i = 0; i < candidates.size(); i++)
		{
			Rect rect = candidates.get(i);
			double rectValues[] = Core.mean(new Mat(fullRGBAImage, rect)).val;
			
			if(	!(
					(rectValues[0] > minR && rectValues[0] < maxR)
				&&	(rectValues[1] > minG && rectValues[1] < maxG)
				&&	(rectValues[2] > minB && rectValues[2] < maxB)
				))
			{
				candidates.remove(i);
				i--;
			}
			
		}
	}
	private void filterContainingPoint(ArrayList<Rect> candidates, Point point)
	{
		for(int i = 0; i < candidates.size(); i++)
		{
			Rect rect = candidates.get(i);
			if(!rect.contains(point))
			{
				candidates.remove(i);
				i--;
			}
		}
	}

	private FocusedObject focusBiggestObject(Mat rgbaImage, ArrayList<Rect> allBoundingRects)
	{
		Rect focusedObject = getBiggestRect(allBoundingRects);	
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
	
	private void drawRects(Mat outputFrame, ArrayList<Rect> rects)
	{
		int i = 0;
		
		for(Rect rect : rects)
		{
			drawRect(outputFrame, rect);
			i++;
			Point middle = new Point(rect.x + rect.width/2, rect.y + rect.height/2);
			Log.d("OBJECT FOUND", "OBJECT " + i +  " AT (" + middle.x + "|" + middle.y + ")");
		}
	}
	private void drawRect(Mat outputFrame, FocusedObject focusedObject)
	{
		if(focusedObject != null)
			drawRect(outputFrame, focusedObject.getRect());
	}
	private void drawRect(Mat outputFrame, Rect rect)
	{
		if(rect == null)
			return;
		
		Point middle = new Point(rect.x + rect.width/2, rect.y + rect.height/2);
		Core.rectangle(outputFrame, rect.tl(), rect.br(), new Scalar(255,255,0), 5);
		Core.rectangle(outputFrame, new Point(middle.x-2, middle.y-2), new Point(middle.x+2, middle.y+2), new Scalar(255,255,0), 5);
	}
	
	
	private Rect getBiggestRect(ArrayList<Rect> rects)
	{
		if(rects.size() == 0)
			return null;
		
		int maxIndex = -1;
		double maxArea = -1;
		int index = 0;
		for(Rect rect : rects)
		{
			double area = rect.area();
			if(area > maxArea)
			{
				maxArea = area;
				maxIndex = index;
			}
			index++;
		}
		return rects.get(maxIndex);
	}

}
