package com.cleenr.cleenr;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.cleenr.cleenr.R;




public class MainActivity extends Activity implements CvCameraViewListener2{
	private static final String TAG = "MainActivity";
	
	
	private CameraBridgeViewBase   mOpenCvCameraView;
	
	private CLEENRBrain mCleenrBrain;
	
	
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("CLEEN_R");
                    Log.i(TAG, "Native library loaded successfully");

                    mOpenCvCameraView.enableView();
                    mCleenrBrain = new CLEENRBrain(MainActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
		setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.main_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
	}
	public void onStart()
	{
		super.onStart();
		SeekBar objectSizeBar 	= (SeekBar) findViewById(R.id.objectSizeBar);
		SeekBar thresholdBar 	= (SeekBar) findViewById(R.id.saturationThreshholdBar);	
		objectSizeBar.setMax(100);
		thresholdBar.setMax(100);
		thresholdBar.setProgress(50);
		objectSizeBar.setProgress(2);

	}
    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId()  == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	

	@Override
	public void onCameraViewStarted(int width, int height) {	}
	@Override
	public void onCameraViewStopped() {	}
	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		return mCleenrBrain.onCameraFrame(inputFrame);
	}
	
	
    private static native void nativeDetect(long thiz, long inputImage, long faces);
    private static native void nativeOpening(int kernelSize, long inputImage);
    private static native void nativeRedFilter(long inputImage);
}
