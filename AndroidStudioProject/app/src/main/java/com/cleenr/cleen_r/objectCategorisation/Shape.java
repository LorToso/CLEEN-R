package com.cleenr.cleen_r.objectCategorisation;

import org.opencv.core.MatOfPoint;

/**
 * Absolutely created by lorenzo toso on 23.03.15.
 */
public enum Shape {
    NONE,
    SPHERE,
    CUBE;

    /**
     * Returns the Shape of an object, that was detected by the findContours() method.
     * @param contour The contours of the object.
     * @return the Shape of the object.
     */
    public static Shape getObjectShape(MatOfPoint contour){
        return NONE;
        // TODO:

    }

    public String toString()
    {
        return this.name();
    }
}
