package com.cis.palm360.palmgrow.SuvenAgro.common;

//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//
//
//public class SignatureView extends View {
//
//    private Canvas canvasBmp;
//    private boolean ignoreTouch;
//    private Point previousPoint, startPoint, currentPoint;
//    public static final float MIN_PEN_SIZE = 1f;
//    private static final float MIN_INCREMENT = 0.01f;
//    private static final float INCREMENT_CONSTANT = 0.0005f;
//    private static final float DRAWING_CONSTANT = 0.0085f;
//    public static final float MAX_VELOCITY_BOUND = 15f;
//    private static final float MIN_VELOCITY_BOUND = 1.6f;
//    private static final float STROKE_DES_VELOCITY = 1.0f;
//    private static final float VELOCITY_FILTER_WEIGHT = 0.2f;
//    private float lastVelocity, lastWidth;
//    private Paint paint, paintBm;
//    private Bitmap bmp;
//    private int layoutLeft, layoutTop, layoutRight, layoutBottom;
//    private Rect drawViewRect;
//    private int penColor, backgroundColor;
//    private boolean enableSignature;
//    private float penSize;
//    private Context context;
//
//    @SuppressWarnings("deprecation")
//    public SignatureView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        this.context = context;
//        this.setWillNotDraw(false);
//        this.setDrawingCacheEnabled(true);
//
//        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
//                attrs, com.kyanogen.signatureview.R.styleable.signature, 0, 0);
//
//        try {
//            backgroundColor = typedArray.getColor(com.kyanogen.signatureview.R.styleable.signature_backgroundColor,
//                    context.getResources().getColor(com.kyanogen.signatureview.R.color.white));
//            penColor = typedArray.getColor(com.kyanogen.signatureview.R.styleable.signature_penColor,
//                    context.getResources().getColor(com.kyanogen.signatureview.R.color.penRoyalBlue));
//            penSize = typedArray.getDimension(com.kyanogen.signatureview.R.styleable.signature_penSize,
//                    context.getResources().getDimension(com.kyanogen.signatureview.R.dimen.pen_size));
//            enableSignature = typedArray.getBoolean(com.kyanogen.signatureview.R.styleable.signature_enableSignature, true);
//        } finally {
//            typedArray.recycle();
//        }
//
//        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setColor(penColor);
//        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.FILL_AND_STROKE);
//        paint.setStrokeJoin(Paint.Join.ROUND);
//        paint.setStrokeCap(Paint.Cap.ROUND);
//        paint.setStrokeWidth(penSize);
//
//        paintBm = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paintBm.setAntiAlias(true);
//        paintBm.setStyle(Paint.Style.STROKE);
//        paintBm.setStrokeJoin(Paint.Join.ROUND);
//        paintBm.setStrokeCap(Paint.Cap.ROUND);
//        paintBm.setColor(Color.BLACK);
//    }
//
//
//    /**
//     * Set background color
//     *
//     * @param backgroundColor int
//     */
//    @Override
//    public void setBackgroundColor(int backgroundColor) {
//        this.backgroundColor = backgroundColor;
//    }
//
//    /**
//     * Clear signature from canvas
//     */
//    public void clearCanvas() {
//        previousPoint = null;
//        startPoint = null;
//        currentPoint = null;
//        lastVelocity = 0;
//        lastWidth = 0;
//
//        newBitmapCanvas(layoutLeft, layoutTop, layoutRight, layoutBottom);
//        postInvalidate();
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        layoutLeft = left;
//        layoutTop = top;
//        layoutRight = right;
//        layoutBottom = bottom;
//        if (bmp == null) {
//            newBitmapCanvas(layoutLeft, layoutTop, layoutRight, layoutBottom);
//        }
//    }
//
//    private void newBitmapCanvas(int left, int top, int right, int bottom) {
//        bmp = null;
//        canvasBmp = null;
//        if ((right - left) > 0 && (bottom - top) > 0) {
//            bmp = Bitmap.createBitmap(right - left, bottom - top, Bitmap.Config.ARGB_8888);
//            canvasBmp = new Canvas(bmp);
//            canvasBmp.drawColor(backgroundColor);
//        }
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        canvas.drawBitmap(bmp, 0, 0, paintBm);
//    }
//
//    private void drawLine(final float lastWidth, final float currentWidth,
//                          final float velocity) {
//        final Point mid1 = midPoint(previousPoint, startPoint);
//        final Point mid2 = midPoint(currentPoint, previousPoint);
//
//        draw(mid1, previousPoint, mid2, lastWidth,
//                currentWidth, velocity);
//    }
//
//    private float getPt(float n1, float n2, float perc) {
//        float diff = n2 - n1;
//        return n1 + (diff * perc);
//    }
//
//    private void draw(Point p0, Point p1, Point p2, float lastWidth,
//                      float currentWidth, float velocity) {
//        if (canvasBmp != null) {
//            float xa, xb, ya, yb, x, y;
//            float increment;
//            if (velocity > MIN_VELOCITY_BOUND && velocity < MAX_VELOCITY_BOUND) {
//                increment = DRAWING_CONSTANT - (velocity * INCREMENT_CONSTANT);
//            } else {
//                increment = MIN_INCREMENT;
//            }
//
//            for (float i = 0f; i < 1f; i += increment) {
//                xa = getPt(p0.x, p1.x, i);
//                ya = getPt(p0.y, p1.y, i);
//                xb = getPt(p1.x, p2.x, i);
//                yb = getPt(p1.y, p2.y, i);
//
//                x = getPt(xa, xb, i);
//                y = getPt(ya, yb, i);
//
//                float strokeVal = lastWidth + (currentWidth - lastWidth) * (i);
//                paint.setStrokeWidth(strokeVal < MIN_PEN_SIZE ? MIN_PEN_SIZE : strokeVal);
//                canvasBmp.drawPoint(x, y, paint);
//            }
//        }
//    }
//
//    private Point midPoint(Point p1, Point p2) {
//        return new Point((p1.x + p2.x) / 2.0f, (p1.y + p2.y) / 2, (p1.time + p2.time) / 2);
//    }
//
//    /**
//     * Get signature as bitmap
//     *
//     * @return Bitmap
//     */
//    public Bitmap getSignatureBitmap() {
//        if (bmp != null) {
//            return Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), true);
//        } else {
//            return null;
//        }
//    }
//
//
//    /**
//     * Render bitmap in signature
//     *
//     * @param bitmap Bitmap
//     */
//    public void setBitmap(Bitmap bitmap) {
//        if (bitmap != null) {
//            bmp = bitmap;
//            canvasBmp = new Canvas(bitmap);
//            postInvalidate();
//        }
//    }
//    public boolean isBitmapEmpty() {
//        if (bmp != null) {
//            Bitmap emptyBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
//                    bmp.getConfig());
//            Canvas canvasBmp = new Canvas(emptyBitmap);
//            canvasBmp.drawColor(backgroundColor);
//            if (bmp.sameAs(emptyBitmap)) {
//                return true;
//            }
//        }
//        return false;
//    }
//}

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.github.gcacace.signaturepad.views.SignaturePad;

public class SignatureView extends SignaturePad {

    private int backgroundColor = Color.WHITE;

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SignatureView(Context context) {
        super(context, null);
        init();
    }

    private void init() {
        setPenColor(Color.BLACK);
        setMinWidth(3f);
        setMaxWidth(5f);
        setBackgroundColor(backgroundColor);
    }

    /**
     * Set background color
     *
     * @param color int
     */
    @Override
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        super.setBackgroundColor(color);
    }

    /**
     * Clear signature from canvas
     */
    public void clearCanvas() {
        clear();
    }

    /**
     * Get signature as bitmap
     *
     * @return Bitmap
     */
    public Bitmap getSignatureBitmap() {
        return super.getSignatureBitmap();
    }

    /**
     * Check if canvas is empty
     *
     * @return boolean
     */
    public boolean isBitmapEmpty() {
        return isEmpty();
    }

    /**
     * Render bitmap in signature (not directly supported, workaround shown)
     *
     * @param bitmap Bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        // NOTE: SignaturePad does not support setting a bitmap directly.
        // This method is a workaround. You can draw the bitmap as background.
        Canvas canvas = new Canvas(getSignatureBitmap());
        canvas.drawBitmap(bitmap, 0, 0, new Paint());
        invalidate();
    }
}
