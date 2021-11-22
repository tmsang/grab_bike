package com.intec.grab.bike;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.intec.grab.bike.login.LoginActivity;
import com.intec.grab.bike.utils.base.SETTING;
import com.intec.grab.bike.utils.log.Log;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
{
    SETTING settings;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // =============================================
        // Configuration First
        // =============================================
        ButterKnife.bind(this);
        Log.init(this);
        settings = new SETTING(this);

        if (!settings.tokenExists()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        // =============================================
        // Draw Listener
        // =============================================

    }

    public void onGoToMessages(View v){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void onLeftMenuRefresh(View v)
    {
        // TODO: refresh button
        // ...
    }
}