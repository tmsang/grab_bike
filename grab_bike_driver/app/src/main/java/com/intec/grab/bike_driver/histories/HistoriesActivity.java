package com.intec.grab.bike_driver.histories;

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
import com.intec.grab.bike_driver.messages.MessageOut;
import com.intec.grab.bike_driver.shared.SharedService;
import com.intec.grab.bike_driver.utils.api.Callback;
import com.intec.grab.bike_driver.utils.base.BaseActivity;
import com.intec.grab.bike_driver.utils.helper.MyStringCallback;
import com.intec.grab.bike_driver.utils.helper.StringHelper;

import java.util.ArrayList;
import java.util.List;

public class HistoriesActivity extends BaseActivity {

    private List<MessageHistoryOut> messageList = new ArrayList<>();
    private HistoryAdapter messageAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histories);
        Initialization(this);

        if (StringHelper.isNullOrEmpty(settings.jwtToken())) {
            this.Redirect(LoginActivity.class);
            return;
        }

        FrameLayout loading = (FrameLayout) findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.message_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new HistoryAdapter(this, messageList, recyclerView,
                settings,
                sslSettings,
                MessageDetailActivity.class);
        recyclerView.setAdapter(messageAdapter);

        loadMessagesFromApi("ALL", v -> {
            if (messageList != null && messageList.size() > 0) {
                findViewById(R.id.lbl_error_message).setVisibility(View.GONE);
            } else {
                findViewById(R.id.lbl_error_message).setVisibility(View.VISIBLE);
            }

            messageAdapter = new HistoryAdapter(this, messageList, recyclerView,
                    settings,
                    sslSettings,
                    MessageDetailActivity.class);
            recyclerView.setAdapter(messageAdapter);

            loading.setVisibility(View.GONE);
        });
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
        SharedService.MessageApi(Constants.API_NET, sslSettings)
                .RequestHistories(header)
                .enqueue(Callback.callInUI(HistoriesActivity.this,
                        (json) -> {
                            // TODO: refactor
                            messageList = json;
                            callback.execute("");
                        },
                        (error) -> {
                            HandleException("Histories", error.body());
                        }
                ));
    }
}
