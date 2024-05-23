/*
 * Copyright (C) 2016 huanghaibin_dev <huanghaibin_dev@163.com>
 * WebSite https://github.com/MiracleTimes-Dev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.haibin.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 默认高仿魅族日历布局
 * Created by huanghaibin on 2017/11/15.
 */

public final class DefaultMonthView extends MonthView {

    private Paint mTextPaint = new Paint();

    private Paint mSchemeBasicPaint = new Paint();

    private float mRadio;

    private int mPadding;

    private float mSchemeBaseLine;

    public DefaultMonthView(Context context) {
        super(context);

        mTextPaint.setTextSize(CalendarUtil.dipToPx(context, 8));
        mTextPaint.setColor(0xffFFFFFF);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);

        mSchemeBasicPaint.setAntiAlias(true);
        mSchemeBasicPaint.setStyle(Paint.Style.FILL);
        mSchemeBasicPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeBasicPaint.setColor(0xffed5353);
        mSchemeBasicPaint.setFakeBoldText(true);
        mRadio = CalendarUtil.dipToPx(getContext(), 7);
        mPadding = CalendarUtil.dipToPx(getContext(), 2);
        Paint.FontMetrics metrics = mSchemeBasicPaint.getFontMetrics();
        mSchemeBaseLine =
            mRadio - metrics.descent + (metrics.bottom - metrics.top) / 2 + CalendarUtil
            .dipToPx(getContext(), 1);

    }

    /**
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param y         日历Card y起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return true 则绘制onDrawScheme，因为这里背景色不是是互斥的
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y,
        boolean hasScheme) {
        mSelectedPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x + mPadding, y + mPadding, x + mItemWidth - mPadding,
            y + mItemHeight - mPadding, mSelectedPaint);
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        mSchemeBasicPaint.setColor(calendar.getSchemeColor());

        canvas.drawCircle(x + mItemWidth - mPadding - mRadio / 2, y + mPadding + mRadio, mRadio,
            mSchemeBasicPaint);

        canvas.drawText(calendar.getScheme(),
            x + mItemWidth - mPadding - mRadio / 2 - getTextWidth(calendar.getScheme()) / 2,
            y + mPadding + mSchemeBaseLine, mTextPaint);
    }

    @Override
    protected void onDrawEvent(Canvas canvas, Calendar calendar, int x, int y) {
        mEventPaint.setStyle(Paint.Style.FILL);
        float radius = (mItemWidth > mItemHeight) ? (mItemHeight / 3f) : mItemWidth / 4f;
        canvas.drawCircle(x + mItemWidth / 2f, y + mItemHeight / 2f, radius,
            mEventPaint);
    }

    @Override
    protected void onDrawLeaveDay(Canvas canvas, Calendar calendar, int x, int y) {
        mEventPaint.setStyle(Paint.Style.STROKE);
        float radius = (mItemWidth > mItemHeight) ? (mItemHeight / 3f) : mItemWidth / 4f;
        canvas.drawCircle(x + mItemWidth / 2f, y + mItemHeight / 2f, radius,
            mEventPaint);
    }

    @Override
    protected void onDrawHighlight(Canvas canvas, Calendar calendar, int x, int y) {
        mHighlightPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x, y + mPadding, x + mItemWidth,
            y + mItemHeight - mPadding, mHighlightPaint);
    }

    /**
     * 获取字体的宽
     *
     * @param text text
     * @return return
     */
    private float getTextWidth(String text) {
        return mTextPaint.measureText(text);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme,
        boolean isSelected, boolean isEvent, boolean isLeaveDate  ) {
        int cx = x + mItemWidth / 2;
        int top = y - mItemHeight / 6;

        if (isSelected && !isLeaveDate) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + y,
                mSelectTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + y,
                calendar.isCurrentDay() ? mCurDayTextPaint :
                    calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);
        } else if (isEvent){
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + y,
                calendar.isCurrentDay() ? mCurDayTextPaint : mEventTextPaint);
        } else if (isLeaveDate) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + y,
                    mHolidayTextPaint);
        } else  {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + y,
                calendar.isCurrentDay() ? mCurDayTextPaint :
                    calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
        }
    }
}
