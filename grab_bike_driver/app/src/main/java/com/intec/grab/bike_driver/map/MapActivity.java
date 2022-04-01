package com.intec.grab.bike_driver.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.intec.grab.bike_driver.R;
import com.intec.grab.bike_driver.configs.Constants;
import com.intec.grab.bike_driver.messages.MessageOut;
import com.intec.grab.bike_driver.messages.MessagesActivity;
import com.intec.grab.bike_driver.shared.SharedService;
import com.intec.grab.bike_driver.utils.api.Callback;
import com.intec.grab.bike_driver.utils.base.BaseActivity;

import com.intec.grab.bike_driver.utils.helper.StringHelper;
import com.intec.grab.bike_driver.utils.log.Log;
import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapScene;
import com.microsoft.maps.MapView;
import com.microsoft.maps.Geopoint;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.DisposableSubscriber;

public class MapActivity extends BaseActivity {
    private int POLL_INTERVAL = 10;
    private int DELAY_TIME = 2;
    private DisposableSubscriber<Long> subscriberDelayInterval;

    private MessageOut message;
    private MapView mMapView;
    private MapGUI mapGUI;

    private boolean isAllowBacked = false;
    private String fromLat = "", fromLng = "", fromAddress = "", fromCoordinate = "";
    private String toLat = "", toLng = "", toAddress = "", toCoordinate = "";

    /*===============================================
        All functions
        1. Map
            -> view map
            -> show bing map
            -> set zoom from current position
            -> attach PIN on Map
            -> set routh path (red line)
        2. Action
            -> set "CALL..." action
            -> set "START" action
            -> set "FINISH" action

    ================================================*/

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_map);
        Initialization(this);

        mapGUI = new MapGUI(this, settings, sslSettings);

        // 0. get intent parameter
        Intent i = getIntent();
        message = (MessageOut)i.getSerializableExtra("message");
        SetTextView(R.id.lblTitle, message.GuestName + "(" + message.GuestPhone + ")");
        SetTextView(R.id.lblSubTitle1, "<span style='color:#ffff00'><u>From</u></span>: " + message.FromAddress);
        SetTextView(R.id.lblSubTitle2, "<span style='color:#ffff00'><u>To</u></span>: " + message.ToAddress);
        SetTextView(R.id.lblDistance, "<span style='color:#ffff00'><u>Distance</u></span>: " + StringHelper.formatNumber(message.Distance, "#,###") + " km");
        SetTextView(R.id.lblAmount, "<span style='color:#ffff00'><u>Amount</u></span>: " + StringHelper.formatNumber(message.Cost, "#,###") + " vnd");

        // prepare [FROM] position variable
        fromLat = settings.currentLat();
        fromLng = settings.currentLng();
        fromAddress = settings.currentAddress();
        fromCoordinate = fromLat + "," + fromLng;
        toLat = message.ToLat;
        toLng = message.ToLng;
        toAddress = message.ToAddress;
        toCoordinate = toLat + "," + toLng;

        //1. Show Bing-Map
        //a. show map
        mMapView = new MapView(this, MapRenderMode.VECTOR);  // or use MapRenderMode.RASTER for 2D map
        mMapView.setCredentialsKey(Constants.BING_MAP_KEY);         // BuildConfig.CREDENTIALS_KEY
        ((FrameLayout)findViewById(R.id.map_view)).addView(mMapView);
        mMapView.onCreate(savedInstanceState);

        //b. set Zoom from current position
        // request permission Bing Map
        mapGUI.RequestGPSBingMap(mMapView, s -> {
            Log.i("GPS Bing Map is granted");
        });

        // set current location (Driver)
        Geopoint centerPoint = new Geopoint(Double.valueOf(fromLat), Double.valueOf(fromLng));
        mMapView.setScene(
                MapScene.createFromLocationAndZoomLevel(centerPoint, 16),
                MapAnimationKind.NONE);

        //c. attach PIN (guest address) on Map
        mapGUI.AttachPinOnMap(
                mMapView,
                "Me",
                Double.valueOf(message.GuestLat),
                Double.valueOf(message.GuestLng),
                R.drawable.ic_current_position_red);

        //d. set routh path (red line)
        String toGuestPosition = message.GuestLat + "," + message.GuestLng;
        mapGUI.DrawLineOnMap(mMapView, fromCoordinate, toGuestPosition);

        //2. Action
        //a. set "Call" phone
        // Request permission Phone
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
        this.ButtonClickEvent(R.id.btnCallPhone, (btn) -> {
            // call Intent in Android
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + message.GuestPhone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        //b. set "Start" to server
        this.ButtonClickEvent(R.id.btnStart, (btn) -> {
            if (IsNullOrEmpty(message.OrderId, "Order Id")) return;

            SharedService.MessageApi(Constants.API_NET, sslSettings)
                .Start(header, message.OrderId)
                .enqueue(Callback.callInUI(MapActivity.this, (result) -> {
                    Toast("Start is success on orderId (" + message.OrderId + "). Please wait Driver response");

                    // set routh path -> destination
                    mapGUI.DrawLineOnMap(mMapView, fromCoordinate, toCoordinate);
                    // Change button
                    btn.setVisibility(View.GONE);
                    Button btnEnd = this.findViewById(R.id.btnEnd);
                    btnEnd.setVisibility(View.VISIBLE);

                }, (error) -> {
                    Toast("API Start cannot reach", error.body());
                }));
        });

        //c. set "End" to server
        this.ButtonClickEvent(R.id.btnEnd, (btn) -> {
            if (IsNullOrEmpty(message.OrderId, "Order Id")) return;

            SharedService.MessageApi(Constants.API_NET, sslSettings)
                    .End(header, message.OrderId)
                    .enqueue(Callback.callInUI(MapActivity.this, (result) -> {
                        Toast("Start is success on orderId (" + message.OrderId + "). Please wait Driver response");
                        // clear routh path
                        mapGUI.ClearLineRoutePath(mMapView);

                        // clear intent: item(MessageOut)
                        settings.currentMessage(null);
                        isAllowBacked = true;

                        // redirect to Message
                        Redirect(MessagesActivity.class);
                    }, (error) -> {
                        Toast("API End cannot reach", error.body());
                    }));
        });
    }

    private void createIntervalSubscriber() {
        subscriberDelayInterval = mapGUI.CreatePushDriverPositionIntervalSubscriber(header, mMapView);
        Flowable.interval(DELAY_TIME, POLL_INTERVAL, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriberDelayInterval);
    }

    private void DestroyIntervalSubscriber() {
        if (subscriberDelayInterval != null && !subscriberDelayInterval.isDisposed()) {
            subscriberDelayInterval.dispose();
        }
        subscriberDelayInterval = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

        // release: Map + Observable
        mMapView = null;
        //DestroyIntervalSubscriber();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        Toast("You must process this Message [Start, End]");
        if (isAllowBacked) {
            super.onBackPressed();
        }
    }
}
