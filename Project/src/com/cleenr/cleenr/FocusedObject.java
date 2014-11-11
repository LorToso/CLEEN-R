package com.cleenr.cleenr;

import java.util.ArrayList;

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
	
	
	

	public static void filterSimilarSize(ArrayList<Rect> candidates, Rect focusedArea, double areaSimilarity) {
		for(int i = 0; i < candidates.size(); i++)
		{
			Rect rect = candidates.get(i);
			
			if(	!(
					focusedArea.area()*areaSimilarity > rect.area()
				&&	focusedArea.area()*(2-areaSimilarity) < rect.area()
				))
			{
				candidates.remove(i);
				i--;
			}
			
		}		
	}
	public static void filterSimilarColor(ArrayList<Rect> candidates, Scalar focusedColor, Mat fullRGBAImage, double colorSimilarity) {
		double minR = focusedColor.val[0]*(2-colorSimilarity);
		double maxR = focusedColor.val[0]*(2-colorSimilarity);
		double minG = focusedColor.val[1]*(2-colorSimilarity);
		double maxG = focusedColor.val[1]*(2-colorSimilarity);
		double minB = focusedColor.val[2]*(2-colorSimilarity);
		double maxB = focusedColor.val[2]*(2-colorSimilarity);
		
		for(int i = 0; i < candidates.size(); i++)
		{
			Rect rect = candidates.get(i);
			double rectValues[] = Core.mean(new Mat(fullRGBAImage, rect)).val;
			
			if(	!(
					(rectValues[0] > minR && rectValues[0] < maxR)
				&&	(rectValues[1] > minG && rectValues[1] < maxG)
				&&	(rectValues[2] > minB && rectValues[2] < maxB)
				))
			{
				candidates.remove(i);
				i--;
			}
			
		}
	}
	public static void filterContainingPoint(ArrayList<Rect> candidates, Point point, double areaDelta)
	{
		for(int i = 0; i < candidates.size(); i++)
		{
			Rect rect = candidates.get(i);
			if(!CleenrUtils.rectContainsWithDelta(rect, point, areaDelta))
			{
				candidates.remove(i);
				i--;
			}
		}
	}
	
}
