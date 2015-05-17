package com.cleenr.cleen_r.robotcontrolunits;

import android.util.Log;

import com.cleenr.cleen_r.CleenrImage;
import com.cleenr.cleen_r.Utils;
import com.cleenr.cleen_r.focusObject.FocusObject;
import com.cleenr.cleen_r.nxt.NxtTalker;

import org.opencv.core.Point;

public class NxtControlUnit implements RobotControlUnit
{
    private final String TAG = "NxtControlUnit";

    private final NxtTalker       mNxtTalker;
    private final PositionTracker mPosTracker;

    private long          movementStartingTime = System.currentTimeMillis();
    private RobotMovement currentMovement      = RobotMovement.STOP;

    private final byte CLAW_MOTOR        = NxtTalker.MOTOR_PORT_A;
    private final byte LEFT_WHEEL_MOTOR  = NxtTalker.MOTOR_PORT_C;
    private final byte RIGHT_WHEEL_MOTOR = NxtTalker.MOTOR_PORT_B;

    private final byte MOTOR_SPEED      = 50; // -100 to 100
    private final byte MOTOR_SPEED_SLOW = 20; // -100 to 100
    private final byte MOTOR_SPEED_CLAW = 30; // -100 to 100

    private final double STARTING_POINT_POSITION_TOLERANCE = 0.1; // in meters
    private final double STARTING_POINT_ANGLE_TOLERANCE    = Math.PI / 180.0; // 1 degree

    public static final byte SYNC_MODE_TURNING = NxtTalker.MOTOR_REG_MODE_NONE;
    public static final byte SYNC_MODE_DRIVING = NxtTalker.MOTOR_REG_MODE_NONE;

    public NxtControlUnit(NxtTalker nxtTalker, PositionTracker posTracker)
    {
        mNxtTalker = nxtTalker;
        mPosTracker = posTracker;
    }

