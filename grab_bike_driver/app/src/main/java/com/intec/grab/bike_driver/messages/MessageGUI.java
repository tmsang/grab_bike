package com.intec.grab.bike_driver.messages;

import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.intec.grab.bike_driver.configs.Constants;
import com.intec.grab.bike_driver.map.MapActivity;
import com.intec.grab.bike_driver.shared.SharedService;
import com.intec.grab.bike_driver.utils.api.Callback;
import com.intec.grab.bike_driver.utils.api.SSLSettings;
import com.intec.grab.bike_driver.utils.base.SETTING;
import com.intec.grab.bike_driver.utils.helper.MyStringCallback;
import com.intec.grab.bike_driver.utils.helper.StringHelper;
import com.intec.grab.bike_driver.utils.log.Log;

import java.util.Map;

import io.reactivex.subscribers.DisposableSubscriber;

public class MessageGUI {

    Context context;
    SETTING settings;
    SSLSettings sslSettings;

    public MessageGUI(Context context, SETTING settings, SSLSettings sslSettings) {
        this.context = context;
        this.settings = settings;
        this.sslSettings = sslSettings;
    }
    public DisposableSubscriber<Long> CreateIntervalSubscriber(
            Map<String, String> header,
            MyStringCallback callback)
    {
        // Set subscriber
        return new DisposableSubscriber<Long>() {
            @Override
            public void onNext(Long aLong) {
                Log.i("------ Interval: Prepare (get new messages by interval) ------");
                SharedService.MessageApi(Constants.API_NET, sslSettings)
                        .IntervalGets(header)
                        .enqueue(Callback.call((rs) -> {
                            Log.i("Messages has been refreshed!");

                            callback.execute(StringHelper.stringify(rs));

                        }, (err) -> {
                            String msg = err.getCause() != null ? err.getCause().toString() : err.body().toString();
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            Log.i(msg);
                        }));
            }

            @Override
            public void onError(Throwable t) {
                Log.i("------ Interval: Error " + t.getMessage() + " ------");
            }

            @Override
            public void onComplete() {
                Log.i("------ Interval: Complete ------");
            }
        };

    }

    public static IntervalResultOut toJson(String str) {
        Gson gson = new Gson();
        IntervalResultOut result = gson.fromJson(str, IntervalResultOut.class);

        return result;
    }
}
