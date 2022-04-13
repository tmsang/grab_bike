package com.intec.grab.bike_driver.utils.log;

import static com.intec.grab.bike_driver.configs.Constants.API_NET;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.android.volley.VolleyError;
import com.hypertrack.hyperlog.HLCallback;
import com.hypertrack.hyperlog.HyperLog;
import com.hypertrack.hyperlog.error.HLErrorResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Log {
    private static String TAG = "INTEC";

    public static void init(Context content) {
        HyperLog.initialize(content, new Format(content));
        HyperLog.setLogLevel(android.util.Log.INFO); // TODO configurable
    }

    public static String get() {
        List<String> logs = HyperLog.getDeviceLogsAsStringList(false);
        Collections.reverse(logs);
        return TextUtils.join("\n", logs.subList(0, Math.min(200, logs.size())));
    }

    public static void e(String message) {
        HyperLog.e(TAG, message);
    }

    public static void e(String message, Throwable e) {
        HyperLog.e(TAG, message + '\n' + android.util.Log.getStackTraceString(e));
    }

    public static void i(String message) {
        HyperLog.i(TAG, message);
    }

    public static void i(String message, Throwable e) {
        HyperLog.i(TAG, message + '\n' + android.util.Log.getStackTraceString(e));
    }

    public static void w(String message) {
        HyperLog.w(TAG, message);
    }

    public static void w(String message, Throwable e) {
        HyperLog.w(TAG, message + '\n' + android.util.Log.getStackTraceString(e));
    }

    public static void clear() {
        HyperLog.deleteLogs();
    }

    public static void pushLog(Context context, Map<String, String> header) {
        HyperLog.setURL(API_NET + "/api/driver/log");

        HashMap<String, String> _header = new HashMap<String, String>();
        for (String i : header.keySet()) {
            _header.put(i, header.get(i));
        }

        HyperLog.pushLogs(context, _header, true, new HLCallback() {
            @Override
            public void onSuccess(@NonNull Object response) {
                // ...
                i("temp");
            }

            @Override
            public void onError(@NonNull HLErrorResponse HLErrorResponse) {
                // ...
                i("temp");
            }
        });
    }
}
