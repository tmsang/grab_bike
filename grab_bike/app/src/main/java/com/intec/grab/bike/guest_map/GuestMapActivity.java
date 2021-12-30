package com.intec.grab.bike.guest_map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

import com.intec.grab.bike.MainActivity;
import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.login.LoginActivity;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.helper.BaseActivity;

import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapView;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapImage;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class GuestMapActivity extends BaseActivity {
    private MapView mMapView;

    private MapElementLayer mPinLayer;

    private String[] destinations = {"Vietnam","England","Canada", "France","Australia"};
    private  AutoCompleteTextView autoCompleteDestination;
    private ArrayAdapter destinationAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_map);
        Initialization(this);

        autoCompleteDestination =(AutoCompleteTextView)findViewById(R.id.autoCompleteDestination);
        destinationAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, destinations);
        autoCompleteDestination.setAdapter(destinationAdapter);
        autoCompleteDestination.setThreshold(1);

        // Show Bing-Map
        mMapView = new MapView(this, MapRenderMode.VECTOR);  // or use MapRenderMode.RASTER for 2D map
        mMapView.setCredentialsKey(Constants.BING_MAP_KEY);         // BuildConfig.CREDENTIALS_KEY
        ((FrameLayout)findViewById(R.id.map_view)).addView(mMapView);
        mMapView.onCreate(savedInstanceState);

        // attach PIN
        Geopoint location = new Geopoint(
                Double.valueOf(settings.currentLat()),
                Double.valueOf(settings.currentLng()));             // your pin lat-long coordinates
        String title = "Current";                                   // title to be shown next to the pin
        Bitmap pinBitmap = BitmapFactory
                .decodeResource(this.getResources(),                // your pin graphic (optional)
                R.drawable.ic_current_position);
        MapIcon pushpin = new MapIcon();
        pushpin.setLocation(location);
        pushpin.setTitle(title);
        pushpin.setImage(new MapImage(pinBitmap));

        mPinLayer = new MapElementLayer();
        mPinLayer.getElements().add(pushpin);
        mMapView.getLayers().add(mPinLayer);

        // Request location permission
        this.RequestPermissionLocation((result) -> {
            String[] items = result.split("@");
            String lat = items[0];
            String lng = items[1];
            settings.currentLat(lat);
            settings.currentLng(lng);
            GuestBingMapApi.instance.getAddressByLocation(this, lat + "", lng + "", (address) -> {
                settings.currentAddress(address);
            });
        });

        // AutoSuggest
        final PublishSubject<String> publisher = PublishSubject.create();
        autoCompleteDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //retrieveData(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
                //this will call your method every time the user stops typing, if you want to call it for each letter, call it in onTextChanged
                publisher.onNext(s.toString());
            }
        });

        autoCompleteDestination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // calculate Distance
                String address1 = settings.currentAddress();
                String address2 = destinations[position];
                GuestBingMapApi.instance.distanceCalculation(GuestMapActivity.this, address1, address2, (result) -> {
                    String[] items = result.split("@");

                    double distance = Math.round(Double.valueOf(items[0]));     // km
                    double duration = Math.round(Double.valueOf(items[1]));     // second

                    Map<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/x-www-form-urlencoded");
                    map.put("Authorization", settings.jwtToken());

                    SharedService.GuestMapApi(Constants.API_NET, sslSettings)
                            .GetPrice(map)
                            .enqueue(Callback.callInUI(GuestMapActivity.this, (price) -> {
                                if (IsNullOrEmpty(price, "Price")) return;

                                DecimalFormat F = new DecimalFormat("#,###,###,###");
                                Double amount = Double.valueOf(distance) * Double.valueOf(price);

                                SetTextView(R.id.lblDistance, "<span style=\"color: #ffffff\"><b>Distance: </b></span><span style=\"color: #00ffff\"><b>" + F.format(Math.round(distance)) + " (km)</b></span>");
                                SetTextView(R.id.lblAmount, "<span style=\"color: #ffffff\"><b>Amount: </b></span><span style=\"color: #00ffff\"><b>" + F.format(Math.round(amount)) + " (vnd)</b></span>");

                            }, (error) -> {
                                Toast("API cannot reach", error.body());
                            }));
                });
                // calculate Amount
            }
        });



        publisher
                .debounce(1000, TimeUnit.MILLISECONDS)
                .filter(text -> !text.isEmpty())
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(key -> {
                    Log.i("GuestMapActivity", "key: " + key);
                    GuestBingMapApi.instance.getDestinations(this, key,
                            settings.currentLat(), settings.currentLng(), (result) -> {
                        String[] itemArrays = result.split("@");
                        destinations = itemArrays;
                        destinationAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, destinations);
                        autoCompleteDestination.setAdapter(destinationAdapter);
                        destinationAdapter.notifyDataSetChanged();
                    });
                });
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
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}
