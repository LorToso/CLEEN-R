package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class ObjectDetector {

	Mat mStrongColors;
	Mat mDarkPixels;
	Mat mStrongButNotDarkPixels;
	Mat mHirachy;
		
	DetectionParameters mDetectionParameters;
	HSVImage mHsvImage;
	
	
	public ObjectDetector()
	{
		initializeMatrices();
		mDetectionParameters = new DetectionParameters();
		mHsvImage = new HSVImage();
	}


	public ArrayList<Rect> detectObjects(CvCameraViewFrame inputFrame)
	{
		Mat rgba = inputFrame.rgba();
		mHsvImage.setImage(rgba);
		
		detectStrongColors();
		detectDarkColors();
		removeDarkColors();
		
		ArrayList<MatOfPoint> contours = findContours();
		
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
			
			allBoundingRects.add(r);
		}
		return allBoundingRects;
	}


	/*
	 * Finds contours of all objects in the image
	 */
	private ArrayList<MatOfPoint> findContours() {
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(mStrongButNotDarkPixels, contours, mHirachy, 0, /*Imgproc.CV_CHAIN_APPROX_SIMPLE*/ 2);
		return contours;
		
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
	 * Filters the Saturation Channel of HSV and 
	 * saves all pixels with a saturation value greater than mSaturationThreshold
	 * as 255 into mStrongColors.
	 */
	private void detectStrongColors() {
		Imgproc.threshold(mHsvImage.getSaturationChannel(), mStrongColors, mDetectionParameters.nSaturationThreshold, 255, Imgproc.THRESH_BINARY);
	}
	
	/*
	 * Filters the Value Channel of HSV and 
	 * saves all pixels with a Value greater than mDarknessThreshold
	 * as 1 into mDarkColors.
	 */
	private void detectDarkColors() {
		Imgproc.threshold(mHsvImage.getValueChannel(), mDarkPixels, mDetectionParameters.nDarknessThreshold, 1, Imgproc.THRESH_BINARY);
	}

	
	/*
	 * Initializes all matrices
	 */
	private void initializeMatrices() {
		Mat testFrame = new Mat(1,1, CvType.CV_8SC4);
		applyNewFrameSize(testFrame);
	}

	/*
	 * Reallocates all matrices to fit the new frame size
	 * This should happen if the orientation is changed.
	 */
	private void applyNewFrameSize(Mat rgbaFrame)
	{
		
		mDarkPixels 			= new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8UC1);
		mStrongColors 			= new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8UC1);
		mStrongButNotDarkPixels = new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8UC1);
		
		mHirachy				= new Mat(rgbaFrame.rows(), 4, CvType.CV_8UC1);
	}


	public void setDetectionParameters(DetectionParameters detectionParameters)
	{
		mDetectionParameters = detectionParameters;
	}
	public DetectionParameters getDetectionParameters()
	{
		return mDetectionParameters;
	}
	
}
