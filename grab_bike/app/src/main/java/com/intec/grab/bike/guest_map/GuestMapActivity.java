package com.intec.grab.bike.guest_map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.base.BaseActivity;

import com.intec.grab.bike.utils.log.Log;
import com.microsoft.maps.GPSMapLocationProvider;
import com.microsoft.maps.Geopath;
import com.microsoft.maps.Geoposition;
import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapElement;
import com.microsoft.maps.MapLayer;
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

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.DisposableSubscriber;

public class GuestMapActivity extends BaseActivity {
    private int POLL_INTERVAL = 10;
    private int DELAY_TIME = 2;
    private DisposableSubscriber<Long> subscriberDelayInterval;

    private MapView mMapView;
    private MapElementLayer mPinLayer;
    private MapUserLocation userLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private GuestMapGUI mapGUI;

    private String fromLat = "", fromLng = "", fromAddress = "", fromCoordinate = "";
    private String toLat = "", toLng = "", toAddress = "", toCoordinate = "";

    private String[] destinations = {"Vietnam","England","Canada", "France","Australia"};
    private AutoCompleteTextView autoCompleteDestination;
    private ArrayAdapter destinationAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_map);
        Initialization(this);

        mapGUI = new GuestMapGUI(this);
        if (subscriberDelayInterval != null && !subscriberDelayInterval.isDisposed()) {
            subscriberDelayInterval.dispose();
        }

        // Get the last known location of users
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // autocomplete BingMap to input address
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

        // show dialog to request permission
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
                fromCoordinate = fromLat + "," + fromLng;

                Geopoint centerPoint = new Geopoint(Double.valueOf(fromLat), Double.valueOf(fromLng));
                mMapView.setScene(
                        MapScene.createFromLocationAndZoomLevel(centerPoint, 16),
                        MapAnimationKind.NONE);

                // attach PIN on Map
                mapGUI.AttachPinOnMap(
                        mMapView,
                        "Me",
                        Double.valueOf(fromLat),
                        Double.valueOf(fromLng),
                        R.drawable.ic_current_position);
            });

            // Push current guest position
            Map<String, String> pushPositionParam = new HashMap<>();
            pushPositionParam.put("Content-Type", "application/x-www-form-urlencoded");
            pushPositionParam.put("Authorization", settings.jwtToken());

            SharedService.GuestMapApi(Constants.API_NET, sslSettings)
                    .PushPosition(pushPositionParam, fromLat, fromLng)
                    .enqueue(Callback.callInUI(GuestMapActivity.this, (res) -> {
                        // TODO: nothing to do here
                        Log.i("Guest position has been pushed - successfully");
                    }, (error) -> {
                        HandleException("Push Position", error.body());
                    }));

            // Set subscriber
            Map<String, String> getPositionMap = new HashMap<>();
            getPositionMap.put("Content-Type", "application/x-www-form-urlencoded");
            getPositionMap.put("Authorization", settings.jwtToken());

            subscriberDelayInterval = new DisposableSubscriber<Long>() {
                @Override
                public void onNext(Long aLong) {
                    Log.i("---");
                    SharedService.GuestMapApi(Constants.API_NET, sslSettings)
                        .GetDriverPositions(getPositionMap, fromLat, fromLng)
                        .enqueue(Callback.callInUI(GuestMapActivity.this, (rs) -> {
                            Log.i("Driver Position has been collected");

                            // list of driver position
                            for (DriverPositionDto obj: rs) {
                                // attach PIN on Map
                                mapGUI.AttachPinOnMap(
                                        mMapView,
                                        obj.Phone,
                                        obj.Lat,
                                        obj.Lng,
                                        R.drawable.ic_automobile_32);
                            }

                            // show pin in it
                        }, (err) -> {
                            Toast("API DriverPositions cannot reach", err.body());
                        }));
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onComplete() {
                    Log.i("....");
                }
            };
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
                GuestBingMapApi.instance.getLocationByAddress(GuestMapActivity.this, address2, (addressResult) -> {
                    String[] items = addressResult.split("_");
                    toLat = items[0];
                    toLng = items[1];
                    toAddress = address2;
                    toCoordinate = toLat + "," + toLng;

                    GuestBingMapApi.instance.distanceCalculation(GuestMapActivity.this, address1, address2, (distanceResult) -> {
                        String[] itms = distanceResult.split("@");

                        double distance = Math.round(Double.valueOf(itms[0]));     // km
                        double duration = Math.round(Double.valueOf(itms[1]));     // second

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
                                    //drawLineOnMap(mMapView, address1, address2);
                                    drawLineOnMap(mMapView, fromCoordinate, toCoordinate);

                                }, (error) -> {
                                    HandleException("GetPrice", error.body());
                                }));
                    });
                    // calculate Amount
                });
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
                    Toast("Your Book is success. Please wait Driver response");

                    // -----------------------------
                    // view driver position
                    Flowable.interval(DELAY_TIME, POLL_INTERVAL, TimeUnit.SECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(subscriberDelayInterval);
                    // -----------------------------

                }, (error) -> {
                    Toast("API BookATrip cannot reach", error.body());
                }));
        });

        publisher
                .debounce(1000, TimeUnit.MILLISECONDS)
                .filter(text -> !text.isEmpty())
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(key -> {
                    Log.i("GuestMapActivity Key: " + key);
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
            // clear old line route
            for (MapLayer layer: mapView.getLayers()) {
                MapElementLayer _layer = (MapElementLayer)layer;
                for (MapElement element: _layer.getElements()) {
                    if (element instanceof MapPolyline) {
                        _layer.getElements().remove(element);
                    }
                }
            }

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
