package com.cleenr.cleen_r.robotcontrolunits;

public class PositionTracker
{
    public enum MOVEMENT_DIRECTION
    {
        FORWARD, BACKWARD, LEFT, RIGHT
    }

    private static final double MAX_NXT_METERS_PER_SECOND = 0.5;

    private double posX, posY;
    private double angle; // mathematically negative, clockwise

    public PositionTracker()
    {
        posX = 0.0;
        posY = 0.0;
        angle = 0.0;
    }

    public void addMovement(MOVEMENT_DIRECTION direction, byte motorSpeed, long duration)
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

    private void addStraightMovement(boolean backward, byte motorSpeed, long duration)
    {
        // With angle=0, a forward movement is in y direction
        // (distance in meters)
        double distance = (motorSpeed / 100.0) * MAX_NXT_METERS_PER_SECOND * (duration / 1000.0);
        double yDistance = Math.cos(angle) * distance;
        double xDistance = Math.sin(angle) * distance;

        addVector(xDistance, yDistance, 0.0);
    }

    private double addRotation(byte motorSpeed, long duration)
    {
        // both motors are moving, in opposite directions
        final double maxDegreesPerSecond = 2.0 * Math.PI * MAX_NXT_METERS_PER_SECOND / 360.0;
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
