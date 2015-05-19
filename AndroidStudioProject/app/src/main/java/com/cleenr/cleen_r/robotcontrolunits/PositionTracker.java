package com.cleenr.cleen_r.robotcontrolunits;

import com.cleenr.cleen_r.Utils;

public class PositionTracker
{
    public enum MOVEMENT_DIRECTION
    {
        FORWARD, BACKWARD, LEFT, RIGHT
    }

    public static final double MAX_METERS_PER_SECOND = 1.0/3.0;
    public static final double MAX_DEGREES_PER_SECOND = Math.PI;

    private double mPosX, mPosY;
    private double mAngle; // mathematically negative radians, clockwise

    public PositionTracker()
    {
        mPosX = 0.0;
        mPosY = 0.0;
        mAngle = 0.0;
    }

    public void addMovement(MOVEMENT_DIRECTION direction, byte motorSpeed, long duration)
    {
        switch (direction)
        {
            case FORWARD:
                addStraightMovement(false, motorSpeed, duration);
                break;
            case BACKWARD:
                addStraightMovement(true, motorSpeed, duration);
                break;
            case LEFT:
                addCircularMovement(true, motorSpeed, duration);
                break;
            case RIGHT:
                addCircularMovement(false, motorSpeed, duration);
                break;
        }
    }

    private void addStraightMovement(boolean backward, byte motorSpeed, long duration)
    {
        // With mAngle=0, a forward movement is in y direction
        // (distance in meters)
        double distance = (motorSpeed / 100.0) * MAX_METERS_PER_SECOND * (duration / 1000.0);
        if (backward)
            distance *= -1;

        double yDistance = Math.cos(mAngle) * distance;
        double xDistance = Math.sin(mAngle) * distance;

        addVector(xDistance, yDistance, 0.0);
    }

    private void addCircularMovement(boolean anticlockwise, byte motorSpeed, long duration)
    {
        // both motors are moving, in opposite directions
        double radiansTurned = (motorSpeed / 100.0) * MAX_DEGREES_PER_SECOND * (duration / 1000.0);
        if (anticlockwise)
            radiansTurned *= -1;

        addVector(0.0, 0.0, radiansTurned);
    }

    public void addVector(double x, double y, double rotation)
    {
        mPosX += x;
        mPosY += y;
        mAngle = Utils.normalizeAngle(mAngle + rotation);
    }

    public void resetPosition()
    {
        mPosX = 0.0;
        mPosY = 0.0;
        mAngle = 0.0;
    }

    public double getX()
    {
        return mPosX;
    }

    public double getY()
    {
        return mPosY;
    }

    public double getAngle()
    {
        return mAngle;
    }
}
