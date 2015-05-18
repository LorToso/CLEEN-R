package com.cleenr.cleen_r.workphase;

import android.util.Log;

import com.cleenr.cleen_r.CleenrBrain;
import com.cleenr.cleen_r.RobotWorker;
import com.cleenr.cleen_r.focusObject.FocusObject;
import com.cleenr.cleen_r.robotcontrolunits.RobotControlUnit;

public class SearchingObject extends WorkPhase {

    public SearchingObject(RobotWorker worker) {
        super(worker);
    }

    @Override
    public void executeWork(FocusObject focusObject, RobotControlUnit controlUnit, CleenrBrain brain) {

        if (!focusObject.isValidFocus() || !focusObject.isSearchedObject(brain)) {
            controlUnit.turnRight();
            return;
        }
        mRobotWorker.switchWorkphase(new GoingToObject(mRobotWorker));
    }


}
