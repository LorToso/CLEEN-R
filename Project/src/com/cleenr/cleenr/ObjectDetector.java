package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class ObjectDetector {

	private Mat mStrongColors;
	private Mat mDarkPixels;
	private Mat mStrongButNotDarkPixels;
	private Mat mHirachy;
		
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
		try{
			mCleenrImage.setImage(rgba);
		}
		catch(FrameSizeChangedException ex)
		{
			applyNewFrameSize(ex.getNewSize());
		}
		
		
		mCleenrImage.detectStrongColors(mStrongColors, mDetectionParameters.nSaturationThreshold);
		mCleenrImage.detectDarkColors(mDarkPixels, mDetectionParameters.nDarknessThreshold);
		removeDarkColors();
		
		ArrayList<MatOfPoint> contours = findContours(mStrongButNotDarkPixels, mHirachy);
		
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
		}
		return allBoundingRects;
	}

	/*
	 * Multiplies the Strong color Matrix with the Dark Color matrix, 
	 * Resulting in a binary image, which shows only colorful pixels
	 * But not dark ones.
	 */
	private void removeDarkColors() {
		mStrongButNotDarkPixels = mStrongColors.mul(mDarkPixels);
	}
	/*
	 * Finds contours of all objects in the image
	 */
	private ArrayList<MatOfPoint> findContours(Mat image, Mat hirarchy) {
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(image, contours, hirarchy, 0, /*Imgproc.CV_CHAIN_APPROX_SIMPLE*/ 2);
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
		mDarkPixels 			= new Mat(frameHeight, frameWidth, CvType.CV_8UC1);
		mStrongColors 			= new Mat(frameHeight, frameWidth, CvType.CV_8UC1);
		mStrongButNotDarkPixels = new Mat(frameHeight, frameWidth, CvType.CV_8UC1);
		
		mHirachy				= new Mat(frameHeight, 4, CvType.CV_8UC1);

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
