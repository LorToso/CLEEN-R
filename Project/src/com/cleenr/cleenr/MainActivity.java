package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

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
	
	private ObjectDetector mObjectDetector;
	private int nImageWidth = 0;
	private int nImageHeight = 0;
	
	
	
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
                    mObjectDetector = new ObjectDetector();
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
		Mat outputFrame = inputFrame.rgba();
		nImageWidth = outputFrame.cols();
		nImageHeight = outputFrame.rows();
		

		SeekBar objectSizeBar 	= (SeekBar) findViewById(R.id.objectSizeBar);
		SeekBar thresholdBar 	= (SeekBar) findViewById(R.id.saturationThreshholdBar);	

		int saturationThreshold = (int) (((double)thresholdBar.getProgress()/100) * 255);
		int minimumObjectSize 	= (int) (((double)objectSizeBar.getProgress()/100) * nImageHeight*nImageWidth/2);
		
		mObjectDetector.setSaturationThreshold(saturationThreshold);
		mObjectDetector.setMinimumObjectSize(minimumObjectSize);
		
		ArrayList<Rect> allBoundingRects = mObjectDetector.detectObjects(inputFrame);
		
		int i = 0;
		
		for(Rect rect : allBoundingRects)
		{
			Point middle = new Point(rect.x + rect.width/2, rect.y + rect.height/2);
			i++;
			Log.d("OBJECT FOUND", "OBJECT " + i +  " AT (" + middle.x + "|" + middle.y + ")");
			Core.rectangle(outputFrame, rect.tl(), rect.br(), new Scalar(255,255,0), 5);
			Core.rectangle(outputFrame, new Point(middle.x-2, middle.y-2), new Point(middle.x+2, middle.y+2), new Scalar(255,255,0), 5);
		}
		return outputFrame;
	}

    private static native void nativeDetect(long thiz, long inputImage, long faces);
    private static native void nativeOpening(int kernelSize, long inputImage);
    private static native void nativeRedFilter(long inputImage);
}
