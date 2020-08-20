package com.example.lime.timeformats;

import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.TimeFormat;

public class CustomMinuteTimeFormat implements TimeFormat {
    @Override
    public String format(Duration duration) {
        return Math.abs(duration.getQuantity()) + "m";
    }

    @Override
    public String formatUnrounded(Duration duration) {
        return format(duration);
    }

    @Override
    public String decorate(Duration duration, String time) {
        return time;
    }

    @Override
    public String decorateUnrounded(Duration duration, String time) {
        return time;
    }
}
