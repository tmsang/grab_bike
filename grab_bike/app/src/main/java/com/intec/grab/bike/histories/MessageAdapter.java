package com.intec.grab.bike.histories;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.api.SSLSettings;
import com.intec.grab.bike.utils.base.SETTING;
import com.intec.grab.bike.utils.helper.StringHelper;
import com.intec.grab.bike.utils.log.Log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class MessageItemViewHolder extends RecyclerView.ViewHolder {
    public TextView autoNumber;
    public TextView title;
    public TextView publishDate;
    public ImageView imgRemarked;

    public MessageItemViewHolder(View itemView) {
        super(itemView);
        autoNumber = itemView.findViewById(R.id.lbl_autonumber);
        title = itemView.findViewById(R.id.title);
        publishDate = itemView.findViewById(R.id.publishDate);
        imgRemarked = itemView.findViewById(R.id.img_remarked);
    }

    public void bind(MessageOut message, int position) {
        String _title = message.ToAddress;
        String _publishDate = StringHelper.formatDateTime(message.RequestDateTime);

        autoNumber.setText(Html.fromHtml("<b>" + (position + 1) + "</b>"));
        title.setText(Html.fromHtml("<b>" + _title + "</b>"));
        publishDate.setText(Html.fromHtml("<span style=\"color:#0000ff;\">" + _publishDate + "</span>"));
        if (message.Status.equals(MessageStatus.EVALUATION)) {
            imgRemarked.setVisibility(View.VISIBLE);
        } else {
            imgRemarked.setVisibility(View.GONE);
        }
    }
}

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Activity activity;
    private List<MessageOut> items;
    private RecyclerView recyclerView;
    private SETTING settings;
    private SSLSettings sslSettings;
    private Class destinationActivity;

    public MessageAdapter(
            Activity activity,
            List<MessageOut> items,
            RecyclerView recyclerView,
            SETTING settings,
            SSLSettings sslSettings,
            Class destinationActivity)
    {
        this.activity = activity;
        this.items = items;
        this.recyclerView = recyclerView;
        this.settings = settings;
        this.sslSettings = sslSettings;
        this.destinationActivity = destinationActivity;
    }

    @Override
    public int getItemViewType(int position) {
        // compare position
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.activity_histories_item, parent, false);
        final RecyclerView.ViewHolder holder = new MessageItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageOut item = items.get(position);
        MessageItemViewHolder viewHolder = (MessageItemViewHolder) holder;
        viewHolder.bind(item, position);

        holder.itemView.setOnClickListener(v -> {
            // redirect to History Detail
            Intent intent = new Intent(activity, destinationActivity);
            intent.putExtra("message", item);
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
