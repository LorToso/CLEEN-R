package com.cleenr.cleenr;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class FocusedObject {

	Mat mRGBA;
	Rect mArea;
	Point mCenter;
	public FocusedObject()
	{
		mRGBA 	= new Mat();
		mArea 	= new Rect();
		mCenter = new Point();
	}	
	public FocusedObject(Mat rgba, Rect area)
	{
		mRGBA = rgba;
		mArea = area;
		mCenter = calcCenter(area);
	}
	public static FocusedObject createFromFullImage(Mat rgbaFullImage, Rect area)
	{
		if(area == null)
			return null;
			
		Mat tmp = new Mat(rgbaFullImage, area);
		Mat rgba = new Mat(tmp.rows(), tmp.cols(), tmp.type());
		tmp.copyTo(rgba);
		return new FocusedObject(rgba, area);
	}
	public Scalar getMeanColor()
	{
		return Core.mean(mRGBA);
	}
	private Point calcCenter(Rect area)
	{
		return new Point(area.x + area.width/2, area.y + area.height/2);
	}
	public Rect getRect()
	{
		return mArea;
	}
	
}
