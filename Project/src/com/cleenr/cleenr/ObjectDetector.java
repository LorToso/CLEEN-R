package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import android.util.Log;


public class ObjectDetector {
		
	private DetectionParameters mDetectionParameters;
	private CleenrImage mCleenrImage;

	private Mat mHierarchy = new Mat();
	private Mat mStrongColors = new Mat();
	private Mat mDarkColors = new Mat();
	
	public ObjectDetector()
	{
		mDetectionParameters = new DetectionParameters(0,0);
		mCleenrImage = CleenrImage.getInstance();
	}


	public ArrayList<Rect> detectObjects(Mat rgba)
	{	
		mCleenrImage.setImage(rgba);
		if(mCleenrImage.didFrameSizeChange())
			mDetectionParameters = new DetectionParameters(mCleenrImage.getFrameSize());
		
		mCleenrImage.detectStrongColors(mStrongColors, mDetectionParameters.nSaturationThreshold);
		mCleenrImage.detectDarkColors(mDarkColors, mDetectionParameters.nDarknessThreshold);
		
		Mat strongButNotDarkPixels = mStrongColors.mul(mDarkColors);
		
		ArrayList<MatOfPoint> contours = findContours(strongButNotDarkPixels);
		ArrayList<Rect>	boundingRects = createBoundingRects(contours);
		contours.clear();
		return boundingRects;
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
		
		
		CLEENRBrain.outputFrame = image; 
		Mat rects = new Mat();
		//findContours(image.nativeObj, rects.nativeObj);
		

//		Imgproc.Canny(image, CLEENRBrain.outputFrame, 50, 100);
		
		for(int i=0; i< rects.rows(); i++)
		{
			Rect r = new Rect(rects.get(i, 0));
			Log.d("Keypoint", "Drawing Rect " + r);
			CleenrUtils.drawRect(CLEENRBrain.outputFrame, r);
		}
		rects.release();
		
		//Imgproc.findContours(image, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        
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
