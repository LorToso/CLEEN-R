package com.cleenr.cleen_r;

import android.util.Log;

import com.cleenr.cleen_r.robotcontrolunits.RobotControlUnit;
import com.cleenr.cleen_r.robotcontrolunits.NxtControlUnit;
import com.cleenr.cleen_r.workphase.Idle;
import com.cleenr.cleen_r.workphase.SearchingObject;
import com.cleenr.cleen_r.workphase.WorkPhase;

public class RobotWorker implements Runnable {


    private WorkPhase mWorkPhase;
    private final RobotControlUnit mRobotControlUnit;
    private final CleenrBrain mBrain;
    private boolean mStopOnNextTurn = false;


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

        while (!mStopOnNextTurn) {
            mWorkPhase.executeWork(mBrain.getFocusedObject(), mRobotControlUnit);
        }
        mStopOnNextTurn = false;
        Log.d("RobotWorker", "Stopping now");
    }

    private void waitForFirstFrame() {
        while (!mBrain.isCamerainitialized)
            Thread.yield();
    }
    public void stopOnNextTurn()
    {
        mStopOnNextTurn = true;
    }

}
