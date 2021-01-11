package com.pnbcode.webrosterimport;

import java.time.LocalDate;
import java.util.Date;

public class Shift {

    private final String date;
    private final String weekday;
    private final String startTime;
    private final String endTime;
    private final String function;
    private final String duration;

    public Shift(String date, String weekday, String startTime, String endTime, String function, String duration) {
        this.date = date;
        this.weekday = weekday;
        this.startTime = startTime;
        this.endTime = endTime;
        this.function = function;
        this.duration = duration + " hrs";
    }

    @Override
    public String toString() {
        return date + " || " + weekday + " || " + startTime + " - " + endTime + " || " + function + " || " + duration;
    }

    public String getDate() {
        return date;
    }

    public String getWeekday() {
        return weekday;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getFunction() {
        return function;
    }

    public String getDuration() {
        return duration;
    }
}
