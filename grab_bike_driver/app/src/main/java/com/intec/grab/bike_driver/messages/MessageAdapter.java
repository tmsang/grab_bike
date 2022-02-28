package com.intec.grab.bike_driver.messages;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.intec.grab.bike_driver.R;
import com.intec.grab.bike_driver.utils.base.SETTING;
import com.intec.grab.bike_driver.utils.helper.StringHelper;

import java.util.List;

class MessageItemViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView publishDate;

    public MessageItemViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        publishDate = itemView.findViewById(R.id.publishDate);
    }

    public void bind(MessageOut message) {
        title.setText(Html.fromHtml("<b>" + message.GuestName + "</b>"));
        publishDate.setText(Html.fromHtml("<span style=\"color:#0000ff;\">" + StringHelper.formatDateTime(message.RequestDateTime) + "</span>"));
    }
}

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Activity activity;
    private List<MessageOut> items;
    private RecyclerView recyclerView;
    private SETTING settings;

    public MessageAdapter(Activity activity, List<MessageOut> items, RecyclerView recyclerView) {
        this.activity = activity;
        this.items = items;
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int position) {
        // compare position
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.activity_message, parent, false);
        final RecyclerView.ViewHolder holder = new MessageItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageOut item = items.get(position);
        MessageItemViewHolder viewHolder = (MessageItemViewHolder) holder;
        viewHolder.bind(item);

        holder.itemView.setOnClickListener(v -> {
            // redirect to Bing Map
            Intent intent = new Intent(this, MapActivity);
            startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
