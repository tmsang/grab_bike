package com.intec.grab.bike.messages;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.api.SSLSettings;
import com.intec.grab.bike.utils.base.SETTING;
import com.intec.grab.bike.utils.helper.CommonHelper;
import com.intec.grab.bike.utils.helper.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity
{
    private SETTING settings;
    private RecyclerView recyclerView;
    private List<MessageDto> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        settings = new SETTING(this);
        loadMessages();
    }

    private void loadMessages()
    {
        // Add Header into request (Retrofit)
        Map<String, String> headers = new HashMap<>();
        headers.put("X-BoltzMessenger-UserKey", settings.userKey());

        SSLSettings sslSettings = new SSLSettings(true, null);
        SharedService.messageApi(Constants.API_BOLTZ, sslSettings)
            .getMessages(headers)
            .enqueue(Callback.callInUI(MessagesActivity.this, (json) -> {
                // TODO: refactor 0
                for (MessageMappingOut obj: json) {
                    messageList.add(new MessageDto(
                            R.drawable.chat_new_b_48,
                            obj.Title,
                            obj.Description,
                            StringHelper.convertToString(obj.PublishedDate, "dd-MMM")
                    ));
                }

                // load RecycleView
                messageAdapter = new MessageAdapter(this, 0, messageList);

                recyclerView = (RecyclerView) findViewById(R.id.rcv_messages);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(messageAdapter);

            }, (error) -> CommonHelper.showToast(
                    MessagesActivity.this,
                    getString(R.string.version_failed_status_code, Constants.API_BOLTZ + "/messages", error.code()),
                    error.body())
            ));
    }
}