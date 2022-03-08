package com.intec.grab.bike;

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
import com.intec.grab.bike.about.AboutActivity;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.guest_map.GuestBingMapApi;
import com.intec.grab.bike.guest_map.GuestMapActivity;
import com.intec.grab.bike.histories.MessagesActivity;
import com.intec.grab.bike.login.LoginActivity;
import com.intec.grab.bike.settings.SettingsActivity;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.base.BaseActivity;
import com.intec.grab.bike.utils.helper.StringHelper;
import com.intec.grab.bike.utils.log.Log;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    /*=========================================
        All functions Main (store important requirements)
        0. Check Token
            -> redirect to login
        1. Toggle Menu
            -> load menu
            -> set action + text on menu
        2. Request permission (access location)
            -> convert coordinate -> address
            -> push current position
        3. Load Statistic - Summary
    ===========================================*/

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initialization(this);
        ButterKnife.bind(this);

        //0. Check Token
        if (!settings.tokenExists()) {
            this.Redirect(LoginActivity.class);
            return;
        }

        //1. Toggle Menu
        toggleMenu();

        //2. Request permission
        // TODO: consider Activity is destroyed (when User redirect!) - sure: Guest must pust position
        this.RequestPermissionLocation((result) -> {
            Log.i("Request permission is granted - current position is stored at Pref");

            // Convert coordinate -> address
            GuestBingMapApi.instance.getAddressByLocation(this, settings.currentLat(), settings.currentLng(), (address) -> {
                settings.currentAddress(address);
            });

            // Push current guest position
            SharedService.GuestMapApi(Constants.API_NET, sslSettings)
                    .PushPosition(header, settings.currentLat(), settings.currentLng())
                    .enqueue(Callback.call((res) -> {
                        Log.i("Guest position has been pushed - successfully");
                    }, (error) -> {
                        HandleException("Push Position", error.body());
                    }));
        });

        //3. Load Statistic - Summary
        String nowStr = StringHelper.formatNow("dd-MMM-yyyy");
        String priceStr = StringHelper.formatNumber("6700", "#,###");
        String cancelCountStr = StringHelper.formatNumber("3", "#,###");
        String doneCountStr = StringHelper.formatNumber("3", "#,###");
        String amountStr = StringHelper.formatNumber("234000", "#,###");

        SetTextView(R.id.lbl_price, "<b>Price of 1 km on " + nowStr + ":</b> " + priceStr + " vnd");
        SetTextView(R.id.lbl_count_cancel, "<b>Total CANCEL trip:</b> " + cancelCountStr);
        SetTextView(R.id.lbl_count_done, "<b>Total DONE trip:</b> " + doneCountStr);
        SetTextView(R.id.lbl_total_amount, "<b>Total Amount:</b> " + amountStr + " vnd");
        SetTextView(R.id.lbl_book_a_trip, "<span style='color:#0000ff'><u>Let's go: Book A Trip!</u></span>");

        TextViewClickEvent(R.id.lbl_book_a_trip, lbl -> {
            Redirect(GuestMapActivity.class);
        });

        /*
        SharedService.MessageApi(Constants.API_NET, sslSettings)
                .Statistic(header)
                .enqueue(Callback.call((res) -> {
                    SetTextView(R.id.lbl_price, "Price of 1 km on 2022-03-08: 6,700 vnd");
                    SetTextView(R.id.lbl_count_cancel, "Total CANCEL trip: 3");
                    SetTextView(R.id.lbl_count_done, "Total DONE trip: 3");
                    SetTextView(R.id.lbl_total_amount, "Total Amount: 234,000 vnd");
                    SetTextView(R.id.lbl_book_a_trip, "Let's go: Book A Trip!");
                }, (error) -> {
                    HandleException("Statistic - Summary", error.body());
                }));
         */
    }

    private void toggleMenu() {
        // load menu
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

        // set action & text
        TextView leftMenuTitle = headerView.findViewById(R.id.left_menu_title);
        leftMenuTitle.setText(settings.fullName());
        TextView leftMenuEmail = headerView.findViewById(R.id.left_menu_email);
        leftMenuEmail.setText(settings.email());

        ImageButton leftMenuRefresh = headerView.findViewById(R.id.left_menu_refresh);
        leftMenuRefresh.setOnClickListener((v) -> {
            // TODO: refresh button
            // ...
        });
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
            this.Redirect(MessagesActivity.class);
        }
        else if (id == R.id.nav_settings)
        {
            this.Redirect(SettingsActivity.class);
        }
        else if (id == R.id.nav_about)
        {
            this.Redirect(AboutActivity.class);
        }
        else if (id == R.id.nav_logout)
        {
            settings.clear();
            this.Redirect(LoginActivity.class);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}