package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class HSVImage {
	private final int CHANNEL_HUE 			= 0;
	private final int CHANNEL_SATURATION 	= 1;
	private final int CHANNEL_VALUE 		= 2;
	
	Mat mHSV;
	Mat mH;
	Mat mS;
	Mat mV;
	ArrayList<Mat> mHSVChannels;

	
	public HSVImage()
	{
		mHSVChannels = new ArrayList<Mat>();
		mHSVChannels.add(mV);
		mHSVChannels.add(mS);
		mHSVChannels.add(mH);
	}
	public HSVImage(Mat rgba)
	{
		super();
		setImage(rgba);
	}
	public void setImage(Mat rgba)
	{
		if(frameSizeChanged(rgba))
			applyNewFrameSize(rgba);		
		convertToHSVImage(rgba);
	}
	
	private boolean frameSizeChanged(Mat rgba)
	{
		if(mHSV == null)
			return true;
		return (getWidth() != rgba.cols() || getHeight() != rgba.rows());
	}
	private void applyNewFrameSize(Mat rgbaFrame)
	{
		mHSV = new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8SC3);		
		mH = new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8UC1);
		mS = new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8UC1);
		mV = new Mat(rgbaFrame.rows(), rgbaFrame.cols(), CvType.CV_8UC1);
	}

	private void convertToHSVImage(Mat rgba)
	{
		mHSVChannels.clear();
		Imgproc.cvtColor(rgba, mHSV, Imgproc.COLOR_RGB2HSV);
		Core.split(mHSV, mHSVChannels);
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
}
