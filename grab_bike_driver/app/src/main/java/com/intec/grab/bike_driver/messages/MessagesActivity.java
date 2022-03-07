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

public class MessagesActivity extends BaseActivity {
    
    private List<MessageOut> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Initialization(this);

        Load();
    }

    private void Load() {
        if (StringHelper.isNullOrEmpty(settings.jwtToken())) {
            this.Redirect(LoginActivity.class);
            return;
        }
        if (settings.currentMessage() != null) {
            // if over >= 3 minutes -> lock app ...
            MessageOut item = settings.currentMessage();
            long current = System.currentTimeMillis();
            long delta = (current - item.AcceptDateTime) / (60 * 1000);
            if (delta > 3) {
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

        FrameLayout loading = (FrameLayout) findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

            loading.setVisibility(View.GONE);
        });
    }

    @Override
    public void onBackPressed() {
        Load();
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

    private void loadMessagesFromApi(String option, MyStringCallback callback)
    {
        // Add Header into request (Retrofit)
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Authorization", settings.jwtToken());

        SSLSettings sslSettings = new SSLSettings(false, null);
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
}
