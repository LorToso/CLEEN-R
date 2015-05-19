package com.cleenr.cleen_r.workphase;

import android.graphics.Point;
import android.graphics.PointF;

import com.cleenr.cleen_r.CleenrBrain;
import com.cleenr.cleen_r.Globals;
import com.cleenr.cleen_r.RobotWorker;
import com.cleenr.cleen_r.focusObject.FocusObject;
import com.cleenr.cleen_r.objectCategorisation.Category;
import com.cleenr.cleen_r.robotcontrolunits.RobotControlUnit;

public class GoingToDestination extends WorkPhase {

    public GoingToDestination(RobotWorker worker) {
        super(worker);
    }

    @Override
    public void executeWork(FocusObject focusObject, RobotControlUnit controlUnit, CleenrBrain brain) {
        Category cat = focusObject.getCategory();

        if (!Globals.searchCategories.containsKey(cat))
        {
            controlUnit.openClaw();
            mRobotWorker.switchWorkphase(new SearchingObject(mRobotWorker));
            return;
        }

        PointF targetPoint = Globals.searchCategories.get(cat);
        controlUnit.driveToPoint(targetPoint);
        Globals.searchCategories.remove(cat);
        mRobotWorker.switchWorkphase(new DroppingOffObject(mRobotWorker));
    }
}
