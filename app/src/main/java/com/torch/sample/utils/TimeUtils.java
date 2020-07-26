package com.torch.sample.utils;

import java.util.Locale;

public class TimeUtils {


    /**
     * format the duration to readable string
     *
     * @param duration duration in minutes.
     *                 <p>
     *                 NOTE:- duration should always be in minutes. otherwise unexpected results will occur.
     */
    public static String formatDuration(long duration) {

        long hour = duration / 60;
        long minutes = duration % 60;

        String hourString = hour > 0 ? (hour == 1 ? "Hour" : "Hours") : "";
        String minuteString = minutes > 0 ? (minutes == 1 ? "Minute" : "Minutes") : "";

        if (hour > 0) {
            if (minutes > 0)
                return String.format(new Locale("en", "IN"), "%d %s and %d %s", hour, hourString, minutes, minuteString);
            else
                return String.format(new Locale("en", "IN"), "%d %s", hour, hourString);
        }

        return String.format(new Locale("en", "IN"), "%d %s", minutes, minuteString);
    }
}
