package com.intec.grab.bike.histories;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.login.LoginActivity;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.api.SSLSettings;
import com.intec.grab.bike.utils.base.BaseActivity;
import com.intec.grab.bike.utils.helper.MyStringCallback;
import com.intec.grab.bike.utils.helper.StringHelper;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends BaseActivity {

    private List<MessageOut> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
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
        messageAdapter = new MessageAdapter(this, messageList, recyclerView,
                settings,
                sslSettings,
                MessageDetailActivity.class);
        recyclerView.setAdapter(messageAdapter);

        loadMessagesFromApi("ALL", v -> {
            messageAdapter = new MessageAdapter(this, messageList, recyclerView,
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
                this.Redirect(LoginActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadMessagesFromApi(String option, MyStringCallback callback)
    {
        SharedService.MessageApi(Constants.API_NET, sslSettings)
                .Requests(header)
                .enqueue(Callback.callInUI(MessagesActivity.this,
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
