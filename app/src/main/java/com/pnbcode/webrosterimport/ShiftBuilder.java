package com.pnbcode.webrosterimport;

public class ShiftBuilder {

    /*
    Right now times and dates are stored as Strings, making ShiftBuilder a bit superfluous. Will be expanded and made more useful
     */

    public static Shift build(String date, String weekday, String startTime, String endTime, String function, String duration) {

        return new Shift(date, weekday, startTime, endTime, function, duration);

    }
}
