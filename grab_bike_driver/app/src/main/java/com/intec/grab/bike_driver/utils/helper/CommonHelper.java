package com.intec.grab.bike_driver.utils.helper;

import android.app.Activity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.intec.grab.bike_driver.configs.Constants;
import com.intec.grab.bike_driver.utils.base.JSON;
import com.intec.grab.bike_driver.utils.log.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.OffsetDateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okio.Buffer;


public class CommonHelper {
    public static String ajax(String url, String method, @Nullable JSONArray headers, @Nullable JSONObject params) {
        try {
            URL apiUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();

            conn.setRequestMethod(method);

            if (headers != null) {
                for (int i = 0; i < headers.length(); i++) {
                    JSONObject obj = headers.getJSONObject(i);
                    String key = obj.keys().next();
                    conn.setRequestProperty(key, obj.getString(key));
                }
            }
            conn.setDoInput(true);

            // Send message content.
            if (params != null) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(params.toString().getBytes());
            }

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            String response = ConvertStreamToString(inputStream);

            return response;
        } catch (IOException | JSONException e) {
            Log.e("Android API - Error ", e);
            return null;
        }
    }

    public static Hashtable<String, String> HashString(String json) {
        Hashtable<String, String> results = new Hashtable();
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> keys = jsonObject.keys();

            while(keys.hasNext()) {
                String key = keys.next();
                //if (jsonObject.get(key) instanceof JSONObject) {
                    results.put(key, (String)jsonObject.get(key));
                //}
            }
            return results;
        } catch (JSONException err){
            Log.e("JSON parse error: ", err);
            return null;
        }
    }

    private static String ConvertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    public static boolean Contains(String inputString, String[] items) {
        boolean found = true;
        for (String item : items) {
            if (!inputString.contains(item)) {
                found = false;
                break;
            }
        }
        return found;
    }

    public static boolean ValidPasswordRule(final String password) {
        Pattern pattern = Pattern.compile(Constants.PASSWORD_POLICY);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    //======================================
    // UTILS
    //======================================
    public static final Gson JSON = new JSON().getGson();

    public static void showSnackBar(Activity activity, String message) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showToast(Activity activity, String message, String... errors) {
        if (errors.length > 0 && errors[0] != null) {
            Log.e(errors[0]);
        }
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    public static int longToInt(long value) {
        return (int) (value % Integer.MAX_VALUE);
    }

    public static String dateToRelative(OffsetDateTime data) {
        long time = data.toInstant().toEpochMilli();
        long now = System.currentTimeMillis();
        return DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)
                .toString();
    }

    public static String resolveAbsoluteUrl(String baseURL, String target) {
        if (target == null) {
            return null;
        }
        try {
            URI targetUri = new URI(target);
            if (targetUri.isAbsolute()) {
                return target;
            }
            return new URL(new URL(baseURL), target).toString();
        } catch (MalformedURLException | URISyntaxException e) {
            Log.e("Could not resolve absolute url", e);
            return target;
        }
    }

    public static String readFileFromStream(@NonNull InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        String currentLine;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((currentLine = reader.readLine()) != null) {
                sb.append(currentLine).append("\n");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("failed to read input");
        }

        return sb.toString();
    }

    public static InputStream stringToInputStream(String str) {
        if (str == null) return null;
        return new Buffer().writeUtf8(str).inputStream();
    }

    public static <T> T first(T[] data) {
        if (data.length != 1) {
            throw new IllegalArgumentException("must be one element");
        }
        return data[0];
    }
}
