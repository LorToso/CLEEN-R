package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

public class CleenrUtils {

	private static Scalar defaultDrawColor = new Scalar(255, 255, 0);

	/*
	 * Increases a Rectangle by factor delta and checks whether it contains the
	 * Point point
	 */
	public static boolean rectContainsWithDelta(Rect rect, Point point, double delta) {
		double newWidth = rect.width * delta;
		double deltaWidth = rect.width - newWidth;
		double newHeight = rect.height * delta;
		double deltaHeight = rect.height - newHeight;
		Point newTL = new Point(rect.tl().x - deltaWidth / 2, rect.tl().y - deltaHeight / 2);
		Point newBR = new Point(rect.br().x + deltaWidth / 2, rect.br().y + deltaHeight / 2);
		Rect deltaRect = new Rect(newTL, newBR);
		return deltaRect.contains(point);
	}

	public static void drawFocusObjects(Mat outputFrame, ArrayList<FocusObject> objects, Scalar color) {
		for (FocusObject focus : objects)
			drawFocusObject(outputFrame, focus, color);
	}
	public static void drawFocusObjects(Mat outputFrame, ArrayList<FocusObject> objects) {
		for (FocusObject focus : objects)
			drawFocusObject(outputFrame, focus);
	}
	public static void drawFocusObject(Mat outputFrame, FocusObject object, Scalar color) {
		drawRect(outputFrame, object.getRect(), color);
		drawPoint(outputFrame, object.getCenter(), color);
	}
	public static void drawFocusObject(Mat outputFrame, FocusObject object) {
		drawRect(outputFrame, object.getRect());
		drawPoint(outputFrame, object.getCenter());
	}

	public static void drawRects(Mat outputFrame, ArrayList<Rect> rects) {
		drawRects(outputFrame, rects, defaultDrawColor);
	}

	public static void drawRects(Mat outputFrame, ArrayList<Rect> rects, Scalar scalar) {
		for (Rect r : rects)
			drawRect(outputFrame, r, scalar);
	}

	public static void drawRect(Mat outputFrame, Rect rect) {
		drawRect(outputFrame, rect, defaultDrawColor);
	}

	public static void drawRect(Mat outputFrame, Rect rect, Scalar color) {
		Core.rectangle(outputFrame, rect.tl(), rect.br(), color, 5);
	}

	public static void drawPoint(Mat outputFrame, Point point) {
		drawRect(outputFrame, new Rect(point, point));
	}

	public static void drawPoint(Mat outputFrame, Point point, Scalar color) {
		drawRect(outputFrame, new Rect(point, point), color);
	}

	public static Size generateSize(Mat rgba) {
		return new Size(rgba.cols(), rgba.rows());
	}

}
