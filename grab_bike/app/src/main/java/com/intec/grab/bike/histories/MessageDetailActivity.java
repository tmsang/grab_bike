package com.intec.grab.bike.histories;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RatingBar;

import androidx.appcompat.widget.Toolbar;

import com.intec.grab.bike.MainActivity;
import com.intec.grab.bike.R;
import com.intec.grab.bike.about.AboutActivity;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.guest_map.GuestMapActivity;
import com.intec.grab.bike.login.LoginActivity;
import com.intec.grab.bike.settings.SettingsActivity;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.base.BaseActivity;
import com.intec.grab.bike.utils.helper.StringHelper;

public class MessageDetailActivity extends BaseActivity
{
    private FrameLayout loading;
    private MessageOut message;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histories_item_detail);
        Initialization(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loading = (FrameLayout) findViewById(R.id.loading);
        ratingBar = findViewById(R.id.rating);

        //1. Load data, map data into control
        Intent i = getIntent();
        message = (MessageOut)i.getSerializableExtra("message");

        SetTextView(R.id.lbl_from_value, message.FromAddress);
        SetTextView(R.id.lbl_to_value, message.ToAddress);
        SetTextView(R.id.lbl_distance, "<b>Distance: </b>" + StringHelper.formatNumber(message.Distance, "#,###") + " (km)");
        SetTextView(R.id.lbl_amount, "<b>Amount: </b>" + StringHelper.formatNumber(message.Cost, "#,###") + " (vnd)");
        SetTextView(R.id.lbl_datetime_value, StringHelper.formatDateTime(message.RequestDateTime));
        SetTextView(R.id.lbl_driver_name_value, message.DriverName + "");
        SetTextView(R.id.lbl_driver_phone_value,  StringHelper.formatPhone(message.DriverPhone) + "");
        SetTextView(R.id.lbl_status_value,  MessageShared.RenderEnumFromOrderStatus(message.Status) + "");
        ratingBar.setRating(message.Rating);
        EditText(R.id.txtRemark, message.Note);

        //2. Evaluate action
        ButtonClickEvent(R.id.btnEvaluate, v -> {
            if (message.Status.equals(MessageStatus.END)) {
                findViewById(R.id.lbl_error_message).setVisibility(View.GONE);
            } else {
                if (message.Status.equals(MessageStatus.EVALUATION)) {
                    SetTextView(R.id.lbl_error_message, "Your Booking was evaluated, cannot re-update again!");
                } else {
                    SetTextView(R.id.lbl_error_message, "Your Booking has not finished yet, so cannot evaluate");
                }
                findViewById(R.id.lbl_error_message).setVisibility(View.VISIBLE);
                return;
            }

            String remark = this.EditText(R.id.txtRemark).trim();
            float rating = ratingBar.getRating();
            if (this.IsNullOrEmpty(remark, "Remark")) {
                SetTextView(R.id.lbl_error_message, "Remark is null or empty");
                findViewById(R.id.lbl_error_message).setVisibility(View.VISIBLE);
                return;
            }

            SharedService.MessageApi(Constants.API_NET, sslSettings)
                .Evaluate(header, message.OrderId, rating, remark)
                .enqueue(Callback.call(
                        (json) -> {
                            // TODO: refactor
                            loading.setVisibility(View.VISIBLE);
                            Toast("Your rating is pushed successful");
                            Redirect(MessagesActivity.class);
                        },
                        (error) -> {
                            Toast("API - Evaluate (histories) cannot reach", error.body());
                        }
                ));
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
}
