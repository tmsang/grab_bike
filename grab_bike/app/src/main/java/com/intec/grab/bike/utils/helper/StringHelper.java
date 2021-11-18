package com.intec.grab.bike.utils.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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

    public static LocalDateTime convertToDateTime(String value)
    {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime dateObj = LocalDateTime.parse(value, dateTimeFormatter);

        return dateObj;
    }

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
}
