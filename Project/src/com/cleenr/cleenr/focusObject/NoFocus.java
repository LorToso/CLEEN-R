package com.cleenr.cleenr.focusObject;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class NoFocus extends FocusObject {

	@Override
	public Rect getRect() {
		return new Rect();
	}

	@Override
	public Point getCenter() {
		return new Point();
	}

	@Override
	public Scalar getMeanColor() {
		return new Scalar(0,0,0);
	}

	@Override
	public boolean isInRange() {
		return false;
	}
	
	public String toString()
	{
		return "No Focus found.";
	}

	@Override
	public boolean isValidFocus() {
		return false;
	}
	
}
