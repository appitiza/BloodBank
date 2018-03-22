package net.appitiza.android.lifedrop.utils;

import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.app.AppLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class DateTimeUtils {

    private static final String TAG = "DateTimeUtilsClass";

    public static String dateDifferenceCalculation(String oldDatestring, Context mContext) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date oldDate = dateFormat.parse(oldDatestring);
            Date currentDate = new Date();


            if (oldDate.before(currentDate)) {
                /*long diff = currentDate.getTime() - oldDate.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                long weeks = days / 7;
                long months = days / 31;
                long years = months / 12;
                if (years > 0)
                    return Long.toString(years) + " " + (years == 1 ? mContext.getString(R.string.year_ago) : mContext.getString(R.string.years_ago));
                else if (months > 0)
                    return Long.toString(months) + " " + (months == 1 ? mContext.getString(R.string.month_ago) : mContext.getString(R.string.months_ago));
                else if (weeks > 0)
                    return Long.toString(weeks) + " " + (weeks == 1 ? mContext.getString(R.string.week_ago) : mContext.getString(R.string.weeks_ago));
                else if (days > 0)
                    return Long.toString(days) + " " + (days == 1 ? mContext.getString(R.string.day_ago) : mContext.getString(R.string.days_ago));
                else if (hours > 0)
                    return Long.toString(hours) + " " + (hours == 1 ? mContext.getString(R.string.hr_ago) : mContext.getString(R.string.hrs_ago));
                else if (minutes > 0)
                    return Long.toString(minutes) + " " + (minutes == 1 ? mContext.getString(R.string.min_ago) : mContext.getString(R.string.mins_ago));
                else
                    return Long.toString(seconds) + " " + (seconds == 1 ? mContext.getString(R.string.sec_ago) : mContext.getString(R.string.secs_ago));*/
                return "";
            } else if (oldDate.after(currentDate)) {
                long diff = oldDate.getTime() - currentDate.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                long weeks = days / 7;
                long months = days / 31;
                long years = months / 12;
                if (years > 0)
                    return Long.toString(years) + " " + (years == 1 ? mContext.getString(R.string.year) : mContext.getString(R.string.years));
                else if (months > 0)
                    return Long.toString(months) + " " + (months == 1 ? mContext.getString(R.string.month) : mContext.getString(R.string.months));
                else if (weeks > 0)
                    return Long.toString(weeks) + " " + (weeks == 1 ? mContext.getString(R.string.week) : mContext.getString(R.string.weeks));
                else if (days > 0)
                    return Long.toString(days) + " " + (days == 1 ? mContext.getString(R.string.day) : mContext.getString(R.string.days));
                else if (hours > 0)
                    return Long.toString(hours) + " " + (hours == 1 ? mContext.getString(R.string.hr) : mContext.getString(R.string.hrs));
                else if (minutes > 0)
                    return Long.toString(minutes) + " " + (minutes == 1 ? mContext.getString(R.string.min) : mContext.getString(R.string.mins));
                else
                    return Long.toString(seconds) + " " + (seconds == 1 ? mContext.getString(R.string.sec) : mContext.getString(R.string.secs));
            } else {

                return "";
            }
        } catch (Exception e) {
            AppLog.logErrorString(TAG, e.getMessage());
        }
        return "";
    }
    public static String ageCalculation(String oldDatestring, Context mContext) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date oldDate = dateFormat.parse(oldDatestring);
            Date currentDate = new Date();


            if (oldDate.before(currentDate)) {
                long diff = currentDate.getTime() - oldDate.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                long weeks = days / 7;
                long months = days / 31;
                long years = months / 12;
                if (years > 0)
                    return Long.toString(years) + " " + (years == 1 ? mContext.getString(R.string.year) : mContext.getString(R.string.years));
                else if (months > 0)
                    return Long.toString(months) + " " + (months == 1 ? mContext.getString(R.string.month) : mContext.getString(R.string.months));
                else if (weeks > 0)
                    return Long.toString(weeks) + " " + (weeks == 1 ? mContext.getString(R.string.week) : mContext.getString(R.string.weeks));
                else if (days > 0)
                    return Long.toString(days) + " " + (days == 1 ? mContext.getString(R.string.day) : mContext.getString(R.string.days));
                else if (hours > 0)
                    return Long.toString(hours) + " " + (hours == 1 ? mContext.getString(R.string.hr) : mContext.getString(R.string.hrs));
                else if (minutes > 0)
                    return Long.toString(minutes) + " " + (minutes == 1 ? mContext.getString(R.string.min) : mContext.getString(R.string.mins));
                else
                    return Long.toString(seconds) + " " + (seconds == 1 ? mContext.getString(R.string.sec) : mContext.getString(R.string.secs));
            } else if (oldDate.after(currentDate)) {
                long diff = oldDate.getTime() - currentDate.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                long weeks = days / 7;
                long months = days / 31;
                long years = months / 12;
                if (years > 0)
                    return Long.toString(years) + " " + (years == 1 ? mContext.getString(R.string.year) : mContext.getString(R.string.years));
                else if (months > 0)
                    return Long.toString(months) + " " + (months == 1 ? mContext.getString(R.string.month) : mContext.getString(R.string.months));
                else if (weeks > 0)
                    return Long.toString(weeks) + " " + (weeks == 1 ? mContext.getString(R.string.week) : mContext.getString(R.string.weeks));
                else if (days > 0)
                    return Long.toString(days) + " " + (days == 1 ? mContext.getString(R.string.day) : mContext.getString(R.string.days));
                else if (hours > 0)
                    return Long.toString(hours) + " " + (hours == 1 ? mContext.getString(R.string.hr) : mContext.getString(R.string.hrs));
                else if (minutes > 0)
                    return Long.toString(minutes) + " " + (minutes == 1 ? mContext.getString(R.string.min) : mContext.getString(R.string.mins));
                else
                    return Long.toString(seconds) + " " + (seconds == 1 ? mContext.getString(R.string.sec) : mContext.getString(R.string.secs));
            } else {

                return "";
            }
        } catch (Exception e) {
            AppLog.logErrorString(TAG, e.getMessage());
        }
        return "";
    }

    public static String formatDate(String format1Date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        try {
            Date oldDate = dateFormat.parse(format1Date);
            String newDate = newFormat.format(oldDate);
            return newDate;
        } catch (Exception e) {
            AppLog.logErrorString(TAG, e.getMessage());
        }
        return "";
    }

    public static String convertDate(String originalDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date tempDate = null;
        try {
            tempDate = sdf.parse(originalDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        String newFormat = formatter.format(tempDate);


        return newFormat;
    }

    public static String convertFormat(String date, String inFormat, String outFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
        SimpleDateFormat output = new SimpleDateFormat(outFormat);
        Date d = null;
        try {
            d = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedTime = output.format(d);
        return formattedTime;
    }
    public static boolean isDateAfter(String Datestring) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date selectedDate = dateFormat.parse(Datestring);
            Date currentDate = new Date();

            return !selectedDate.before(currentDate);
        } catch (Exception e) {
            AppLog.logErrorString(e.getMessage());
            return false;
        }

    }
    public static boolean isDateBefore(String Datestring) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date selectedDate = dateFormat.parse(Datestring);
            Date currentDate = new Date();

            return selectedDate.before(currentDate);
        } catch (Exception e) {
            AppLog.logErrorString(e.getMessage());
            return false;
        }

    }
    public static boolean isDateAfterorEqual(String Datestring,Context mContext) {
        try {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date selectedDate = dateFormat.parse(Datestring);

            Date currentDate = new Date();

            if (selectedDate.before(currentDate)) {
                return false;
            }
            else if (selectedDate.after(currentDate)) {
                return true;
            }
            else {
                return true;
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            AppLog.logErrorString(e.getMessage());
            Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_LONG).show();
            return false;
        }

    }

    public static boolean isDateBeforeorEqual(String Datestring) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date selectedDate = dateFormat.parse(Datestring);
            Date currentDate = new Date();

            if (selectedDate.before(currentDate)) {
                return true;
            }
            else return !selectedDate.after(currentDate);
        } catch (Exception e) {
            AppLog.logErrorString(e.getMessage());
            return false;
        }

    }
}