    public void turnRight()
    {
        Log.d("ControlUnit", "Turning right");
        mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, MOTOR_SPEED, SYNC_MODE_TURNING);
        mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED), SYNC_MODE_TURNING);
        refreshMovement(RobotMovement.TURN_RIGHT, MOTOR_SPEED);
    }

    public void closeClaw()
    {
        try
        {
            Log.d("ControlUnit", "Closing claw");
            if (isMoving())
                stopMoving();
            mNxtTalker.setMotorSpeed(CLAW_MOTOR, MOTOR_SPEED_CLAW);
            Thread.sleep(250);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void driveForward()
    {
        Log.d("ControlUnit", "Driving forward");
        mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, MOTOR_SPEED, SYNC_MODE_DRIVING);
        mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, MOTOR_SPEED, SYNC_MODE_DRIVING);
        refreshMovement(RobotMovement.DRIVE_FORWARD, MOTOR_SPEED);
    }

    public void driveBackward()
    {
        Log.d("ControlUnit", "Driving backward");
        mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED), SYNC_MODE_DRIVING);
        mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED), SYNC_MODE_DRIVING);
        refreshMovement(RobotMovement.DRIVE_FORWARD, MOTOR_SPEED);
    }

    public boolean hasObjectInClaw()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isMoving()
    {
        if (currentMovement == RobotMovement.STOP)
            return false;
        return true;
    }

    public void openClaw()
    {
        try
        {
            Log.d("ControlUnit", "Opening claw");
            if (isMoving())
                stopMoving();
            mNxtTalker.setMotorSpeed(CLAW_MOTOR, (byte) (-1 * MOTOR_SPEED_CLAW));
            Thread.sleep(250);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void turnLeft()
    {
        Log.d("ControlUnit", "Turning left");
        mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED), SYNC_MODE_TURNING);
        mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, MOTOR_SPEED, SYNC_MODE_TURNING);
        refreshMovement(RobotMovement.TURN_LEFT, MOTOR_SPEED);
    }

    public void centerObject(FocusObject focusObject)
    {
        Point focusCenter = focusObject.getCenter();

        int imageWidth = (int) CleenrImage.getInstance().getFrameSize().width;
        double minimumValidArea = (2 - FocusObject.sHorizonalCentrationTolerance) * imageWidth / 2;
        double maximumValidArea = FocusObject.sHorizonalCentrationTolerance * imageWidth / 2;

        // CAREFUL!!!! Point(0|0) is on the Bottom right, because FUCK YOU
        if (focusCenter.x < minimumValidArea)
            turnLeftSlowly();
        else if (focusCenter.x > maximumValidArea)
            turnRightSlowly();
    }

    private void turnRightSlowly()
    {
        Log.d("ControlUnit", "Turning right");
        mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, MOTOR_SPEED_SLOW, SYNC_MODE_TURNING);
        mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED_SLOW), SYNC_MODE_TURNING);
        refreshMovement(RobotMovement.TURN_RIGHT_SLOWLY, MOTOR_SPEED_SLOW);
    }


    private void turnLeftSlowly()
    {
        Log.d("ControlUnit", "Turning left");
        mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED_SLOW), SYNC_MODE_TURNING);
        mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, MOTOR_SPEED_SLOW, SYNC_MODE_TURNING);
        refreshMovement(RobotMovement.TURN_LEFT_SLOWLY, MOTOR_SPEED_SLOW);
    }

    public void returnToStartingPoint()
    {
        // turn the robot until it faces the starting point,
        // which is equal to a robot angle of 180 degrees plus
        // the angle from starting point to robot

        double x = mPosTracker.getX();
        double y = mPosTracker.getY();
        double robotAngle = mPosTracker.getAngle();

        // (radians from the y-axes clockwise, like in PositionTracker)
        double angleStartingPointToRobot = Math.atan(x / y);

        double targetAngle = Utils.normalizeAngle(angleStartingPointToRobot + Math.PI);
        double radiansToTurn = Utils.normalizeAngle(targetAngle - robotAngle);

        Log.d(TAG, String.format("target angle: %.3f\u00b0, to turn: %.3f\u00b0",
                                 Math.toDegrees(targetAngle), Math.toDegrees(radiansToTurn)));

        if (radiansToTurn > Math.PI)
        {
            // turning left is the shorter way
            while (Utils.normalizeAngle(targetAngle - mPosTracker.getAngle()) >
                    STARTING_POINT_ANGLE_TOLERANCE / 2.0)
                turnLeft();
        }
        else
        {
            // turning right is the shorter way
            while (Utils.normalizeAngle(targetAngle - mPosTracker.getAngle()) <
                    (2.0 * Math.PI) - (STARTING_POINT_ANGLE_TOLERANCE / 2.0))
                turnRight();
        }

        // the robot should be facing the starting point now
        // (within the tolerance), start moving towards it

        while (
                mPosTracker.getX() > STARTING_POINT_POSITION_TOLERANCE / 2.0 ||
                        mPosTracker.getX() < -1 * STARTING_POINT_POSITION_TOLERANCE / 2.0 ||
                        mPosTracker.getY() > STARTING_POINT_POSITION_TOLERANCE / 2.0 ||
                        mPosTracker.getY() < -1 * STARTING_POINT_POSITION_TOLERANCE / 2.0
                )
        {
            driveForward();
            // TODO don't overreach and drive until the end of days!
        }
    }

    @Override
    public void stopMoving()
    {
        Log.d("ControlUnit", "Stopping");
        mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
        refreshMovement(RobotMovement.TURN_RIGHT_SLOWLY, (byte) 0);
    }

    private void refreshMovement(RobotMovement nextMovement, byte motorSpeed)
    {
        long now = System.currentTimeMillis();
        long timePassed = now - movementStartingTime;

        if (currentMovement == RobotMovement.STOP)
        {
            // starting to move, haven't travelled any distance yet
            movementStartingTime = now;
            currentMovement = nextMovement;
            return;
        }

        // still moving in the same direction, changing direction or stopping
        // either way, add the previous movement to the position
        switch (currentMovement)
        {
            case DRIVE_FORWARD:
                mPosTracker.addMovement(PositionTracker.MOVEMENT_DIRECTION.FORWARD, motorSpeed, timePassed);
                break;
            case DRIVE_BACKWARD:
                mPosTracker.addMovement(PositionTracker.MOVEMENT_DIRECTION.BACKWARD, motorSpeed, timePassed);
                break;
            case TURN_LEFT:
            case TURN_LEFT_SLOWLY:
                mPosTracker.addMovement(PositionTracker.MOVEMENT_DIRECTION.LEFT, motorSpeed, timePassed);
                break;
            case TURN_RIGHT:
            case TURN_RIGHT_SLOWLY:
                mPosTracker.addMovement(PositionTracker.MOVEMENT_DIRECTION.RIGHT, motorSpeed, timePassed);
                break;
        }
        movementStartingTime = now;
        currentMovement = nextMovement;
    }
}
