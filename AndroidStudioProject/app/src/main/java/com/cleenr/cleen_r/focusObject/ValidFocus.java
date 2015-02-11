package com.cleenr.cleen_r.focusObject;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class ValidFocus extends FocusObject {
	private Mat mRGBA;
	private Rect mArea;
	private Point mCenter;
	private Scalar mCoreColor;

	public ValidFocus() {
		mRGBA = new Mat();
		mArea = new Rect();
		mCenter = new Point();
	}

	protected ValidFocus(Mat rgba, Rect area) {
		mRGBA = rgba;
		mArea = area;
		mCenter = calcCenter(area);
		mCoreColor = Core.mean(mRGBA);
	}


	private Point calcCenter(Rect area) {
		return new Point(area.x + area.width / 2, area.y + area.height / 2);
	}

	public Rect getRect() {
		return mArea;
	}

	public Point getCenter() {
		return mCenter;
	}

	public Scalar getMeanColor() {
		return mCoreColor;
	}

	public boolean isInRange() {
		// TODO
		return false;
	}

	public String toString()
	{
		return "FocusObject at " +  getCenter() + ". Width = " + getRect().width + ". Height = " + getRect().height + ". Mean Color: " + getMeanColor() + ".";
	}

	@Override
	public boolean isValidFocus() {
		return true;
	}
}
