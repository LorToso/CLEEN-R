package com.cleen_r.app;

/**
 * Created by lorenzo on 20.10.14.
 */

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;

/**
 * A basic Camera preview class
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, View.OnClickListener {
    private static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private boolean bCanTakeImage = true;


    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        setOnClickListener(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        if (v != this || !bCanTakeImage)
            return;

        bCanTakeImage = false;
        mCamera.takePicture(new MyShutterCallback(), new MyPictureCallback("raw"), new MyPictureCallback("postview"), new MyPictureCallback("jpeg"));
    }

    class MyShutterCallback implements Camera.ShutterCallback {

        @Override
        public void onShutter() {
            bCanTakeImage = true;
            Log.d(TAG, "ShutterCallback");
            mCamera.startPreview();

        }
    }

    class MyPictureCallback implements Camera.PictureCallback {
        String name;

        public MyPictureCallback(String name) {
            this.name = name;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            int datasize = 0;
            if (data != null)
                datasize = data.length;

            Log.d(TAG, name + ": Data-Size: " + datasize);
        }
    }
}
