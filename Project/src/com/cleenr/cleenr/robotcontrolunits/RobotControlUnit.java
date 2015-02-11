package com.cleenr.cleenr.robotcontrolunits;

import com.cleenr.cleenr.focusObject.FocusObject;


public interface RobotControlUnit {
	public boolean isClawClosed();

	public void turnRight();

	public void closeClaw();

	public void driveForward();

	public boolean hasObjectInClaw();

	public void openClaw();

	public void turnLeft();
	public void centerObject(FocusObject focusObject);
}
