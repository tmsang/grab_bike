package com.intec.grab.bike;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.intec.grab.bike.guest_map.GuestMapActivity;
import com.intec.grab.bike.login.LoginActivity;
import com.intec.grab.bike.shared.SharedIntentService;
import com.intec.grab.bike.utils.base.BaseActivity;
import com.intec.grab.bike.utils.log.Log;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    String token;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initialization(this);

        // =============================================
        // Configuration First
        // =============================================
        ButterKnife.bind(this);
        if (!settings.tokenExists()) {
            this.Redirect(LoginActivity.class);
            return;
        }

        // Start Background Service
        //Intent cbIntent =  new Intent();
        //cbIntent.setClass(this, SharedIntentService.class);
        //cbIntent.putExtra("param", "");
        //startService(cbIntent);

        // =============================================
        // Draw Listener
        // =============================================
        initDrawer();
    }

    private void initDrawer() {
        setSupportActionBar(toolbar);
        navigationView.setItemIconTintList(null);

        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(
                        this,
                        drawer,
                        toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView leftMenuTitle = headerView.findViewById(R.id.left_menu_title);
        leftMenuTitle.setText("Temp....");

        ImageButton leftMenuRefresh = headerView.findViewById(R.id.left_menu_refresh);
        leftMenuRefresh.setOnClickListener(this::onLeftMenuRefresh);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_bookings)
        {
            this.Redirect(GuestMapActivity.class);
        }
        else if (id == R.id.nav_histories)
        {
            Log.i("No action to Histories");
        }
        else if (id == R.id.nav_settings)
        {
            Log.i("No action to settings");
        }
        else if (id == R.id.nav_about)
        {
            Log.i("No action to About");
        }
        else if (id == R.id.nav_logout)
        {
            settings.clear();
            this.Redirect(LoginActivity.class);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onGoToMessages(View v){
        this.Redirect(LoginActivity.class);
    }

    public void onLeftMenuRefresh(View v)
    {
        // TODO: refresh button
        // ...
    }
}