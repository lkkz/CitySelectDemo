package com.cool.selectlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by cool on 2018/1/5.
 */

public class CheckView extends View {

    private int color = Color.parseColor("#FF4040");

    private Paint paint;
    private  int width;
    private int height;
    private int offset;
    private Path path;
    public CheckView(Context context) {
        this(context,null);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1.8f,getResources().getDisplayMetrics());
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,getResources().getDisplayMetrics());
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(color);
        path.moveTo(offset*2/3,height/2-offset/2);
        path.lineTo(width/3,height-2*offset);
        path.lineTo(width-offset,height/2-2*offset);
        canvas.drawPath(path,paint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int suggestWidth = measureSize(widthMode, widthSize);
        int suggestHeight = measureSize(heightMode,heightSize);
        setMeasuredDimension(suggestWidth,suggestHeight);
    }

    private int measureSize(int mode, int size) {
        int with;
        if(mode == MeasureSpec.EXACTLY){
            with = size;
        }else {
            int defaultWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,25,getResources().getDisplayMetrics());
            with = Math.min(size,defaultWidth);
        }
        return with;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }

    public void setColor(int color){
        this.color = color;
        invalidate();
    }
}
