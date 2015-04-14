package com.cleenr.cleen_r.focusObject;

import com.cleenr.cleen_r.objectCategorisation.Color;
import com.cleenr.cleen_r.objectCategorisation.Shape;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class NoFocus extends FocusObject {

    @Override
    public Shape getShapeCategorisation() {
        return Shape.NONE;
    }

    @Override
    public Color getColorCategorisation() {
        return Color.NONE;
    }

    @Override
    public Rect getRect() {
        return new Rect();
    }

    @Override
    public Point getCenter() {
        return new Point();
    }

    @Override
    public Scalar getMeanColorRGBA() {
        return new Scalar(0, 0, 0, 0);
    }

    public Scalar getMeanColorHSV() {
        return new Scalar(0, 0, 0);
    }

    public String toString() {
        return "No Focus found.";
    }

    @Override
    public boolean isValidFocus() {
        return false;
    }

    public Shape getForm()
    {
        return Shape.NONE;
    }
}
