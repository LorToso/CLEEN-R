package com.cleenr.cleen_r.robotcontrolunits;

public class PositionTracker
{
    public enum MOVEMENT_DIRECTION
    {
        FORWARD, BACKWARD, LEFT, RIGHT
    }

    private static final double MAX_METERS_PER_SECOND = 0.5;
    private static final double WHEELBASE             = 0.2; // distance between the driving wheels

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
        final double maxDegreesPerSecond = (2.0 * Math.PI * WHEELBASE) * (2.0 * MAX_METERS_PER_SECOND) / (2.0 * Math.PI);

        double radiansTurned = (motorSpeed / 100.0) * maxDegreesPerSecond * (duration / 1000.0);
        if (anticlockwise)
            radiansTurned *= -1;

        addVector(0.0, 0.0, radiansTurned);
    }

    public void addVector(double x, double y, double rotation)
    {
        mPosX += x;
        mPosY += y;
        mAngle += rotation;
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
