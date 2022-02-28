package com.intec.grab.bike_driver.utils.helper;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Parameters use parameters of doInBackground - AsyncTask<String, Void, String>
 * First Parameter <String> -> multi parameter of doInBackground -> String...
 * Second Parameter <Void> -> multi parameter of onProgressUpdate -> Void...
 * Last Parameter <String> -> return result of doInBackground
 *
 * Interface !important: this is callback - implement by @Override
 */
public class CallbackHelper extends AsyncTask<String, Void, String> {

    public interface TaskListener {
        void onFinished(String result) throws JSONException;
    }
    private final TaskListener taskListener;

    public CallbackHelper(TaskListener listener) {
        this.taskListener = listener;
    }

    @Override
    protected String doInBackground(String... items) {
        try {
            // JSON
            JSONObject jsonObject = new JSONObject(items[0]);
            Iterator<String> keys = jsonObject.keys();

            String url = "";
            String method = "";
            JSONArray headers = null;
            JSONObject params = null;

            while(keys.hasNext()) {
                String key = keys.next();
                if (key.equals("url")) {
                    url = (String)jsonObject.get(key);
                }
                if (key.equals("method")) {
                    method = (String)jsonObject.get(key);
                }
                if (key.equals("headers")) {
                    headers = (JSONArray) jsonObject.get(key);
                }
                if (key.equals("params")) {
                    params = (JSONObject) jsonObject.get(key);
                }
            }
            String response = CommonHelper.ajax(url, method, headers, params);

            return response;
        } catch (JSONException e) {
            Log.e("Rest API", "Error ", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {            // param: return (last parameter)
        super.onPostExecute(s);

        if (this.taskListener != null) {
            try {
                this.taskListener.onFinished(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
