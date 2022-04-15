package com.intec.grab.bike_driver.messages;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intec.grab.bike_driver.MainActivity;
import com.intec.grab.bike_driver.R;
import com.intec.grab.bike_driver.about.AboutActivity;
import com.intec.grab.bike_driver.configs.Constants;
import com.intec.grab.bike_driver.histories.HistoriesActivity;
import com.intec.grab.bike_driver.login.LoginActivity;
import com.intec.grab.bike_driver.map.BingMapApi;
import com.intec.grab.bike_driver.map.MapActivity;
import com.intec.grab.bike_driver.settings.SettingsActivity;
import com.intec.grab.bike_driver.shared.SharedService;
import com.intec.grab.bike_driver.utils.api.Callback;
import com.intec.grab.bike_driver.utils.api.SSLSettings;
import com.intec.grab.bike_driver.utils.base.BaseActivity;
import com.intec.grab.bike_driver.utils.helper.GPSTracker;
import com.intec.grab.bike_driver.utils.helper.MyStringCallback;
import com.intec.grab.bike_driver.utils.helper.StringHelper;
import com.intec.grab.bike_driver.utils.log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

public class MessagesActivity extends BaseActivity {
    
    private List<MessageOut> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private MessageGUI messageGUI;

    private int POLL_INTERVAL = 10;
    private int DELAY_TIME = 2;
    private DisposableSubscriber<Long> subscriberDelayInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Initialization(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();
        Load();
    }

    /*====================================
        1. Check Token
        2. Check current message (pending -> just accept)
            . if over 3 minute -> stop
            . redirect to MapActivity
        3. Set Adapter (initial -> API full load)
        4. Real-time interval
            . separate "registerForActivityResult" out of interval
            . automatically push position Driver (in Interval get)
    ======================================*/
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void Load() {
        FrameLayout loading = (FrameLayout) findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        messageGUI = new MessageGUI(this, settings, sslSettings);

        // 1. Check Token
        if (StringHelper.isNullOrEmpty(settings.jwtToken())) {
            this.Redirect(LoginActivity.class);
            return;
        }

        // 2. Check current message (pending -> just accept)
        if (settings.currentMessage() != null) {
            // if over >= 3 minutes -> lock app ...
            MessageOut item = settings.currentMessage();
            long current = System.currentTimeMillis();
            double delta = (current - item.AcceptDateTime) / (60 * 1000);

            Intent intent = new Intent(activity, MapActivity.class);
            intent.putExtra("message", item);
            activity.startActivity(intent);

            /*
            if ((double)delta > (double)3) {
                // down level of user || lock app
                Toast("You missed message of (" + item.GuestName + " - " + item.GuestPhone + ")");
                settings.currentMessage(null);
                // TODO: ...

            } else {
                Intent intent = new Intent(activity, MapActivity.class);
                intent.putExtra("message", item);
                activity.startActivity(intent);
            }*/
        }

        // 3. Set Adapter (initial -> API full load)
        recyclerView = findViewById(R.id.message_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setAdapter(messageList);

        loadMessagesFromApi("ALL", v -> {
            setAdapter(messageList);

            // publish
            Flowable.interval(DELAY_TIME, POLL_INTERVAL, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(subscriberDelayInterval);

            loading.setVisibility(View.GONE);

        });

        // separate "registerForActivityResult" out of interval
        // registerForActivityResult chi dang ky 1 lan, khi "launch" no se tu tim den day.... hehe
        ActivityResultLauncher<String[]> locationPermissions = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        // ===========================================
                        // process location result
                        // (xu ly tay, vi interval tren OnCreate -> loop registerForActivityResult -> loi)
                        // "... is attempting to register while current state is RESUMED. LifecycleOwners must call register before they are STARTED"
                        // ===========================================
                        Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                            GPSTracker gps = new GPSTracker(MessagesActivity.this);
                            if (gps.canGetLocation()) {
                                double lat = gps.getLatitude();
                                double lng = gps.getLongitude();
                                settings.currentLat(lat + "");
                                settings.currentLng(lng + "");

                                AnalyzeCurrentLocation();
                            }
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                            Toast("Only approximate location access granted");
                        } else {
                            // No location access granted.
                            Toast("No location access granted");
                        }
                    }
                });

        // 4. Real-time interval
        subscriberDelayInterval = messageGUI.CreateIntervalSubscriber(str -> {

            // Before you perform the actual permission request, check whether your app
            // already has the permissions, and whether your app needs to show a permission
            // rationale dialog. For more details, see Request permissions.
            locationPermissions.launch(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION

            // khi gap lenh launch nay -> he thong tu nhay vao onActivityResult thuc thi
            // tu AnalyzeCurrentLocation, roi getInterval
            });
        });
    }

    private void AnalyzeCurrentLocation() {
        // Convert coordinate -> address
        BingMapApi.instance.getAddressByLocation(this, settings.currentLat(), settings.currentLng(), (address) -> {
            settings.currentAddress(address);
        });

        // Push current position
        SharedService.MessageApi(Constants.API_NET, sslSettings)
                .IntervalGets(header, settings.currentLat(), settings.currentLng())
                .enqueue(Callback.call((res) -> {
                    Log.i("Driver position has been pushed - successfully");

                    setAdapter(res.Requests);

                }, (error) -> {
                    String message = error.getCause().toString();
                    HandleException("Driver Position", message);
                }));
    }

    private void setAdapter(List<MessageOut> requests) {
        if (requests != null && requests.size() > 0) {
            findViewById(R.id.lbl_error_message).setVisibility(View.GONE);
        } else {
            findViewById(R.id.lbl_error_message).setVisibility(View.VISIBLE);
        }

        messageAdapter = new MessageAdapter(this, requests, recyclerView,
                settings,
                sslSettings,
                loading,
                MapActivity.class);
        recyclerView.setAdapter(messageAdapter);
    }

    private void loadMessagesFromApi(String option, MyStringCallback callback)
    {
        // Add Header into request (Retrofit)
        SharedService.MessageApi(Constants.API_NET, sslSettings)
                .Requests(header)
                .enqueue(Callback.callInUI(MessagesActivity.this,
                        (json) -> {
                            // TODO: refactor 0
                            messageList = json;
                            callback.execute("");
                        },
                        (error) -> {
                            String message = error.getCause().toString();
                            Toast("API - GetRequest cannot reach: " + message, error.getMessage());
                        }
                ));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_settings, menu);

        MenuItem item = menu.findItem(R.id.toolbar_messages);
        item.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.toolbar_home:
                this.Redirect(MainActivity.class);
                return true;
            case R.id.toolbar_messages:
                this.Redirect(MessagesActivity.class);
                return true;
            case R.id.toolbar_histories:
                this.Redirect(HistoriesActivity.class);
                return true;
            case R.id.toolbar_settings:
                this.Redirect(SettingsActivity.class);
                return true;
            case R.id.toolbar_about:
                this.Redirect(AboutActivity.class);
                return true;
            case R.id.toolbar_logout:
                settings.clear();
                this.Redirect(LoginActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (subscriberDelayInterval != null && !subscriberDelayInterval.isDisposed()) {
            subscriberDelayInterval.dispose();
        }
        subscriberDelayInterval = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (subscriberDelayInterval != null && !subscriberDelayInterval.isDisposed()) {
            subscriberDelayInterval.dispose();
        }
        subscriberDelayInterval = null;
    }
}
