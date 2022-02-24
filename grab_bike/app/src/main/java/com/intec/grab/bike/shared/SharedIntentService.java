package com.intec.grab.bike.shared;

import android.Manifest;
import android.app.Application;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.intec.grab.bike.utils.base.SETTING;
import com.intec.grab.bike.utils.helper.StringHelper;


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
