package com.cleenr.cleen_r;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.cleenr.cleen_r.nxt.ChooseDeviceActivity;
import com.cleenr.cleen_r.nxt.NXTTalker;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;


public class MainActivity extends Activity implements CvCameraViewListener
{
    private static final String TAG = "MainActivity";


    private CameraBridgeViewBase mOpenCvCameraView;

    private CLEENRBrain      mCleenrBrain;
    private BluetoothAdapter mBluetoothAdapter;
    private       String    mDeviceAddress = null;
    private final NXTTalker mNXTTalker     = new NXTTalker();

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this)
    {
        @Override
        public void onManagerConnected(int status)
        {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load native library after(!) OpenCV initialization
                    //System.loadLibrary("CLEEN_R");
                    //Log.i(TAG, "Native library loaded successfully");

                    mOpenCvCameraView.enableView();
                    mCleenrBrain = new CLEENRBrain();
                }
                break;
                default:
                {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.main_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_settings)
        {
            findBrick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findBrick()
    {
        if (!checkForBluetooth())
            return;

        Intent intent = new Intent(this, ChooseDeviceActivity.class);
        startActivityForResult(intent, 0);
    }

    private boolean checkForBluetooth()
    {
        if (mBluetoothAdapter != null)
            return true;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null)
            return true;

        Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onCameraViewStarted(int width, int height)
    {
    }

    @Override
    public void onCameraViewStopped()
    {
    }

    /*@Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return mCleenrBrain.onCameraFrame(inputFrame);
    }*/
    @Override
    public Mat onCameraFrame(Mat inputFrame)
    {
        return mCleenrBrain.onCameraFrame(inputFrame);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            String address = data.getExtras().getString(ChooseDeviceActivity.EXTRA_DEVICE_ADDRESS);
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            //Toast.makeText(this, address, Toast.LENGTH_LONG).show();
            mDeviceAddress = address;
            mNXTTalker.connect(device);
        }
    }
}
