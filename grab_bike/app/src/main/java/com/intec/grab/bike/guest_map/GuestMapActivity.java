package com.intec.grab.bike.guest_map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import com.microsoft.maps.AltitudeReferenceSystem;
import com.microsoft.maps.GPSMapLocationProvider;
import com.microsoft.maps.Geopath;
import com.microsoft.maps.Geoposition;
import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapPolyline;
import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapScene;
import com.microsoft.maps.MapUserLocation;
import com.microsoft.maps.MapUserLocationTrackingState;
import com.microsoft.maps.MapView;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapImage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class GuestMapActivity extends BaseActivity {
    private MapView mMapView;
    private MapElementLayer mPinLayer;
    private MapUserLocation userLocation;

    private String fromLat = "", fromLng = "", fromAddress = "";
    private String toLat = "", toLng = "", toAddress = "";

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
        userLocation = mMapView.getUserLocation();

        MapUserLocationTrackingState userLocationTrackingState = userLocation.startTracking(
                new GPSMapLocationProvider.Builder(getApplicationContext()).build());
        if (userLocationTrackingState == MapUserLocationTrackingState.PERMISSION_DENIED)
        {
            // request for user location permissions and then call startTracking again
        } else if (userLocationTrackingState == MapUserLocationTrackingState.READY)
        {
            // handle the case where location tracking was successfully started
        } else if (userLocationTrackingState == MapUserLocationTrackingState.DISABLED)
        {
            // handle the case where all location providers were disabled
        }

        // Request location permission
        this.RequestPermissionLocation((result) -> {
            String[] items = result.split("@");
            fromLat = items[0];
            fromLng = items[1];
            GuestBingMapApi.instance.getAddressByLocation(this, fromLat + "", fromLng + "", (address) -> {
                fromAddress = address;

                Geopoint centerPoint = new Geopoint(Double.valueOf(fromLat), Double.valueOf(fromLng));
                mMapView.setScene(
                        MapScene.createFromLocationAndZoomLevel(centerPoint, 16),
                        MapAnimationKind.NONE);

                // attach PIN
                Geopoint location = new Geopoint(
                        Double.valueOf(fromLat),
                        Double.valueOf(fromLng));                           // your pin lat-long coordinates
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
                String address1 = fromAddress;
                String address2 = destinations[position];

                // analyze address (no need to synchonize)
                GuestBingMapApi.instance.getLocationByAddress(GuestMapActivity.this, address2, (result) -> {
                    String[] items = result.split("_");
                    toLat = items[0];
                    toLng = items[1];
                    toAddress = address2;
                });

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

                                // set distance & amount to UI
                                SetTextView(R.id.lblDistance, "<span style=\"color: #ffffff\"><b>Distance: </b></span><span style=\"color: #00ffff\"><b>" + F.format(Math.round(distance)) + " (km)</b></span>");
                                SetTextView(R.id.lblAmount, "<span style=\"color: #ffffff\"><b>Amount: </b></span><span style=\"color: #00ffff\"><b>" + F.format(Math.round(amount)) + " (vnd)</b></span>");

                                // set route path
                                drawLineOnMap(mMapView, address1, address2);

                            }, (error) -> {
                                Toast("API cannot reach", error.body());
                            }));
                });
                // calculate Amount
            }
        });

        this.ButtonClickEvent(R.id.btnBook, (btn) -> {
            if (IsNullOrEmpty(fromLat, "From Latitude")) return;
            if (IsNullOrEmpty(fromLng, "From Longitude")) return;
            if (IsNullOrEmpty(fromAddress, "From Address")) return;
            if (IsNullOrEmpty(toLat, "To Latitude")) return;
            if (IsNullOrEmpty(toLng, "To Longitude")) return;
            if (IsNullOrEmpty(toAddress, "To Address")) return;

            Map<String, String> map = new HashMap<>();
            map.put("Content-Type", "application/x-www-form-urlencoded");
            map.put("Authorization", settings.jwtToken());

            SharedService.GuestMapApi(Constants.API_NET, sslSettings)
                .BookATrip(map, fromLat, fromLng, fromAddress, toLat, toLng, toAddress)
                .enqueue(Callback.callInUI(GuestMapActivity.this, (result) -> {
                    Log.i("", "");
                }, (error) -> {
                    Toast("API cannot reach", error.body());
                }));
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
                            fromLat, fromLng, (result) -> {
                        String[] itemArrays = result.split("@");
                        destinations = itemArrays;
                        destinationAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, destinations);
                        autoCompleteDestination.setAdapter(destinationAdapter);
                        destinationAdapter.notifyDataSetChanged();
                    });
                });
    }

    private void drawLineOnMap(MapView mapView, String address1, String address2) {

        GuestBingMapApi.instance.drivingRoutePath(this, address1, address2, (result) -> {
            ArrayList<Geoposition> geopoints = new ArrayList<>();
            String[] items;
            Double lat, lng;

            String[] points = result.split("@");
            for (int i = 0; i < points.length; i++) {
                items = points[i].split("_");
                lat = Double.valueOf(items[0]);
                lng = Double.valueOf(items[1]);
                geopoints.add(new Geoposition(lat, lng));
            }

            MapPolyline mapPolyline = new MapPolyline();
            mapPolyline.setPath(new Geopath(geopoints));
            mapPolyline.setStrokeColor(Color.RED);
            mapPolyline.setStrokeWidth(5);
            mapPolyline.setStrokeDashed(true);

            // Add Polyline to a layer on the map control.
            MapElementLayer linesLayer = new MapElementLayer();
            linesLayer.setZIndex(1.0f);
            linesLayer.getElements().add(mapPolyline);
            mapView.getLayers().add(linesLayer);

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
