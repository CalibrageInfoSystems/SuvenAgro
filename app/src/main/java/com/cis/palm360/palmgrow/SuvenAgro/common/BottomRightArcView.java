package com.cis.palm360.palmgrow.SuvenAgro.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class BottomRightArcView extends View {
    private Paint arcPaint;
    private Paint borderPaint;

    public BottomRightArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setColor(0x1AFF6600); // light orange with transparency
        arcPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(0xFFFF6600); // solid orange
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4f); // adjust border width
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();

        float radius = Math.min(width, height);

        RectF arcRect = new RectF(width - radius, height - radius, width, height);

        canvas.drawArc(arcRect, 0, 90, true, arcPaint);     // Quarter arc
        canvas.drawArc(arcRect, 0, 90, false, borderPaint); // Arc border
    }
}
