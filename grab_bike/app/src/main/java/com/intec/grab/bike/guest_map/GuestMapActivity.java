package com.intec.grab.bike.guest_map;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.histories.MessageDetailActivity;
import com.intec.grab.bike.histories.MessagesActivity;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.base.BaseActivity;

import com.intec.grab.bike.utils.helper.StringHelper;
import com.intec.grab.bike.utils.helper.TimerHelper;
import com.intec.grab.bike.utils.log.Log;
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

public class GuestMapActivity extends BaseActivity {
    private int POLL_INTERVAL = 10;
    private int DELAY_TIME = 2;
    private DisposableSubscriber<Long> subscriberDelayInterval;

    private Map<String, String> header;
    private MapView mMapView;
    private GuestMapGUI mapGUI;
    private Button btnBook;

    private String fromLat = "", fromLng = "", fromAddress = "", fromCoordinate = "";
    private String toLat = "", toLng = "", toAddress = "", toCoordinate = "";

    private String[] destinations = {""};
    private AutoCompleteTextView autoCompleteDestination;
    private ArrayAdapter destinationAdapter;

    /*===============================================
        All functions
        1. AutoComplete
            -> init data
            -> create publish/subscribe - suggestion
            -> event change text
                load new data on Suggestion Autocomplete
            -> event click item
                calculate distance & amount
                show line route on Map
        2. Map
            -> view map
                show bing map
                set zoom from current position
                attach PIN on Map
        3. Book action
            -> push Booking to server
            -> pull driver positions (interval)

        4. Restore session map
    ================================================*/

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_map);
        Initialization(this);

        mapGUI = new GuestMapGUI(this, settings, sslSettings);
        btnBook = findViewById(R.id.btnBook);

        // set header http
        header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Authorization", settings.jwtToken());

        // prepare [FROM] position variable
        fromLat = settings.currentLat();
        fromLng = settings.currentLng();
        fromAddress = settings.currentAddress();
        fromCoordinate = fromLat + "," + fromLng;

        // 1. AutoSuggest
        final PublishSubject<String> publisher = PublishSubject.create();

        // a. init data
        autoCompleteDestination =(AutoCompleteTextView)findViewById(R.id.autoCompleteDestination);
        destinationAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, destinations);
        autoCompleteDestination.setAdapter(destinationAdapter);
        autoCompleteDestination.setThreshold(1);

        // b. create publish/subscribe - suggestion
        // subscribe
        mapGUI.AutoComplete_CreateSuggestionListener(publisher, result -> {
            destinations = result.split("@");
            mapGUI.setSuggestions(destinations);
            destinationAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, destinations);
            autoCompleteDestination.setAdapter(destinationAdapter);
            destinationAdapter.notifyDataSetChanged();
        });
        // publish
        mapGUI.AutoComplete_OnChangeText(autoCompleteDestination, str -> {
            publisher.onNext(str);
        });

        // c. event click item
        mapGUI.AutoComplete_OnItemClick(autoCompleteDestination, coordinates -> {
            String[] items = coordinates.split("_");
            String _lat = items[0];
            String _lng = items[1];
            String _address = items[2];

            settings.sessionMap_SetToLat(_lat);
            settings.sessionMap_SetToLng(_lng);
            settings.sessionMap_SetToAddress(_address);

            LoadMapWhenConfirmDestination(_lat, _lng, _address);
        });

        //2. Show Bing-Map
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

        //c. attach PIN on Map
        mapGUI.AttachPinOnMap(
                mMapView,
                "Me",
                Double.valueOf(fromLat),
                Double.valueOf(fromLng),
                R.drawable.ic_current_position);

        //3. Book action
        //a. push Booking to server
        this.ButtonClickEvent(R.id.btnBook, (btn) -> {
            if (IsNullOrEmpty(fromLat, "From Latitude")) return;
            if (IsNullOrEmpty(fromLng, "From Longitude")) return;
            if (IsNullOrEmpty(fromAddress, "From Address")) return;
            if (IsNullOrEmpty(toLat, "To Latitude")) return;
            if (IsNullOrEmpty(toLng, "To Longitude")) return;
            if (IsNullOrEmpty(toAddress, "To Address")) return;
            if (mapGUI.getDistance() <= 0) { Toast("Distance is invalid"); return; }
            if (mapGUI.getAmount() <= 0) { Toast("Amount is invalid"); return; }

            SharedService.GuestMapApi(Constants.API_NET, sslSettings)
                .BookATrip(header, fromLat, fromLng, fromAddress, toLat, toLng, toAddress,
                        mapGUI.getDistance(), mapGUI.getAmount())
                .enqueue(Callback.callInUI(GuestMapActivity.this, (result) -> {
                    Toast("Your Book is success. Please wait Driver response");

                    // change text button -> Waiting... + remove event on it
                    btnBook.setText(Html.fromHtml("<span style='color: #ff0000'><b>Waiting...</b></span>"));
                    btnBook.setOnClickListener(null);
                    btnBook.setOnTouchListener(null);

                    settings.sessionMap_SetOrderId(result.OrderId);

                    // publish
                    Flowable.interval(DELAY_TIME, POLL_INTERVAL, TimeUnit.SECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(subscriberDelayInterval);

                }, (error) -> {
                    String message = error.body();
                    if (StringHelper.isNullOrEmpty(message)) {
                        message = error.getCause() == null ? null : error.getCause().toString();
                    }
                    Toast("API BookATrip raise error: " + message);
                }));
        });

        //b. pull driver positions (interval)
        subscriberDelayInterval = mapGUI.CreateIntervalSubscriber(header, mMapView, btnBook, s -> {
            // clear session when trip is ended
            settings.sessionMap(null);
            // release interval
            ClearMemory();
            // redirect to History to evaluate
            startActivity(new Intent(this, MessagesActivity.class));
        });

        // 4. Restore session map
        if (settings.sessionMap() != null)
        {
            SessionMapDto session = settings.sessionMap();
            if (!StringHelper.isNullOrEmpty(session.ToLat)) {
                // load text into AutoComplete
                autoCompleteDestination.setText(session.ToAddress);
                // load map
                LoadMapWhenConfirmDestination(session.ToLat, session.ToLng, session.ToAddress);
            }

            // loop driver position when Guest booked
            if (!StringHelper.isNullOrEmpty(session.OrderId))
            {
                Flowable.interval(DELAY_TIME, POLL_INTERVAL, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(subscriberDelayInterval);
            }
        }
    }

    private void LoadMapWhenConfirmDestination(String _toLat, String _toLng, String _toAddress) {
        toLat = _toLat;
        toLng = _toLng;
        toAddress = _toAddress;
        toCoordinate = toLat + "," + toLng;
        fromCoordinate = fromLat + "," + fromLng;

        mapGUI.GetDistanceAndAmount(header, fromCoordinate, toCoordinate, distanceAndAmount -> {
            // display distance & amount
            TextView lblDistance = findViewById(R.id.lblDistance);
            TextView lblAmount = findViewById(R.id.lblAmount);
            mapGUI.DisplayDistanceAndAmount(distanceAndAmount, lblDistance, lblAmount);
            // set route path
            mapGUI.DrawLineOnMap(mMapView, fromCoordinate, toCoordinate);
        });
    }

    private void ClearMemory() {
        mMapView = null;
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
        if(mMapView != null) mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mMapView != null) mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mMapView != null) mMapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMapView != null) mMapView.onDestroy();

        // release: Map + Observable
        ClearMemory();

        TimerHelper.Delay(3000, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        ClearMemory();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if(mMapView != null) mMapView.onLowMemory();
    }
}
