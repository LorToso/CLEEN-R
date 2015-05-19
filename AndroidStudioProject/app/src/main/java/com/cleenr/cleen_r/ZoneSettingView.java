package com.cleenr.cleen_r;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.cleenr.cleen_r.objectCategorisation.Category;
import com.cleenr.cleen_r.objectCategorisation.Color;
import com.cleenr.cleen_r.objectCategorisation.Shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sebastian on 19.05.15.
 */
public class ZoneSettingView extends SurfaceView implements SurfaceHolder.Callback {

    private static final float SQUARE_SIZE = 60.0f;

    private Spinner colorSpinner = null;
    private RadioButton radioSphere = null;
    private RadioButton radioRect = null;

    private OnTouchListener touchListener = null;

    private Paint backPaint = null;
    private Paint rasterPaint = null;
    private Map<Color, Paint> categoryPaints = null;

    private Bitmap robotBmp = null;

    private SurfaceHolder holder = null;

    public ZoneSettingView(Context context) {
        super(context);
        init();
    }

    public ZoneSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoneSettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        backPaint = new Paint();
        backPaint.setColor(0xFFFFFFFF);

        rasterPaint = new Paint();
        rasterPaint.setColor(0x88000000);

        categoryPaints = new HashMap<>();

        categoryPaints.put(Color.RED, new Paint());
        categoryPaints.get(Color.RED).setColor(android.graphics.Color.RED);

        categoryPaints.put(Color.YELLOW, new Paint());
        categoryPaints.get(Color.YELLOW).setColor(android.graphics.Color.YELLOW);

        categoryPaints.put(Color.GREEN, new Paint());
        categoryPaints.get(Color.GREEN).setColor(android.graphics.Color.GREEN);

        categoryPaints.put(Color.BLUE, new Paint());
        categoryPaints.get(Color.BLUE).setColor(android.graphics.Color.BLUE);

        categoryPaints.put(Color.PURPLE, new Paint());
        categoryPaints.get(Color.PURPLE).setColor(0xFF990099);

        categoryPaints.put(Color.ORANGE, new Paint());
        categoryPaints.get(Color.ORANGE).setColor(0xFFFF8000);

        robotBmp = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);

        getHolder().addCallback(this);
    }

    private class ZoneSettingViewOnTouchListener implements OnTouchListener {
        public ZoneSettingViewOnTouchListener()
        {
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (colorSpinner == null || radioSphere == null || radioRect == null)
                return false;

            Shape s = radioSphere.isChecked() ? Shape.SPHERE : Shape.CUBE;

            Color c = null;
            switch ((String) colorSpinner.getSelectedItem())
            {
                case "rot":
                    c = Color.RED;
                    break;
                case "gelb":
                    c = Color.YELLOW;
                    break;
                case "grün":
                    c = Color.GREEN;
                    break;
                case "blau":
                    c = Color.BLUE;
                    break;
                case "lila":
                    c = Color.PURPLE;
                    break;
                case "orange":
                    c = Color.ORANGE;
                    break;
                default:
                    return false;
            }
            PointF p = new PointF(
                    (event.getX() - getWidth() / 2.0f) / SQUARE_SIZE,
                    (event.getY() - getHeight() / 2.0f) / SQUARE_SIZE);

            Category category = new Category(s, c);

            Globals.searchCategories.put(category, p);

            refresh();
            return true;
        }
    }

    private void performDraw(SurfaceHolder holder)
    {
        if (holder == null)
            return;

        Canvas canvas = null;
        try
        {
            canvas = holder.lockCanvas(null);

            synchronized (holder)
            {
                update(canvas);
            }
        }
        finally
        {
            if (canvas != null)
            {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void refresh()
    {
        performDraw(holder);
    }

    public void setControls(Spinner colorSpinner, RadioButton radioSphere, RadioButton radioRect)
    {
        this.colorSpinner = colorSpinner;
        this.radioSphere = radioSphere;
        this.radioRect = radioRect;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
        touchListener = new ZoneSettingViewOnTouchListener();
        setOnTouchListener(touchListener);
        performDraw(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.holder = null;
        if (touchListener == null)
            return;
        setOnClickListener(null);
        setClickable(false);
        touchListener = null;
    }

    private void update(Canvas canvas)
    {
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        float xCenter = w / 2.0f;
        float yCenter = h / 2.0f;

        canvas.drawPaint(backPaint);

        canvas.save();
        canvas.translate(xCenter, yCenter);

        // draw the categories
        for (Category c : Globals.searchCategories.keySet())
        {
            PointF p = Globals.searchCategories.get(c);
            float squareStartX = p.x * SQUARE_SIZE - SQUARE_SIZE / 2.0f;
            float squareStartY = p.y * SQUARE_SIZE - SQUARE_SIZE / 2.0f;
            RectF square = new RectF(
                    squareStartX, squareStartY,
                    squareStartX + SQUARE_SIZE, squareStartY + SQUARE_SIZE);

            Paint paint = categoryPaints.get(c.getColor());
            if (c.getShape() == Shape.CUBE)
                canvas.drawRect(square, paint);
            else
                canvas.drawArc(square, 0.0f, 360.0f, true, paint);
        }

        canvas.restore();

        // draw the raster, with the robot in the center of a square
        final int rasterVerLines = (int) (w / SQUARE_SIZE + 1.0f) | 1;
        final int rasterHorLines = (int) (h / SQUARE_SIZE + 1.0f) | 1;
        final float rasterVerStart = (w - rasterVerLines * SQUARE_SIZE) / 2.0f;
        final float rasterHorStart = (h - rasterHorLines * SQUARE_SIZE) / 2.0f;
        float xPos, yPos;
        for (int x = 0; x < rasterVerLines; x++)
        {
            xPos = x * SQUARE_SIZE + rasterVerStart;
            canvas.drawLine(xPos, 0.0f, xPos, h, rasterPaint);
        }
        for (int y = 0; y < rasterHorLines; y++)
        {
            yPos = y * SQUARE_SIZE + rasterHorStart;
            canvas.drawLine(0.0f, yPos, w, yPos, rasterPaint);
        }

        canvas.translate(xCenter, yCenter);

        canvas.drawBitmap(
                robotBmp,
                robotBmp.getWidth() / -2.0f,
                robotBmp.getHeight() / -2.0f,
                null
        );
    }
}
