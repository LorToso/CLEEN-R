package com.cleenr.cleen_r.objectCategorisation;

import org.opencv.core.MatOfPoint;

/**
 * Absolutely created by lorenzo toso on 23.03.15.
 */
public enum Shape {
    NONE,
    SPHERE,
    CUBE;

    private static final double rectangularAngle = 30;

    /**
     * Returns the Shape of an object, that was detected by the findContours() method.
     * @param contour The contours of the object.
     * @return the Shape of the object.
     */
    public static Shape getObjectShape(MatOfPoint contour){
        double[] angles = calcAngles(contour);

        double maxAngle = angles[0];

        for(double angle : angles)
            maxAngle = Math.max(angle, maxAngle);

        if(maxAngle > rectangularAngle)
            return CUBE;

        return SPHERE;
    }

    private static double[] calcAngles(MatOfPoint contour) {
        double[] angles = new double[contour.rows()];
        int rowCount = contour.rows();

        for(int row=0; row < contour.rows()-2; row++)
        {

            double[] point1 = contour.get(row, 0);
            double[] point2 = contour.get((row+1)%rowCount, 0);
            double[] point3 = contour.get((row+2)%rowCount, 0);
            angles[row] = calcAngle(point1, point2, point3);
        }

        return angles;
    }

    private static double calcAngle(double[] point1, double[] point2, double[] point3) {
        double d1 = getDistance(point1, point2);
        double d2 = getDistance(point2, point3);

        if(d1 == 0 || d2 == 0)
            return 0;

        return Math.atan(d1/d2);
    }

    private static double getDistance(double[] point1, double[] point2) {
        return Math.sqrt(Math.pow(point1[0]-point2[0],2)+Math.pow(point1[1]-point2[1],2));
    }

    public String toString()
    {
        return this.name();
    }
}
