package com.intec.grab.bike.guest_map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.intec.grab.bike.R;

import java.util.ArrayList;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder> {
    private ArrayList<String> names;

    public DestinationAdapter() {
        this.names = new ArrayList<String>();
    }

    @Override
    public DestinationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_guest_map_destination_item, null, false);
        return new DestinationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DestinationViewHolder holder, int position) {
        holder.tvName.setText(names.get(position));
    }

    @Override
    public int getItemCount() {
        return names != null ? names.size() : 0;
    }

    public void addDestinations(ArrayList<String> names) {
        this.names = names;
        notifyDataSetChanged();
    }

    public void removeAllDestinations() {
        names.clear();
        notifyDataSetChanged();
    }

    public class DestinationViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;

        public DestinationViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
