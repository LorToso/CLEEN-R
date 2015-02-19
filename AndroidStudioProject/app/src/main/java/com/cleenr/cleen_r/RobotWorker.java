package com.cleenr.cleen_r;

import com.cleenr.cleen_r.robotcontrolunits.RobotControlUnit;
import com.cleenr.cleen_r.robotcontrolunits.TempControlUnit;
import com.cleenr.cleen_r.workphase.Idle;
import com.cleenr.cleen_r.workphase.SearchingObject;
import com.cleenr.cleen_r.workphase.WorkPhase;

public class RobotWorker implements Runnable {
    private WorkPhase mWorkPhase = new Idle(this);
    private RobotControlUnit mRobotControlUnit = new TempControlUnit();
    private CleenrBrain mBrain;

    public RobotWorker(CleenrBrain brain) {
        mBrain = brain;
    }

    public void switchWorkphase(WorkPhase newWorkPhase) {
        mWorkPhase = newWorkPhase;
    }

    @Override
    public void run() {
        workLoop();
    }

    private void workLoop() {
        waitForFirstFrame();

        switchWorkphase(new SearchingObject(this));

        while (true) {
            mWorkPhase.executeWork(mBrain.getFocusedObject(), mRobotControlUnit);
        }
    }

    private void waitForFirstFrame() {
        while (!mBrain.isCamerainitialized)
            Thread.yield();
    }
}
