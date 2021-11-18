package com.intec.grab.bike.messages;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.intec.grab.bike.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder>
{
    Context mContext;
    LayoutInflater mLayoutInflater;
    List<MessageDto> messageList;

    public MessageAdapter(Context context, int layout, List<MessageDto> messageList) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.messageList = messageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // map file layout into ViewHolder
        View item = mLayoutInflater.inflate(R.layout.activity_messages_items,parent,false);
        return new MessageViewHolder(this, item);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        MessageDto message = messageList.get(position);
        holder.line1.setText(message.getLine1());
        holder.line2.setText(message.getLine2());
        //holder.image.setImageResource(message.getImage());
        holder.image.setImageDrawable(ContextCompat.getDrawable(mContext, message.getImage()));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}

class MessageViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.textLine1) TextView line1;
    @BindView(R.id.textLine2) TextView line2;
    @BindView(R.id.imageView) ImageView image;

    public MessageViewHolder(MessageAdapter messageAdapter, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/?title=" + line1.getText()));
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);        // | Intent.FLAG_ACTIVITY_CLEAR_TOP
            messageAdapter.mContext.startActivity(browserIntent);
        });

        itemView.setOnLongClickListener(v -> {
            Toast.makeText(messageAdapter.mContext, "Long item clicked " + line1.getText(), Toast.LENGTH_SHORT).show();
            return true;
        });
    }
}
