package com.cleenr.cleen_r;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.cleenr.cleen_r.nxt.ChooseDeviceActivity;
import com.cleenr.cleen_r.nxt.NxtTalker;
import com.cleenr.cleen_r.robotcontrolunits.NxtControlUnit;
import com.cleenr.cleen_r.robotcontrolunits.PositionTracker;
import com.cleenr.cleen_r.robotcontrolunits.RobotAction;
import com.cleenr.cleen_r.robotcontrolunits.RobotControlUnit;

import java.util.Timer;
import java.util.TimerTask;


public class ManualControlActivity extends ActionBarActivity
{
    private static final String TAG = "ManualControlActivity";

    private static final int REQUEST_ENABLE_BT  = 1;
    private static final int REQUEST_FIND_BRICK = 2;

    private BluetoothAdapter mBluetoothAdapter = null;
    private String           mDeviceAddress    = null;
    private NxtTalker        mNXTTalker        = null;
    private RobotControlUnit mRobotControlUnit = null;
    private PositionTracker  mPosTracker       = null;
    private RobotAction      mRobotTask        = RobotAction.STOP;

    private Timer   mPositionDisplayTimer = null;
    private Thread  mTaskSenderThread     = null;
    private boolean mStopSendingTasks     = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_manual_control);

        mNXTTalker = new NxtTalker();
        mPosTracker = new PositionTracker();
        mRobotControlUnit = new NxtControlUnit(mNXTTalker, mPosTracker);

        setTaskButtonOnTouchListener();

        final PositionSurfaceView posSurfaceView = (PositionSurfaceView) findViewById(R.id.positionSurfaceView);
        posSurfaceView.setPositionTracker(mPosTracker);

        findViewById(R.id.button_reset).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mPosTracker.resetPosition();
                        posSurfaceView.reset();
                    }
                }
        );

        Log.d(TAG, "created activity");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mPositionDisplayTimer != null) {
            mPositionDisplayTimer.cancel();
            mPositionDisplayTimer = null;
        }
        if (mTaskSenderThread != null)
            stopTaskSenderThread();
        Log.d(TAG, "paused activity");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // refresh every 100 ms
        mPositionDisplayTimer = new Timer("refreshDisplay timer");
        mPositionDisplayTimer.scheduleAtFixedRate(new RefreshPositionDisplayTimerTask(), 0, 100);
        if (mTaskSenderThread != null)
            stopTaskSenderThread();
        mTaskSenderThread = new Thread(new NxtTaskSender(), "task sender thread");
        mTaskSenderThread.start();
        Log.d(TAG, "resumed activity");
    }

    private void stopTaskSenderThread()
    {
        Log.d(TAG, "stopping task sender thread...");
        mStopSendingTasks = true;
        try
        {
            mTaskSenderThread.join();
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
            Thread.interrupted();
        }
        mStopSendingTasks = false;
        Log.d(TAG, "stopped task sender thread");
    }

    private void setTaskButtonOnTouchListener()
    {
        View.OnTouchListener taskButtonOnTouchListener = new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        // button pressed
                        switch (v.getId())
                        {
                            case R.id.button_moveForward:
                                mRobotTask = RobotAction.DRIVE_FORWARD;
                                break;
                            case R.id.button_moveBackward:
                                mRobotTask = RobotAction.DRIVE_BACKWARD;
                                break;
                            case R.id.button_moveLeft:
                                mRobotTask = RobotAction.TURN_LEFT;
                                break;
                            case R.id.button_moveRight:
                                mRobotTask = RobotAction.TURN_RIGHT;
                                break;
                            case R.id.button_return_to_starting_point:
                                mRobotTask = RobotAction.RETURN_TO_STARTING_POINT;
                                break;
                            case R.id.button_openClaw:
                                mRobotTask = RobotAction.OPEN_CLAW;
                                break;
                            case R.id.button_closeClaw:
                                mRobotTask = RobotAction.CLOSE_CLAW;
                                break;
                            default:
                                return false;
                        }
                        v.setPressed(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        // button released
                        mRobotTask = RobotAction.STOP;
                        v.setPressed(false);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        };

        findViewById(R.id.button_moveForward).setOnTouchListener(taskButtonOnTouchListener);
        findViewById(R.id.button_moveForward).setOnTouchListener(taskButtonOnTouchListener);
        findViewById(R.id.button_moveBackward).setOnTouchListener(taskButtonOnTouchListener);
        findViewById(R.id.button_moveLeft).setOnTouchListener(taskButtonOnTouchListener);
        findViewById(R.id.button_moveRight).setOnTouchListener(taskButtonOnTouchListener);
        findViewById(R.id.button_return_to_starting_point).setOnTouchListener(taskButtonOnTouchListener);
        findViewById(R.id.button_openClaw).setOnTouchListener(taskButtonOnTouchListener);
        findViewById(R.id.button_closeClaw).setOnTouchListener(taskButtonOnTouchListener);
    }

    private class RefreshPositionDisplayTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            refreshPositionDisplay();
        }
    }

    private void refreshPositionDisplay()
    {
        runOnUiThread(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        TextView xView = (TextView) findViewById(R.id.textView_x);
                        TextView yView = (TextView) findViewById(R.id.textView_y);
                        TextView angleView = (TextView) findViewById(R.id.textView_angle);

                        xView.setText(String.format("%.3f", mPosTracker.getX()));
                        yView.setText(String.format("%.3f", mPosTracker.getY()));
                        angleView.setText(String.format("%.3f\u00b0", Math.toDegrees(mPosTracker.getAngle())));
                    }
                }
        );
    }

    private class NxtTaskSender implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                while (!mStopSendingTasks)
                {
                    if (mRobotTask == RobotAction.STOP)
                    {
                        if (mRobotControlUnit.isMoving())
                            mRobotControlUnit.stopMoving();
                        Thread.yield();
                        continue;
                    }
                    switch (mRobotTask)
                    {
                        case DRIVE_FORWARD:
                            mRobotControlUnit.driveForward();
                            break;
                        case DRIVE_BACKWARD:
                            mRobotControlUnit.driveBackward();
                            break;
                        case TURN_LEFT:
                            mRobotControlUnit.turnLeft();
                            break;
                        case TURN_RIGHT:
                            mRobotControlUnit.turnRight();
                            break;
                        case RETURN_TO_STARTING_POINT:
                            mRobotControlUnit.driveToPoint(new PointF(0.0f, 0.0f));
                            break;
                        case OPEN_CLAW:
                            mRobotControlUnit.openClaw();
                            break;
                        case CLOSE_CLAW:
                            mRobotControlUnit.closeClaw();
                            break;
                        default:
                            break;
                    }
                    Thread.sleep(100);
                }
            }
            catch (InterruptedException ex)
            { }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manual_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_connect:
                findBrick();
                return true;
            case R.id.action_disconnect:
                if (mNXTTalker != null)
                    mNXTTalker.stop();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findBrick()
    {
        if (!isBluetoothAvailable())
            return;

        if (enableBluetooth())
            startBrickFindingActivity();
    }

    private void startBrickFindingActivity()
    {
        Intent intent = new Intent(this, ChooseDeviceActivity.class);
        startActivityForResult(intent, REQUEST_FIND_BRICK);
    }

    private boolean isBluetoothAvailable()
    {
        if (mBluetoothAdapter != null)
            return true;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null)
            return true;

        Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        return false;
    }

    private boolean enableBluetooth()
    {
        if (mBluetoothAdapter.isEnabled())
            return true;

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_ENABLE_BT:
                if (resultCode != Activity.RESULT_OK)
                {
                    Toast.makeText(this, "Bluetooth could not be enabled", Toast.LENGTH_LONG).show();
                    break;
                }
                startBrickFindingActivity();
                break;
            case REQUEST_FIND_BRICK:
                if (resultCode != Activity.RESULT_OK)
                {
                    break;
                }
                String address = data.getExtras().getString(ChooseDeviceActivity.EXTRA_DEVICE_ADDRESS);
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                //Toast.makeText(this, address, Toast.LENGTH_LONG).show();
                mDeviceAddress = address;
                mNXTTalker.connect(device);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
