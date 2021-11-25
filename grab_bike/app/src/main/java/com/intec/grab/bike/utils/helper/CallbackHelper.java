package com.intec.grab.bike.utils.helper;

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

/*
    HOW TO USE:

        new CallbackAPI(new CallbackAPI.TaskListener() {
            @Override
            public void onFinished(String result) throws JSONException {
                JSONArray arr = new JSONArray(result);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    dataList.add(new MessageDto(
                            R.drawable.dialog,
                            obj.getString("title"),
                            obj.getString("description")
                    ));
                }

                MessageAdapter messageAdapter = new MessageAdapter(MessagesActivity.this,
                        R.layout.custom_list, dataList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent){
                        View view = super.getView(position, convertView, parent);
                        view.setBackground(getContext().getDrawable(R.drawable.shape));
                        return view;
                    }
                };
                list.setAdapter(messageAdapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MessageDto selectedItem = (MessageDto) parent.getItemAtPosition(position);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/?title=" + selectedItem.getLine1()));
                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);        // | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(browserIntent);
                    }

                });
            }
        }).execute("{" +
                "'url': '" + Constants.API_URL_MESSAGES + "', " +
                "'method': 'GET', " +
                "'headers': [{'Content-Type': 'application/json'}, {'X-BoltzMessenger-UserKey': '" + Variables.UserKey + "'}]" +
                "}");*/