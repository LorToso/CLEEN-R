package com.cleenr.cleen_r.robotcontrolunits;

import com.cleenr.cleen_r.focusObject.FocusObject;


public interface RobotControlUnit
{
    void turnRight();

    void closeClaw();

    void driveForward();

    void driveBackward();

    boolean hasObjectInClaw();

    void openClaw();

    void turnLeft();

    void centerObject(FocusObject focusObject);

    void returnToStartingPoint();

    void stopMoving();
}
