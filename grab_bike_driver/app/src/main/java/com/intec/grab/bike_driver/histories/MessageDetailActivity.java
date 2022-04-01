package com.intec.grab.bike_driver.histories;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;

import androidx.appcompat.widget.Toolbar;

import com.intec.grab.bike_driver.MainActivity;
import com.intec.grab.bike_driver.R;
import com.intec.grab.bike_driver.about.AboutActivity;
import com.intec.grab.bike_driver.configs.Constants;
import com.intec.grab.bike_driver.login.LoginActivity;
import com.intec.grab.bike_driver.messages.MessageOut;
import com.intec.grab.bike_driver.messages.MessagesActivity;
import com.intec.grab.bike_driver.settings.SettingsActivity;
import com.intec.grab.bike_driver.shared.SharedService;
import com.intec.grab.bike_driver.utils.api.Callback;
import com.intec.grab.bike_driver.utils.base.BaseActivity;
import com.intec.grab.bike_driver.utils.helper.StringHelper;

public class MessageDetailActivity extends BaseActivity
{
    private MessageHistoryOut message;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histories_item_detail);
        Initialization(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ratingBar = findViewById(R.id.rating);

        //1. Load data, map data into control
        Intent i = getIntent();
        message = (MessageHistoryOut)i.getSerializableExtra("message");

        SetTextView(R.id.lbl_from_value, message.FromAddress);
        SetTextView(R.id.lbl_to_value, message.ToAddress);
        SetTextView(R.id.lbl_distance, "<b>Distance: </b>" + StringHelper.formatNumber(message.Distance, "#,###") + " (km)");
        SetTextView(R.id.lbl_amount, "<b>Amount: </b>" + StringHelper.formatNumber(message.Cost, "#,###") + " (vnd)");
        SetTextView(R.id.lbl_datetime_value, StringHelper.formatDateTime(message.RequestDateTime));
        SetTextView(R.id.lbl_start_value, StringHelper.formatTime(message.Start) + "");
        SetTextView(R.id.lbl_end_value, StringHelper.formatTime(message.End) + "");
        SetTextView(R.id.lbl_guest_name_value, message.GuestName + "");
        SetTextView(R.id.lbl_guest_phone_value, StringHelper.formatPhone(message.GuestPhone) + "");
        SetTextView(R.id.lbl_status_value,  MessageShared.RenderEnumFromOrderStatus(message.Status) + "");
        ratingBar.setRating(message.Rating);
        SetTextView(R.id.lbl_remark_value,  message.Note);
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
            case R.id.toolbar_home:
                this.Redirect(MainActivity.class);
                return true;
            case R.id.toolbar_messages:
                this.Redirect(MessagesActivity.class);
                return true;
            case R.id.toolbar_histories:
                this.Redirect(HistoriesActivity.class);
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
}
