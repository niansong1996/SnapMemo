package com.alan.sphare.presentation.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.alan.sphare.model.Tool.Time;
import com.alan.sphare.model.VO.TimeVO;

/**
 * Created by Alan on 2016/1/19.
 */
public class TimeBar extends View {

    TimeVO[] timePeriod;

    public TimeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void repaint(TimeVO[] timePeriod) {
        this.timePeriod = timePeriod;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(Color.DKGRAY);
        p.setStrokeWidth(2);
        canvas.drawRect(2, 0, getWidth() - 2, 20, p);

        //用白色来标识有空余的时间段
        p.setColor(Color.WHITE);
        if (timePeriod != null) {
            Time startTime, endTime;
            float start, end;
            for (int i = 0; i < timePeriod.length; i++) {
                startTime = timePeriod[i].getStartTime();
                endTime = timePeriod[i].getEndTime();
                start = (float) (getWidth() * (startTime.hour - 8 + startTime.minute / 60.0) / 16.0);
                end = (float) (getWidth() * (endTime.hour - 8 + endTime.minute / 60.0) / 16.0);
                canvas.drawRect(start + 2, 0, end + 2, 20, p);
            }
        }
    }
}
