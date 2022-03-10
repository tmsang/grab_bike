package com.intec.grab.bike.utils.helper;

import android.os.Handler;
import android.os.Looper;

public class TimerHelper {

    public static void Delay(int miliSecond, MyStringCallback callback)
    {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if (callback != null) callback.execute("");
            }
        }, miliSecond);
    }
}
