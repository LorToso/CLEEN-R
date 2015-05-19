package com.cleenr.cleen_r;

import android.graphics.PointF;
import android.util.Log;

import com.cleenr.cleen_r.objectCategorisation.Category;
import com.cleenr.cleen_r.objectCategorisation.Color;
import com.cleenr.cleen_r.objectCategorisation.Shape;
import com.cleenr.cleen_r.robotcontrolunits.NxtControlUnit;
import com.cleenr.cleen_r.robotcontrolunits.RobotControlUnit;
import com.cleenr.cleen_r.workphase.Idle;
import com.cleenr.cleen_r.workphase.SearchingObject;
import com.cleenr.cleen_r.workphase.WorkPhase;

public class RobotWorker implements Runnable {


    private WorkPhase mWorkPhase;
    private final RobotControlUnit mRobotControlUnit;
    private final CleenrBrain mBrain;
    private boolean mStopOnNextTurn = false;
    private long workStepTime = 250;

    public RobotWorker(CleenrBrain brain) {
        mBrain = brain;
        mWorkPhase = new Idle(this);
        mRobotControlUnit = new NxtControlUnit(brain.mNxtTalker, brain.mPositionTracker);

        Globals.searchCategories.put(new Category(Shape.SPHERE, Color.YELLOW), new PointF(0.0f, 0.0f));
        //Globals.searchCategories.put(new Category(Shape.SPHERE, Color.BLUE), new PointF(0.0f, 0.0f));
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
        waitForSearchedCategories();

        switchWorkphase(new SearchingObject(this));

        while (!mStopOnNextTurn) {
            mWorkPhase.executeWork(mBrain.getFocusedObject(), mRobotControlUnit, mBrain);
            try
            {
                Thread.sleep(workStepTime);
            }
            catch (InterruptedException e)
            {
                stopOnNextTurn();
            }
        }
        mRobotControlUnit.stopMoving();
        mStopOnNextTurn = false;
        Log.d("RobotWorker", "Stopping now");
    }

    private void waitForSearchedCategories()
    {
        while (Globals.searchCategories.isEmpty())
            Thread.yield();
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
