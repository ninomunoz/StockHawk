package com.udacity.stockhawk.util;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by i57198 on 4/25/17.
 */

public class DateAxisValueFormatter implements IAxisValueFormatter {

    private String[] mDates;

    public DateAxisValueFormatter(String[] dates) {
        this.mDates = dates;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Long millis = Long.parseLong(mDates[(int)value]);
        Date date = new Date(millis);
        DateFormat format = new SimpleDateFormat("MM/yyyy");
        return format.format(date);
    }

}
