package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import android.util.Log;

public class CleenrUtils {
	/*
	 * Increases a Rectangle by factor delta and checks whether it contains the Point point
	 */
	public static boolean rectContainsWithDelta(Rect rect, Point point, double delta)
	{
		double newWidth = rect.width*delta;
		double deltaWidth = rect.width-newWidth;
		double newHeight = rect.height*delta;
		double deltaHeight = rect.height-newHeight;
		Point newTL = new Point(rect.tl().x-deltaWidth/2, rect.tl().y-deltaHeight/2);
		Point newBR = new Point(rect.br().x+deltaWidth/2, rect.br().y+deltaHeight/2);
		Rect deltaRect = new Rect(newTL, newBR);
		return deltaRect.contains(point);
	}
	
	public static void drawRects(Mat outputFrame, ArrayList<Rect> rects)
	{
		int i = 0;
		
		for(Rect rect : rects)
		{
			drawRect(outputFrame, rect);
			i++;
			Point middle = new Point(rect.x + rect.width/2, rect.y + rect.height/2);
			Log.d("OBJECT FOUND", "OBJECT " + i +  " AT (" + middle.x + "|" + middle.y + ")");
		}
	}
	public static void drawRect(Mat outputFrame, FocusedObject focusedObject)
	{
		if(focusedObject != null)
			drawRect(outputFrame, focusedObject.getRect());
	}
	public static void drawRect(Mat outputFrame, Rect rect)
	{
		if(rect == null)
			return;
		
		Point middle = new Point(rect.x + rect.width/2, rect.y + rect.height/2);
		Core.rectangle(outputFrame, rect.tl(), rect.br(), new Scalar(255,255,0), 5);
		Core.rectangle(outputFrame, new Point(middle.x-2, middle.y-2), new Point(middle.x+2, middle.y+2), new Scalar(255,255,0), 5);
	}
	public static void drawPoint(Mat outputFrame, Point point)
	{
		if(point == null)
			return;
		Core.rectangle(outputFrame, point, point, new Scalar(255,255,0), 1);
	}
	public static Rect getBiggestRect(ArrayList<Rect> rects)
	{
		if(rects.size() == 0)
			return null;
		
		int maxIndex = -1;
		double maxArea = -1;
		int index = 0;
		for(Rect rect : rects)
		{
			double area = rect.area();
			if(area > maxArea)
			{
				maxArea = area;
				maxIndex = index;
			}
			index++;
		}
		return rects.get(maxIndex);
	}
	
	
	public static Size generateSize(Mat rgba)
	{
		return new Size(rgba.cols(), rgba.rows());
	}
}
