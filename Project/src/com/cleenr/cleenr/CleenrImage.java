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
	private static final int CHANNEL_VALUE 		= 2;
	
	private Mat mRGBA;
	private Mat mHSV;
	private Mat mH;
	private Mat mS;
	private Mat mV;
	private ArrayList<Mat> mHSVChannels;

	private Size mFrameSize = new Size(0,0);
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
		mHSVChannels.add(mV);
		mHSVChannels.add(mS);
		mHSVChannels.add(mH);
	}
	public CleenrImage(Mat rgba)
	{
		super();
		try
		{
			setImage(rgba);
		}
		catch(FrameSizeChangedException e)
		{
			// Nothing to do
		}
	}
	/*
	 * Sets a new Rgba image to be converted to HSV
	 */
	public void setImage(Mat rgba) throws FrameSizeChangedException
	{
		Size oldSize = mFrameSize;
		Size newSize = generateSize(rgba);
		
		if(oldSize != newSize)
			applyNewFrameSize(rgba);		
		
		mRGBA = rgba;
		convertToHSVImage(mRGBA, mHSV);
		splitHSVChannels(mHSV, mHSVChannels);
		
		if(oldSize != newSize)
			throw new FrameSizeChangedException(oldSize, newSize);
	}
	

	private void applyNewFrameSize(Mat rgbaFrame)
	{
		mHSV = new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8SC3);		
		mH = new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8UC1);
		mS = new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8UC1);
		mV = new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8UC1);
	}
	private void convertToHSVImage(Mat inRGBA, Mat outHSV)
	{
		Imgproc.cvtColor(inRGBA, outHSV, Imgproc.COLOR_RGB2HSV);
	}
	private void splitHSVChannels(Mat hsv, ArrayList<Mat> outChannels)
	{
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
	public int getWidth()
	{
		return mHSV.cols();
	}
	public int getHeight()
	{
		return mHSV.rows();
	}
	
	
	
	
	private static Size generateSize(Mat rgba)
	{
		return new Size(rgba.cols(), rgba.rows());
	}
}
