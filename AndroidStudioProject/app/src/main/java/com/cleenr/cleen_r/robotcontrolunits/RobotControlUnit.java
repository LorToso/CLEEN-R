package com.cleenr.cleen_r.robotcontrolunits;

import android.graphics.PointF;

import com.cleenr.cleen_r.focusObject.FocusObject;


public interface RobotControlUnit
{
    void turnRight();

    void closeClaw();

    void driveForward();

    void driveBackward();

    boolean isMoving();

    void openClaw();

    void turnLeft();

    void centerObject(FocusObject focusObject);

    void facePoint(PointF targetPoint);

    void driveToPoint(PointF targetPoint);

    void stopMoving();
}
