package com.cleenr.cleen_r;

import com.cleenr.cleen_r.nxt.NxtTalker;
import com.cleenr.cleen_r.robotcontrolunits.RobotControlUnit;
import com.cleenr.cleen_r.robotcontrolunits.NxtControlUnit;
import com.cleenr.cleen_r.workphase.Idle;
import com.cleenr.cleen_r.workphase.SearchingObject;
import com.cleenr.cleen_r.workphase.WorkPhase;

public class RobotWorker implements Runnable {
    private WorkPhase mWorkPhase;
    private final RobotControlUnit mRobotControlUnit;
    private final CleenrBrain mBrain;

    public RobotWorker(CleenrBrain brain) {
        mBrain = brain;
        mWorkPhase = new Idle(this);
        mRobotControlUnit = new NxtControlUnit(brain.mNxtTalker);
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
        while (!mBrain.isCameraInitialized)
            Thread.yield();
    }
}
