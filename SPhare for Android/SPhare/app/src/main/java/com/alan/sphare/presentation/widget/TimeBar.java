package com.alan.sphare.presentation.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.alan.sphare.R;
import com.alan.sphare.model.tool.Time;
import com.alan.sphare.model.VO.TimeVO;

/**
 * Created by Alan on 2016/1/19.
 */
public class TimeBar extends View {

    final int dialog_up_shift = 38, dialog_down_shift = 110,
            dialog_up_text_shift = dialog_up_shift - 18, dialog_down_text_shift = dialog_down_shift - 24;

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
        canvas.drawRect(2, 100, getWidth() - 2, 120, p);

        //用白色来标识有空余的时间段，并且在时间轴上显示具体空余时间标识
        p.setColor(Color.WHITE);
        if (timePeriod != null) {
            Time startTime, endTime;
            float start, end;
            for (int i = 0; i < timePeriod.length; i++) {
                startTime = timePeriod[i].getStartTime();
                endTime = timePeriod[i].getEndTime();
                start = (float) (getWidth() * (startTime.hour - 8 + startTime.minute / 60.0) / 16.0 + 2);
                end = (float) (getWidth() * (endTime.hour - 8 + endTime.minute / 60.0) / 16.0 + 2);
                canvas.drawRect(start, 100, end, 120, p);

                //文字画笔类
                Paint textP = new TextPaint();
                textP.setTextSize(40);
                textP.setFakeBoldText(true);
                textP.setColor(getResources().getColor(R.color.white));
                //读取上对话框图片
                Bitmap dialogup = BitmapFactory.decodeResource(getResources(), R.drawable.dialog_up);
                //绘制上对话框
                if (start - 190 < 0) {//如果超出左边界就从边界开始画
                    canvas.drawBitmap(dialogup, 0, 0, p);
                    //绘制上对话框里的时间显示
                    canvas.drawText(Time.amplify(startTime.hour) + ":" + Time.amplify(startTime.minute), 24, 50, textP);

                } else {
                    canvas.drawBitmap(dialogup, start - dialog_up_shift, 0, p);
                    //绘制上对话框里的时间显示
                    canvas.drawText(Time.amplify(startTime.hour) + ":" + Time.amplify(startTime.minute), start - dialog_up_text_shift, 50, textP);

                }

                //读取下对话框图片
                Bitmap dialogdown = BitmapFactory.decodeResource(getResources(), R.drawable.dialog_down);
                if (end - 30 + dialogdown.getWidth() > getWidth()) {//超出右边框处理
                    //绘制下对话框
                    canvas.drawBitmap(dialogdown, getWidth() - dialogdown.getWidth(), 125, p);
                    //绘制上对话框里的时间显示
                    canvas.drawText(Time.amplify(endTime.hour) + ":" + Time.amplify(endTime.minute), getWidth() - dialogdown.getWidth() + 28, 200, textP);

                } else {
                    //绘制下对话框
                    canvas.drawBitmap(dialogdown, end - dialog_down_shift, 125, p);
                    //绘制上对话框里的时间显示
                    canvas.drawText(Time.amplify(endTime.hour) + ":" + Time.amplify(endTime.minute), end - dialog_down_text_shift, 200, textP);

                }
            }
        }
    }
}
