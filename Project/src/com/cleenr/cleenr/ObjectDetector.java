package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class ObjectDetector {
		
	private DetectionParameters mDetectionParameters;
	private CleenrImage mCleenrImage;
	
	
	public ObjectDetector()
	{
		initializeMatrices();
		mDetectionParameters = new DetectionParameters(0,0);
		mCleenrImage = CleenrImage.getInstance();
	}


	public ArrayList<Rect> detectObjects(Mat rgba)
	{	
		mCleenrImage.setImage(rgba);
		if(mCleenrImage.didFrameSizeChange())
		{
			applyNewFrameSize(mCleenrImage.getFrameSize());
		}
		
		Mat strongColors = new Mat();
		mCleenrImage.detectStrongColors(strongColors, mDetectionParameters.nSaturationThreshold);
		
		Mat darkPixels = new Mat();
		mCleenrImage.detectDarkColors(darkPixels, mDetectionParameters.nDarknessThreshold);
		
		Mat strongButNotDarkPixels = strongColors.mul(darkPixels);
		
		ArrayList<MatOfPoint> contours = findContours(strongButNotDarkPixels);
		
		return createBoundingRects(contours);
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
	private ArrayList<MatOfPoint> findContours(Mat image) {
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE); 
		return contours;
	}	

	
	/*
	 * Initializes all matrices
	 */
	private void initializeMatrices() {
		// tempSize
		applyNewFrameSize(new Size(1,1));
	}

	/*
	 * Reallocates all matrices to fit the new frame size
	 * This should happen if the orientation is changed.
	 */
	private void applyNewFrameSize(Size frameSize)
	{
		int frameWidth 			= (int) frameSize.width;
		int frameHeight 		= (int) frameSize.height;
				
		//mDarkPixels 			= new Mat(frameHeight, frameWidth, CvType.CV_8UC1);
		//mStrongColors 			= new Mat(frameHeight, frameWidth, CvType.CV_8UC1);
		
		mDetectionParameters 	= new DetectionParameters(frameWidth,frameHeight);		
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
	
}
