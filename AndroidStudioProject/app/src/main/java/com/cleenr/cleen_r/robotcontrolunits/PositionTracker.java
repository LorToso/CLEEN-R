package com.cleenr.cleen_r.robotcontrolunits;

public class PositionTracker
{
    public enum MOVEMENT_DIRECTION
    {
        FORWARD, BACKWARD, LEFT, RIGHT
    }

    private static final double MAX_NXT_METERS_PER_SECOND = 0.5;

    private double posX, posY;
    private double angle;

    public PositionTracker()
    {
        posX = 0.0;
        posY = 0.0;
        angle = 0.0;
    }

    public void addMovement(MOVEMENT_DIRECTION direction, long duration)
    {
        switch (direction)
        {
            case FORWARD:
                break;
            case BACKWARD:
                break;
            case LEFT:
                break;
            case RIGHT:
                break;
        }
    }

    public void addVector(double x, double y, double rotation)
    {
        posX += x;
        posY += y;
        angle += rotation;
    }

    public void resetPosition()
    {
        posX = 0.0;
        posY = 0.0;
        angle = 0.0;
    }

    public double getX()
    {
        return posX;
    }

    public double getY()
    {
        return posY;
    }

    public double getAngle()
    {
        return angle;
    }
}
