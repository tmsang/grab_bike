package com.intec.grab.bike.histories;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intec.grab.bike.MainActivity;
import com.intec.grab.bike.R;
import com.intec.grab.bike.about.AboutActivity;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.guest_map.GuestMapActivity;
import com.intec.grab.bike.login.LoginActivity;
import com.intec.grab.bike.settings.SettingsActivity;
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
    }

    @Override
    protected void onStart() {
        super.onStart();

        Load();
    }

    private void Load() {
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
            if (messageList != null && messageList.size() > 0) {
                findViewById(R.id.lbl_error_message).setVisibility(View.GONE);
            } else {
                findViewById(R.id.lbl_error_message).setVisibility(View.VISIBLE);
            }

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

        MenuItem item = menu.findItem(R.id.toolbar_histories);
        item.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.toolbar_home:
                this.Redirect(MainActivity.class);
                return true;
            case R.id.toolbar_booking:
                this.Redirect(GuestMapActivity.class);
                return true;
            case R.id.toolbar_histories:
                this.Redirect(MessagesActivity.class);
                return true;
            case R.id.toolbar_settings:
                this.Redirect(SettingsActivity.class);
                return true;
            case R.id.toolbar_about:
                this.Redirect(AboutActivity.class);
                return true;
            case R.id.toolbar_logout:
                settings.clear();
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
                            HandleException("Histories", error);
                        }
                ));
    }
}
