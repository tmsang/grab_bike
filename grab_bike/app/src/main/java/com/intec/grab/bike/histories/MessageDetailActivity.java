package com.intec.grab.bike.histories;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;

import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.base.BaseActivity;
import com.intec.grab.bike.utils.helper.StringHelper;

public class MessageDetailActivity extends BaseActivity
{
    private MessageOut message;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histories_item_detail);
        Initialization(this);

        ratingBar = findViewById(R.id.rating);

        //1. Load data, map data into control
        Intent i = getIntent();
        message = (MessageOut)i.getSerializableExtra("message");

        SetTextView(R.id.lbl_from_value, message.FromAddress);
        SetTextView(R.id.lbl_to_value, message.ToAddress);
        SetTextView(R.id.lbl_distance, "<b>Distance: </b>" + message.Distance + " km");
        SetTextView(R.id.lbl_amount, "<b>Amount: </b>" + message.Cost + " vnd");
        SetTextView(R.id.lbl_datetime_value, StringHelper.formatDateTime(message.RequestDateTime));
        SetTextView(R.id.lbl_driver_name_value, message.DriverName + "");
        SetTextView(R.id.lbl_driver_phone_value, message.DriverPhone + "");
        SetTextView(R.id.lbl_status_value,  MessageShared.RenderEnumFromOrderStatus(message.Status) + "");

        //2. Evaluate action
        ButtonClickEvent(R.id.btnEvaluate, v -> {
            if (message.Status.equals(MessageStatus.END)) {
                findViewById(R.id.lbl_error_message).setVisibility(View.GONE);
            } else {
                findViewById(R.id.lbl_error_message).setVisibility(View.VISIBLE);
                return;
            }

            String remark = this.EditText(R.id.txtRemark).trim();
            float rating = ratingBar.getRating();

            if (this.IsNullOrEmpty(remark, "Remark")) return;

            SharedService.MessageApi(Constants.API_NET, sslSettings)
                .Evaluate(header, message.OrderId, rating, remark)
                .enqueue(Callback.call(
                        (json) -> {
                            // TODO: refactor
                            Toast("Your rating is pushed successful");
                        },
                        (error) -> {
                            Toast("API - Evaluate (histories) cannot reach", error.body());
                        }
                ));
        });
    }
}
