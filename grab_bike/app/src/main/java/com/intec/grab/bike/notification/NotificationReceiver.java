package com.intec.grab.bike.notification;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("title");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        // load url into Browser
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com/messages/?content=" + message));
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);        // | Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(context, browserIntent, null);
    }
}
