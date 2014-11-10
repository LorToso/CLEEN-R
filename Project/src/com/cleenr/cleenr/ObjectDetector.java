package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Core;
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
	
	Mat mHSV;
	Mat mH;
	Mat mS;
	Mat mV;
	ArrayList<Mat> mHSVChannels;

	int mSaturationThreshold 	= 0;
	int mDarknessThreshold 		= 50;

	int mMinimumObjectSize		= 100;
	
	//private final int CHANNEL_HUE 			= 0;
	private final int CHANNEL_SATURATION 	= 1;
	private final int CHANNEL_VALUE 		= 2;
	
	
	
	public ObjectDetector()
	{
		initializeMatrices();
		
		mHSVChannels = new ArrayList<Mat>();
		mHSVChannels.add(mV);
		mHSVChannels.add(mS);
		mHSVChannels.add(mH);
		
	}


	public ArrayList<Rect> detectObjects(CvCameraViewFrame inputFrame)
	{
		Mat rgba = inputFrame.rgba();
		if(rgba.rows() != mHSV.rows() || rgba.cols() != mHSV.cols())
			applyNewFrameSize(rgba);
		
		convertToHSVImage(rgba);
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
			if(r.area() < mMinimumObjectSize)
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
		Imgproc.threshold(getSaturationChannel(), mStrongColors, mSaturationThreshold, 255, Imgproc.THRESH_BINARY);
	}
	
	/*
	 * Filters the Value Channel of HSV and 
	 * saves all pixels with a Value greater than mDarknessThreshold
	 * as 1 into mDarkColors.
	 */
	private void detectDarkColors() {
		Imgproc.threshold(getValueChannel(), mDarkPixels, mDarknessThreshold, 1, Imgproc.THRESH_BINARY);
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
		mHSV = new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8SC3);
		
		mH = new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8UC1);
		mS = new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8UC1);
		mV = new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8UC1);
		
		mDarkPixels 			= new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8UC1);
		mStrongColors 			= new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8UC1);
		mStrongButNotDarkPixels = new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8UC1);
		
		mHirachy				= new Mat(rgbaFrame.rows(), 4, CvType.CV_8UC1);
	}
	
	private void convertToHSVImage(Mat rgba)
	{
		Imgproc.cvtColor(rgba, mHSV, Imgproc.COLOR_RGB2HSV);
		Core.split(mHSV, mHSVChannels);
	}

	private Mat getSaturationChannel()
	{
		return mHSVChannels.get(CHANNEL_SATURATION);
	}
	/*private Mat getHueChannel()
	{
		return mHSVChannels.get(CHANNEL_HUE);
	}*/
	private Mat getValueChannel()
	{
		return mHSVChannels.get(CHANNEL_VALUE);
	}

	public void setSaturationThreshold(int saturationThreshold)
	{
		mSaturationThreshold = saturationThreshold;
	}
	public void setDarknessThreshold(int darknessThreshold)
	{
		mDarknessThreshold = darknessThreshold;
	}
	public void setMinimumObjectSize(int minimumObjectSize)
	{
		mMinimumObjectSize = minimumObjectSize;
	}
}
