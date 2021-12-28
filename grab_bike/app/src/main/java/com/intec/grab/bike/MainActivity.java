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
import com.intec.grab.bike.guest_map.GuestMapActivity;
import com.intec.grab.bike.login.LoginActivity;
import com.intec.grab.bike.utils.base.SETTING;
import com.intec.grab.bike.utils.log.Log;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

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

        // EX: Redirect to BingMap Guest
        Intent intent = new Intent(this, GuestMapActivity.class);
        startActivity(intent);

        // =============================================
        // Draw Listener
        // =============================================
        // initDrawer();
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

        if (id == R.id.nav_all_messages) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            // clear cookie
            settings.clear();
            // redirect
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logs) {
            Log.i("No action to logs");
        } else if (id == R.id.nav_settings) {
            Log.i("No action to settings");
        } else if (id == R.id.nav_push_message) {
            Log.i("No action to push_message");
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
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