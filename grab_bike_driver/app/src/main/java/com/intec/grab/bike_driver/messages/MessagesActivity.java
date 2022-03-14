package com.intec.grab.bike_driver.messages;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intec.grab.bike_driver.MainActivity;
import com.intec.grab.bike_driver.R;
import com.intec.grab.bike_driver.configs.Constants;
import com.intec.grab.bike_driver.login.LoginActivity;
import com.intec.grab.bike_driver.map.MapActivity;
import com.intec.grab.bike_driver.shared.SharedService;
import com.intec.grab.bike_driver.utils.api.Callback;
import com.intec.grab.bike_driver.utils.api.SSLSettings;
import com.intec.grab.bike_driver.utils.base.BaseActivity;
import com.intec.grab.bike_driver.utils.helper.MyStringCallback;
import com.intec.grab.bike_driver.utils.helper.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

public class MessagesActivity extends BaseActivity {
    
    private List<MessageOut> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private MessageGUI messageGUI;

    private int POLL_INTERVAL = 10;
    private int DELAY_TIME = 2;
    private DisposableSubscriber<Long> subscriberDelayInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Initialization(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Load();
    }

    /*====================================
        1. Check Token
        2. Check current message (pending -> just accept)
            . if over 3 minute -> stop
            . redirect to MapActivity
        3. Set Adapter (initial -> API full load)
        4. Real-time interval
    ======================================*/
    private void Load() {
        FrameLayout loading = (FrameLayout) findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        messageGUI = new MessageGUI(this, settings, sslSettings);

        // 1. Check Token
        if (StringHelper.isNullOrEmpty(settings.jwtToken())) {
            this.Redirect(LoginActivity.class);
            return;
        }

        // 2. Check current message (pending -> just accept)
        if (settings.currentMessage() != null) {
            // if over >= 3 minutes -> lock app ...
            MessageOut item = settings.currentMessage();
            long current = System.currentTimeMillis();
            double delta = (current - item.AcceptDateTime) / (60 * 1000);
            if ((double)delta > (double)3) {
                // down level of user || lock app
                Toast("You missed message of (" + item.GuestName + " - " + item.GuestPhone + ")");
                settings.currentMessage(null);
                // TODO: ...

            } else {
                Intent intent = new Intent(activity, MapActivity.class);
                intent.putExtra("message", item);
                activity.startActivity(intent);
            }
        }

        // 3. Set Adapter (initial -> API full load)
        recyclerView = findViewById(R.id.message_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(this, messageList, recyclerView,
                settings,
                sslSettings,
                MapActivity.class);
        recyclerView.setAdapter(messageAdapter);

        loadMessagesFromApi("ALL", v -> {
            messageAdapter = new MessageAdapter(this, messageList, recyclerView,
                    settings,
                    sslSettings,
                    MapActivity.class);
            recyclerView.setAdapter(messageAdapter);

            // publish
            Flowable.interval(DELAY_TIME, POLL_INTERVAL, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(subscriberDelayInterval);

            loading.setVisibility(View.GONE);
        });

        // 4. Real-time interval
        subscriberDelayInterval = messageGUI.CreateIntervalSubscriber(header, str -> {
            IntervalResultOut result = messageGUI.toJson(str);

            messageAdapter = new MessageAdapter(this, result.Requests, recyclerView,
                    settings,
                    sslSettings,
                    MapActivity.class);
            recyclerView.setAdapter(messageAdapter);
        });
    }

    private void loadMessagesFromApi(String option, MyStringCallback callback)
    {
        // Add Header into request (Retrofit)
        SharedService.MessageApi(Constants.API_NET, sslSettings)
                .Requests(header)
                .enqueue(Callback.callInUI(MessagesActivity.this,
                        (json) -> {
                            // TODO: refactor 0
                            messageList = json;
                            callback.execute("");
                        },
                        (error) -> {
                            Toast("API - GetRequest cannot reach", error.getMessage());
                        }
                ));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.toolbar_menu:
                this.Redirect(MainActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (subscriberDelayInterval != null && !subscriberDelayInterval.isDisposed()) {
            subscriberDelayInterval.dispose();
        }
        subscriberDelayInterval = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (subscriberDelayInterval != null && !subscriberDelayInterval.isDisposed()) {
            subscriberDelayInterval.dispose();
        }
        subscriberDelayInterval = null;
    }
}
