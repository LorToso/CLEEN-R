package com.cleenr.cleen_r;

public final class Utils
{
    public static double normalizeAngle(double a)
    {
        while (a < 0.0)
            a += 2.0 * Math.PI;
        while (a > 2.0 * Math.PI)
            a -= 2.0 * Math.PI;
        return a;
    }
}
