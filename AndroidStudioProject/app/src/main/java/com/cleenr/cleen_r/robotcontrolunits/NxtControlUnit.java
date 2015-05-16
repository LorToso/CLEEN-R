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

    private RobotAction prevAction = RobotAction.STOP;

    private final byte CLAW_MOTOR        = NxtTalker.MOTOR_PORT_A;
    private final byte LEFT_WHEEL_MOTOR  = NxtTalker.MOTOR_PORT_C;
    private final byte RIGHT_WHEEL_MOTOR = NxtTalker.MOTOR_PORT_B;

    private final byte MOTOR_SPEED      = 50; // -100 to 100
    private final byte MOTOR_SPEED_SLOW = 20; // -100 to 100

    private final long SLEEP_TIME_TURNING = 100;
    private final long SLEEP_TIME_DRIVING = 200;

    private final double STARTING_POINT_POSITION_TOLERANCE = 0.1; // in meters
    private final double STARTING_POINT_ANGLE_TOLERANCE    = Math.PI / 180.0; // 1 degree

    public static final byte SYNC_MODE_TURNING = NxtTalker.MOTOR_REG_MODE_NONE;
    public static final byte SYNC_MODE_DRIVING = NxtTalker.MOTOR_REG_MODE_NONE;

    public NxtControlUnit(NxtTalker nxtTalker, PositionTracker posTracker)
    {
        mNxtTalker = nxtTalker;
        mPosTracker = posTracker;
    }

    public boolean isClawClosed()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void turnRight(long sleepTime)
    {
        try
        {
            Log.d("ControlUnit", "Turning right");
            mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, MOTOR_SPEED, SYNC_MODE_TURNING);
            mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED), SYNC_MODE_TURNING);

            Thread.sleep(sleepTime);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);

            mPosTracker.addMovement(PositionTracker.MOVEMENT_DIRECTION.RIGHT, MOTOR_SPEED, sleepTime);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        prevAction = RobotAction.TURN_RIGHT;
    }

    public void turnRight()
    {
        turnRight(SLEEP_TIME_TURNING);
    }

    public void closeClaw()
    {
        try
        {
            Log.d("ControlUnit", "Closing claw");
            mNxtTalker.setMotorSpeed(CLAW_MOTOR, MOTOR_SPEED);
            Thread.sleep(250);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        prevAction = RobotAction.CLOSE_CLAW;
    }

    public void driveForward(long sleepTime)
    {
        try
        {
            Log.d("ControlUnit", "Driving forward");
            mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, MOTOR_SPEED, SYNC_MODE_DRIVING);
            mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, MOTOR_SPEED, SYNC_MODE_DRIVING);

            Thread.sleep(sleepTime);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);

            mPosTracker.addMovement(PositionTracker.MOVEMENT_DIRECTION.FORWARD, MOTOR_SPEED, sleepTime);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        prevAction = RobotAction.DRIVE_FORWARD;
    }

    public void driveForward()
    {
        driveForward(SLEEP_TIME_DRIVING);
    }

    public void driveBackward(long sleepTime)
    {
        try
        {
            Log.d("ControlUnit", "Driving backward");
            mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED), SYNC_MODE_DRIVING);
            mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED), SYNC_MODE_DRIVING);

            Thread.sleep(sleepTime);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);

            mPosTracker.addMovement(PositionTracker.MOVEMENT_DIRECTION.BACKWARD, MOTOR_SPEED, sleepTime);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        prevAction = RobotAction.DRIVE_FORWARD;
    }

    public void driveBackward()
    {
        driveBackward(SLEEP_TIME_DRIVING);
    }

    public boolean hasObjectInClaw()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void openClaw()
    {
        // TODO
        try
        {
            Log.d("ControlUnit", "Opening claw");
            mNxtTalker.setMotorSpeed(CLAW_MOTOR, (byte) (-1 * MOTOR_SPEED));
            Thread.sleep(250);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        prevAction = RobotAction.OPEN_CLAW;
    }

    public void turnLeft(long sleepTime)
    {
        try
        {
            Log.d("ControlUnit", "Turning left");
            mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED), SYNC_MODE_TURNING);
            mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, MOTOR_SPEED, SYNC_MODE_TURNING);

            Thread.sleep(sleepTime);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);

            mPosTracker.addMovement(PositionTracker.MOVEMENT_DIRECTION.LEFT, MOTOR_SPEED, sleepTime);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        prevAction = RobotAction.TURN_LEFT;
    }

    public void turnLeft()
    {
        turnLeft(SLEEP_TIME_TURNING);
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

    @Override
    public void repeatLastAction()
    {
        switch (prevAction)
        {
            case TURN_LEFT:
                turnLeft();
                break;
            case TURN_RIGHT:
                turnRight();
                break;
            case TURN_LEFT_SLOWLY:
                turnLeftSlowly();
                break;
            case TURN_RIGHT_SLOWLY:
                turnRightSlowly();
                break;
            case OPEN_CLAW:
                openClaw();
                break;
            case CLOSE_CLAW:
                closeClaw();
                break;
            default:
                break;
        }
    }

    private void turnRightSlowly()
    {
        try
        {
            Log.d("ControlUnit", "Turning right");
            mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, MOTOR_SPEED_SLOW, SYNC_MODE_TURNING);
            mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED_SLOW), SYNC_MODE_TURNING);

            Thread.sleep(SLEEP_TIME_TURNING);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
            Thread.sleep(200);

            mPosTracker.addMovement(PositionTracker.MOVEMENT_DIRECTION.RIGHT, MOTOR_SPEED_SLOW, SLEEP_TIME_TURNING);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        prevAction = RobotAction.TURN_RIGHT_SLOWLY;
    }


    private void turnLeftSlowly()
    {
        try
        {
            Log.d("ControlUnit", "Turning left");
            mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED_SLOW), SYNC_MODE_TURNING);
            mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, MOTOR_SPEED_SLOW, SYNC_MODE_TURNING);

            Thread.sleep(SLEEP_TIME_TURNING);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
            Thread.sleep(200);

            mPosTracker.addMovement(PositionTracker.MOVEMENT_DIRECTION.LEFT, MOTOR_SPEED_SLOW, SLEEP_TIME_TURNING);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        prevAction = RobotAction.TURN_LEFT_SLOWLY;

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
}
