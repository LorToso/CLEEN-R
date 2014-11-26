package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class ObjectDetector {
		
	private DetectionParameters mDetectionParameters;

	private Mat mHierarchy = new Mat();
	private Mat mStrongColors = new Mat();
	private Mat mDarkColors = new Mat();
	
	public ObjectDetector()
	{
		mDetectionParameters = new DetectionParameters(0,0);
	}


	public ArrayList<FocusObject> detectObjects()
	{	
		CleenrImage image = CleenrImage.getInstance();
		if(image.didFrameSizeChange())
			mDetectionParameters = new DetectionParameters(image.getFrameSize());
		
		Mat strongButNotDarkPixels = prepareImageForDetection();
		
		ArrayList<MatOfPoint> contours 	= findContours(strongButNotDarkPixels);
		ArrayList<Rect>	boundingRects 	= createBoundingRects(contours);
		contours.clear();
		ArrayList<FocusObject>	detectedObjects = FocusObject.createFromRects(boundingRects);
		boundingRects.clear();
		return detectedObjects;
	}


	private Mat prepareImageForDetection() {
		CleenrImage image = CleenrImage.getInstance();
		image.detectStrongColors(mStrongColors, mDetectionParameters.nSaturationThreshold);
		image.detectDarkColors(mDarkColors, mDetectionParameters.nDarknessThreshold);
		Mat strongButNotDarkPixels = mStrongColors.mul(mDarkColors);
		return strongButNotDarkPixels;
	}

	/*
	 * Creates bounding Rects to every countour List 
	 */
	private ArrayList<Rect> createBoundingRects(ArrayList<MatOfPoint> contours) {
		ArrayList<Rect> allBoundingRects = new ArrayList<Rect>();
		
		for(MatOfPoint contour : contours)
		{
			Rect r = Imgproc.boundingRect(contour);
			if(r.area() < mDetectionParameters.nMinimumObjectSize)
				continue;
			if(r.area() > mDetectionParameters.nMaximumObjectSize)
				continue;
			
			allBoundingRects.add(r);
			contour.release();
		}
		return allBoundingRects;
	}

	/*
	 * Finds contours of all objects in the image
	 */
	public ArrayList<MatOfPoint> findContours(Mat image) {
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.blur(image, image, new Size(3,3));
		Imgproc.threshold(image, image, 150, 255, Imgproc.THRESH_BINARY);
		
		
		//CLEENRBrain.outputFrame = image; 

		//findContours(image.nativeObj, rects.nativeObj);
		Imgproc.findContours(image, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);		
        
        return contours;
	}
	
	public void setDetectionParameters(DetectionParameters detectionParameters)
	{
		mDetectionParameters = detectionParameters;
	}
	
	/*
	 * Lowers the detection criteria by 10%
	 */
	public void lowerDetectionCriteria()
	{
		mDetectionParameters.lower();
	}

	/*
	 * Increases the detection criteria by 10%
	 */
	public void increaseDetectionCriteria()
	{
		mDetectionParameters.increase();
	}
	private native void findContours(long pImage, long pContures);
	
}
