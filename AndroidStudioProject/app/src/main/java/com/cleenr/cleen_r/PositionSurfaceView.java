package com.cleenr.cleen_r;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cleenr.cleen_r.robotcontrolunits.PositionTracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PositionSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    private PositionTracker mPosTracker = null;
    private Timer           mDrawTimer  = null;

    private PositionList mPositions = null;

    private Paint mBackPaint          = null;
    private Paint mStartingPointPaint = null;
    private Paint mWayPaint           = null;

    private Bitmap robotBmp = null;

    public PositionSurfaceView(Context context)
    {
        super(context);
        init();
    }

    public PositionSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PositionSurfaceView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        mDrawTimer = new Timer("draw timer");

        mPositions = new PositionList();

        mBackPaint = new Paint();
        mStartingPointPaint = new Paint();
        mWayPaint = new Paint();
        mBackPaint.setColor(0xFFFFFFFF);
        mStartingPointPaint.setColor(0xFFAA0000);
        mWayPaint.setColor(0xFF0000AA);
        mWayPaint.setStrokeWidth(1.02f);
        mWayPaint.setStrokeCap(Paint.Cap.ROUND);

        robotBmp = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);

        getHolder().addCallback(this);
    }

    public void setPositionTracker(PositionTracker posTracker)
    {
        mPosTracker = posTracker;
        mPositions.clear();
    }

    public void reset()
    {
        mPositions.clear();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // draw every 100 ms
        mDrawTimer.scheduleAtFixedRate(new DrawTimerTask(holder), 0, 100);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        mDrawTimer.cancel();
    }

    private class DrawTimerTask extends TimerTask
    {
        private SurfaceHolder mHolder = null;

        public DrawTimerTask(SurfaceHolder holder)
        {
            mHolder = holder;
        }

        @Override
        public void run()
        {
            refreshPositions();

            Canvas canvas = null;
            try
            {
                canvas = mHolder.lockCanvas(null);

                synchronized (mHolder)
                {
                    update(canvas);
                }
            }
            finally
            {
                if (canvas != null)
                {
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void refreshPositions()
    {
        if (mPosTracker != null)
        {
            float x = (float) mPosTracker.getX();
            float y = (float) mPosTracker.getY();

            int positionCount = mPositions.size();
            if (positionCount == 0 || !mPositions.get(positionCount - 1).equals(x, y))
            {
                // new position
                mPositions.add(new PointF(x, y));
            }
        }
    }

    private void update(Canvas canvas)
    {
        final float scale = 30.0f;

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        float xCenter = w / 2.0f;
        float yCenter = h / 2.0f;

        canvas.drawPaint(mBackPaint);
        canvas.translate(xCenter, yCenter);

        canvas.save();
        canvas.scale(scale, scale);

        float x = (float) mPosTracker.getX();
        float y = (float) mPosTracker.getY();

        for (PointF p : mPositions)
        {
            canvas.drawPoint(
                    p.x, p.y,
                    mWayPaint
            );
        }

        // draw robot
        canvas.restore();
        canvas.drawBitmap(
                robotBmp,
                robotBmp.getWidth() / -2.0f,
                robotBmp.getHeight() / -2.0f,
                null
        );
    }

    private class PositionList extends ArrayList<PointF>
    {
        private PositionList()
        {
        }

        public Iterator<PointF> iterator()
        {
            return new PositionIterator(this);
        }
    }

    private class PositionIterator implements Iterator<PointF>
    {
        private List<PointF> mList  = null;
        private int          mIndex = 0;

        private PositionIterator(List<PointF> list)
        {
            mList = list;
            mIndex = list.size() - 1;
        }

        @Override
        public boolean hasNext()
        {
            return mIndex >= 0;
        }

        @Override
        public PointF next()
        {
            return mList.get(mIndex--);
        }

        @Override
        public void remove()
        {

        }
    }
}
