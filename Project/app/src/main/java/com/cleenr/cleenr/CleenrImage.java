package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CleenrImage {
	public static final int CHANNEL_HUE = 0;
	public static final int CHANNEL_SATURATION = 1;
	public static final int CHANNEL_VALUE = 2;

	public Mat mInputFrame;
	public Mat mOutputFrame;

	private Mat mHSV;
	private ArrayList<Mat> mHSVChannels;

	private Size mFrameSize = new Size(0, 0);
	private boolean mFrameSizeChanged = false; // shows if the frame size
												// changed between the last two
												// calls of setImage
	private static CleenrImage instance = null;

	public static CleenrImage getInstance() {
		if (instance == null)
			instance = new CleenrImage();
		return instance;
	}

	private CleenrImage() {
		mHSVChannels = new ArrayList<Mat>();
	}

	public void changeFrame(Mat inputFrame) {
		applyFrameSize(inputFrame);

		mInputFrame = inputFrame.clone();
		mOutputFrame = inputFrame.clone();

		convertToHSVImage(mInputFrame, mHSV);
		splitHSVChannels(mHSV, mHSVChannels);
	}

	private void applyFrameSize(Mat inputFrame) {
		if (mInputFrame == null || (inputFrame.cols() != mInputFrame.cols() || inputFrame.rows() != mInputFrame.rows())) {
			mFrameSize = CleenrUtils.generateSize(inputFrame);
			mHSV = new Mat((int) mFrameSize.height, (int) mFrameSize.width, CvType.CV_8SC3);
			mFrameSizeChanged = true;
			return;
		}
		mFrameSizeChanged = false;
	}

	private void convertToHSVImage(Mat inRGBA, Mat outHSV) {
		Imgproc.cvtColor(inRGBA, outHSV, Imgproc.COLOR_RGB2HSV);
	}

	private void splitHSVChannels(Mat hsv, ArrayList<Mat> outChannels) {
		for (Mat m : outChannels) {
			m.release();
		}
		outChannels.clear();
		Core.split(mHSV, mHSVChannels);

	}

	/*
	 * Filters the Value Channel of HSV and saves all pixels with a Value
	 * greater than mDarknessThreshold as 1 into outDarkPixels.
	 */
	public void detectDarkColors(Mat outDarkPixels, int nDarknessThreshold) {
		Imgproc.threshold(getHSVChannel(CHANNEL_VALUE), outDarkPixels, nDarknessThreshold, 1, Imgproc.THRESH_BINARY);
	}

	/*
	 * Filters the Saturation Channel of HSV and saves all pixels with a
	 * saturation value greater than mSaturationThreshold as 255 into
	 * outStrongColors.
	 */
	public void detectStrongColors(Mat outStrongColors, int nSaturationThreshold) {
		Imgproc.threshold(getHSVChannel(CHANNEL_SATURATION), outStrongColors, nSaturationThreshold, 255, Imgproc.THRESH_BINARY);
	}

	public Mat getHSVChannel(int channel) {
		return mHSVChannels.get(channel);
	}

	public Size getFrameSize() {
		return mFrameSize;
	}

	public boolean didFrameSizeChange() {
		return mFrameSizeChanged;
	}
}