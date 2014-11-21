package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CleenrImage {
	private static final int CHANNEL_HUE 			= 0;
	private static final int CHANNEL_SATURATION 	= 1;
	private static final int CHANNEL_VALUE 			= 2;
	
	private Mat mRGBA;
	private Mat mHSV;
	private ArrayList<Mat> mHSVChannels;

	private Size mFrameSize = new Size(0,0);
	private boolean mFrameSizeChanged = false; // shows if the frame size changed between the last two calls of setImage
	private static CleenrImage instance = null;
	
	public static CleenrImage getInstance()
	{
		if(instance == null)
			instance = new CleenrImage();
		return instance;
	}
	
	private CleenrImage()
	{
		mHSVChannels = new ArrayList<Mat>();
	}
	public CleenrImage(Mat rgba)
	{
		super();
		setImage(rgba);
	}
	/*
	 * Sets a new Rgba image to be converted to HSV
	 */
	public void setImage(Mat rgba)
	{
		Size oldSize = mFrameSize;
		Size newSize = CleenrUtils.generateSize(rgba);
		
		mFrameSizeChanged = (oldSize.width != newSize.width) || (oldSize.height != newSize.height);
		if(mFrameSizeChanged)
			applyNewFrameSize(newSize);
		
		releaseOldImage();
		
		mRGBA = rgba;
		convertToHSVImage(mRGBA, mHSV);
		splitHSVChannels(mHSV, mHSVChannels);
	}
	

	private void releaseOldImage() {
		if(mRGBA != null)
			mRGBA.release();
		if(mHSV != null)
			mHSV.release();
	}

	private void applyNewFrameSize(Size newSize)
	{
		mFrameSize = newSize;
		mHSV = new Mat((int)mFrameSize.height, (int)mFrameSize.width, CvType.CV_8SC3);		
	}
	private void convertToHSVImage(Mat inRGBA, Mat outHSV)
	{
		Imgproc.cvtColor(inRGBA, outHSV, Imgproc.COLOR_RGB2HSV);
	}
	private void splitHSVChannels(Mat hsv, ArrayList<Mat> outChannels)
	{
		for(Mat m : outChannels)
		{
			m.release();
		}
		outChannels.clear();
		Core.split(mHSV, mHSVChannels);
		
	}
	
	/*
	 * Filters the Value Channel of HSV and 
	 * saves all pixels with a Value greater than mDarknessThreshold
	 * as 1 into outDarkPixels.
	 */
	public void detectDarkColors(Mat outDarkPixels, int nDarknessThreshold) {
		Imgproc.threshold(getValueChannel(), outDarkPixels, nDarknessThreshold, 1, Imgproc.THRESH_BINARY);
	}
	
	/*
	 * Filters the Saturation Channel of HSV and 
	 * saves all pixels with a saturation value greater than mSaturationThreshold
	 * as 255 into outStrongColors.
	 */
	public void detectStrongColors(Mat outStrongColors, int nSaturationThreshold) {
		Imgproc.threshold(getSaturationChannel(), outStrongColors, nSaturationThreshold, 255, Imgproc.THRESH_BINARY);
	}
	
	
	public Mat getSaturationChannel()
	{
		return mHSVChannels.get(CHANNEL_SATURATION);
	}
	public Mat getHueChannel()
	{
		return mHSVChannels.get(CHANNEL_HUE);
	}
	public Mat getValueChannel()
	{
		return mHSVChannels.get(CHANNEL_VALUE);
	}
	public Size getFrameSize()
	{
		return mFrameSize;
	}

	public boolean didFrameSizeChange() {
		return mFrameSizeChanged;
	}

}
