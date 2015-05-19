package com.cleenr.cleen_r.workphase;

import android.util.Log;

import com.cleenr.cleen_r.CleenrBrain;
import com.cleenr.cleen_r.RobotWorker;
import com.cleenr.cleen_r.focusObject.FocusObject;
import com.cleenr.cleen_r.robotcontrolunits.RobotControlUnit;

public abstract class WorkPhase {
    RobotWorker mRobotWorker;

    public WorkPhase(RobotWorker worker) {
        mRobotWorker = worker;
        Log.i("work phase", "new phase: " + this.getClass().getSimpleName());
    }

    public abstract void executeWork(FocusObject focusObject, RobotControlUnit controlUnit, CleenrBrain brain);
}
