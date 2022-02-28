package com.intec.grab.bike_driver.shared;

import android.app.IntentService;
import android.content.Intent;


public class SharedIntentService extends IntentService {

    final static String CALLBACK_INFO = "callback_info";

    public SharedIntentService() {
        super("Shared Intent Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String param = intent.getStringExtra("param");

        //...
    }

    private void sendCallbackToMainThread(String msg){
        Intent intent = new Intent();
        intent.setAction(CALLBACK_INFO);
        intent.putExtra("param", msg);
        sendBroadcast(intent);
    }
}
