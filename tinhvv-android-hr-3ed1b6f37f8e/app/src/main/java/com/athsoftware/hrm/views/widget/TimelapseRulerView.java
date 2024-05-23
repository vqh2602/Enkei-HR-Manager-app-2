package com.athsoftware.hrm.views.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.athsoftware.hrm.R;
import com.athsoftware.hrm.helper.extensions.Resource_ExtensionsKt;

import java.util.ArrayList;
import java.util.List;

public class TimelapseRulerView extends View {

    private Paint paint;
    private Paint paintProcess;

    public float getProcess() {
        return process;
    }

    public void setProcess(float process) {
        this.process = process;
        invalidate();
    }

    private float process = 0f;

    public List<String> getListMonth() {
        return listMonth;
    }

    public void setListMonth(List<String> listMonth) {
        this.listMonth = listMonth;
        invalidate();
    }

    private List<String> listMonth = new ArrayList<>();
    public TimelapseRulerView(Context context) {
        super(context);
        init();
    }

    public TimelapseRulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimelapseRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paint = new Paint();
        paint.setColor(Color.parseColor("#575757"));
        paint.setStrokeWidth(getResources().getDisplayMetrics().density * 1.2f);
        paint.setTextSize(getResources().getDisplayMetrics().scaledDensity * 12f);
        paintProcess = new Paint();
        paintProcess.setColor(Color.parseColor("#009b90"));
        paintProcess.setStrokeWidth(getResources().getDisplayMetrics().density * 2f);
        paintProcess.setTextSize(getResources().getDisplayMetrics().scaledDensity * 12f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int desiredHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(measureDimension(desiredWidth, widthMeasureSpec),
                measureDimension(desiredHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float padding = getResources().getDisplayMetrics().density * 3;
        int width = (int) (getWidth() - padding * 2);
        float height = getResources().getDisplayMetrics().density * 50f;
        int max = listMonth.size();
        float start = 10f;
        float xWidth = width / (float)max;
        float bottom = height * 2f / 3f;
        for (int i = 0; i < max; i++) {
            float x = i * xWidth + padding;
            paint.setColor(Color.parseColor("#575757"));
            canvas.drawLine(x, start, x, bottom, paint);
            drawTextCentered(listMonth.get(i).split("/")[0], x, height, paint, canvas);
//            if (i == max - 1) {
//                continue;
//            }
            for (int j = 1; j < 7; j++) {
                float dd = xWidth / 6f;
                paint.setColor(Color.parseColor("#717171"));
                canvas.drawLine(x + j * dd, bottom * 2f / 3f, x + j * dd, bottom, paint);
            }
            paint.setColor(Color.parseColor("#575757"));
            canvas.drawLine(x + xWidth / 2f, bottom / 2f, x + xWidth / 2f, bottom, paint);
        }
        if (process > 0f) {
            canvas.drawLine(process * max * xWidth + padding, 0, process * max * xWidth + padding, bottom, paintProcess);
        }
        super.onDraw(canvas);
    }

    private void drawTextCentered(String text, float x, float y, Paint paint, Canvas canvas) {
        float xPos = x - (paint.measureText(text)/2);
        float yPos = (y - ((paint.descent() + paint.ascent()) / 2)) ;
        canvas.drawText(text, xPos, yPos, paint);
    }

    private int measureDimension(int desiredSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = desiredSize;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        if (result < desiredSize){
            Log.e("ChartView", "The view is too small, the content might get cut");
        }
        return result;
    }
}
