package com.cleenr.cleenr;

public class WorkLoop implements Runnable{
	private WorkPhase mWorkPhase 				= WorkPhase.INITIALIZING;
	private RobotControlUnit mRobotControlUnit 	= new TempControlUnit();
	private CLEENRBrain mBrain;
	
	public WorkLoop(CLEENRBrain brain)
	{
		mBrain = brain;
	}
	
	
	@Override
	public void run() {
		workLoop();
	}

	private void workLoop() {

		while (!CLEENRBrain.isCameraInitialized())
			Thread.yield();

		mWorkPhase = WorkPhase.SEARCHING_OBJECT;

		while (true) {
			switch (mWorkPhase) {
			case SEARCHING_OBJECT:
				searchObject();
				break;
			case GOING_TO_OBJECT:
				driveTowardsObject();
				break;
			case PICKING_UP_OBJECT:
				pickUpObject();
				break;
			case SEARCHING_DESTINATION:
				searchDestination();
				break;
			case GOING_TO_DESTINATION:
				driveTowardsDestination();
				break;
			case DROPPING_OFF_OBJECT:
				dropOffObject();
				break;
			default:
				break;
			}
		}
	}

	private void searchObject() {
		int maximumTurnRate = 100; // TODO: Make this static

		int currentTurnRate = 0;
		while (mBrain.getFocusedObject() == null) {
			mRobotControlUnit.turnRight();
			if (++currentTurnRate > maximumTurnRate) {
				// TODO: What if no object was found after 1 complete Rotation?
				// mFocusObjectFinder.lowerCriteria();
				currentTurnRate = 0;
			}
		}
		mWorkPhase = WorkPhase.GOING_TO_OBJECT;
	}

	private void driveTowardsObject() {

		FocusObject tempFocus = mBrain.getFocusedObject();

		if (tempFocus == null) {
			mWorkPhase = WorkPhase.SEARCHING_OBJECT;
			return;
		}

		if (tempFocus.isInRange()) {
			mWorkPhase = WorkPhase.PICKING_UP_OBJECT;
			return;
		}

		if (!tempFocus.isHorizontallyCentered()) {
			mRobotControlUnit.centerObject(tempFocus);
			return;
		}

		mRobotControlUnit.driveForward();

	}

	private void pickUpObject() {
		mRobotControlUnit.centerObject(mBrain.getFocusedObject());
		mRobotControlUnit.closeClaw();
		if (!mRobotControlUnit.hasObjectInClaw()) {
			mRobotControlUnit.openClaw();
			mWorkPhase = WorkPhase.SEARCHING_OBJECT;
			return;
		}
		mWorkPhase = WorkPhase.SEARCHING_DESTINATION;
	}

	private void searchDestination() {
		// TODO
	}

	private void driveTowardsDestination() {
		// TODO
	}

	private void dropOffObject() {
		// TODO
	}
}
