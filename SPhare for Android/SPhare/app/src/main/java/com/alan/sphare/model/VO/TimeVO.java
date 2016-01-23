package com.alan.sphare.model.VO;

import com.alan.sphare.model.Tool.Time;

/**
 * Created by Alan on 2016/1/19.
 */
public class TimeVO {

    Time startTime;
    Time endTime;

    public TimeVO(Time startTime, Time endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }


}
