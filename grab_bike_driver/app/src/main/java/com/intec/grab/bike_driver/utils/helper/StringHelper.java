package com.intec.grab.bike_driver.utils.helper;


import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class StringHelper {
    /**
     * Check if the given array contains the given value (with case-insensitive comparison).
     *
     * @param array The array
     * @param value The value to search
     * @return true if the array contains the value
     */
    public static boolean containsIgnoreCase(String[] array, String value) {
        for (String str : array) {
            if (value == null && str == null) return true;
            if (value != null && value.equalsIgnoreCase(str)) return true;
        }
        return false;
    }

    /**
     * Join an array of strings with the given separator.
     * <p>
     * Note: This might be replaced by utility method from commons-lang or guava someday
     * if one of those libraries is added as dependency.
     * </p>
     *
     * @param array     The array of strings
     * @param separator The separator
     * @return the resulting string
     */
    public static String join(String[] array, String separator) {
        int len = array.length;
        if (len == 0) return "";

        StringBuilder out = new StringBuilder();
        out.append(array[0]);
        for (int i = 1; i < len; i++) {
            out.append(separator).append(array[i]);
        }
        return out.toString();
    }

    public static boolean isNullOrEmpty(String value) {
        if (value == null) return true;
        if (value.isEmpty()) return true;
        return false;
    }

    public static Date convertToDate(String value)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date date = format.parse(value);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDateTime convertToDateTime(String value)
    {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime dateObj = LocalDateTime.parse(value, dateTimeFormatter);

        return dateObj;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String convertToString(String value, String format)
    {
        // value = 2021-06-23T04:29:42+00:00
        String v = value;
        if (v.length() == 25) {
            v = value.substring(0, value.length() - 6);
        }

        // Only parse: yyyy-MM-dd'T'HH:mm:ss
        LocalDateTime dateObj = LocalDateTime.parse(v);
        // Format
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);

        String result = dateObj.format(dateTimeFormatter);

        return result;
    }

    public static String formatDateTime(String fullDate) {
        try
        {
            // fullDate: 2022-01-19T13:47:25+07:00[Asia/Bangkok]
            String date = fullDate.substring(0, 10);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date fromDate = formatter.parse(date);
            Date current = formatter.parse(formatter.format(new Date()));

            SimpleDateFormat _format = new SimpleDateFormat("dd-MMM-yyyy");
            String _date = _format.format(fromDate);
            String _time = fullDate.substring(11, 16);
            return _date + " " + _time;

            /*
            if (current.compareTo(fromDate) == 0) {
                // if is today -> show Time: hh:mm
                String _t = fullDate.substring(11, 16);
                return _t;
            } else {
                // if is the past -> show Date: dd-MMM-yyyy
                SimpleDateFormat _f = new SimpleDateFormat("dd-MMM-yyyy");
                String _d = _f.format(fromDate);
                return _d;
            }
             */
        }
        catch (ParseException parseException) {
            parseException.printStackTrace();
        }
        return "";
    }

    public static String formatTime(String fullDate) {
        try
        {
            // fullDate: 2022-01-19T13:47:25.586247
            String _time = fullDate.substring(11, 16);
            return _time;
        }
        catch (Exception parseException) {
            parseException.printStackTrace();
        }
        return "UnKnown";
    }

    public static String formatNow(String format) {
        SimpleDateFormat formatter= new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public static String formatNumber(String number, String format) {
        DecimalFormat formatter = new DecimalFormat(format);
        double amount = Double.parseDouble(number);
        return formatter.format(amount);
    }

    public static String formatPhone(String number) {
        String result = number.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1)-$2-$3");
        return result;
    }

    public static <T> String stringify(T obj) {
        Gson gson = new Gson();
        String result = gson.toJson(obj);

        return result;
    }

    /*
    public static <T> T toJson(String str) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<T>(){}.getType();
        T result = gson.fromJson(str, collectionType);

        return result;
    }
    */

    public static String encodeBase64(String str) {
        byte[] encode = android.util.Base64.encode(str.getBytes(), Base64.NO_WRAP + Base64.NO_PADDING);

        return new String(encode);
    }

    public static String decodeBase64(String str) {
        byte[] decode = android.util.Base64.decode(str, Base64.NO_WRAP + Base64.NO_PADDING);

        return new String(decode);
    }
}
