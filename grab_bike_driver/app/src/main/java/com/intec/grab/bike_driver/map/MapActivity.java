package com.intec.grab.bike_driver.map;

import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.intec.grab.bike_driver.R;
import com.intec.grab.bike_driver.configs.Constants;
import com.intec.grab.bike_driver.shared.SharedService;
import com.intec.grab.bike_driver.utils.api.Callback;
import com.intec.grab.bike_driver.utils.base.BaseActivity;

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

    private Map<String, String> header;
    private MapView mMapView;
    private MapGUI mapGUI;

    private String fromLat = "", fromLng = "", fromAddress = "", fromCoordinate = "";
    private String toLat = "", toLng = "", toAddress = "", toCoordinate = "";

    private String[] destinations = {""};
    private AutoCompleteTextView autoCompleteDestination;
    private ArrayAdapter destinationAdapter;

    /*===============================================
        All functions
        1. Map
            -> view map
                show bing map
                set zoom from current position
                attach PIN on Map
        2. Action
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

        // set header http
        header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Authorization", settings.jwtToken());

        // prepare [FROM] position variable
        fromLat = settings.currentLat();
        fromLng = settings.currentLng();
        fromAddress = settings.currentAddress();
        fromCoordinate = fromLat + "," + fromLng;

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

        Geopoint centerPoint = new Geopoint(Double.valueOf(fromLat), Double.valueOf(fromLng));
        mMapView.setScene(
                MapScene.createFromLocationAndZoomLevel(centerPoint, 16),
                MapAnimationKind.NONE);

        //c. attach PIN (guest address) on Map
        mapGUI.AttachPinOnMap(
                mMapView,
                "Me",
                Double.valueOf(fromLat),
                Double.valueOf(fromLng),
                R.drawable.ic_current_position);

        //2. Action
        //a. set "Start" to server
        this.ButtonClickEvent(R.id.btnAction, (btn) -> {
            if (IsNullOrEmpty(fromLat, "From Latitude")) return;
            if (IsNullOrEmpty(fromLng, "From Longitude")) return;
            if (IsNullOrEmpty(fromAddress, "From Address")) return;
            if (IsNullOrEmpty(toLat, "To Latitude")) return;
            if (IsNullOrEmpty(toLng, "To Longitude")) return;
            if (IsNullOrEmpty(toAddress, "To Address")) return;

            SharedService.MapApi(Constants.API_NET, sslSettings)
                .BookATrip(header, fromLat, fromLng, fromAddress, toLat, toLng, toAddress)
                .enqueue(Callback.callInUI(MapActivity.this, (result) -> {
                    Toast("Your Book is success. Please wait Driver response");

                    // publish
                    Flowable.interval(DELAY_TIME, POLL_INTERVAL, TimeUnit.SECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(subscriberDelayInterval);

                }, (error) -> {
                    Toast("API BookATrip cannot reach", error.body());
                }));
        });

        //b. pull driver positions (interval)
        // create subscribe
        subscriberDelayInterval = mapGUI.CreateDriverPositionIntervalSubscriber(header, mMapView);
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
        if (subscriberDelayInterval != null && !subscriberDelayInterval.isDisposed()) {
            subscriberDelayInterval.dispose();
        }
        subscriberDelayInterval = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
