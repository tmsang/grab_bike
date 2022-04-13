package com.intec.grab.bike.guest_map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.histories.MessageShared;
import com.intec.grab.bike.histories.MessageStatus;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.api.SSLSettings;
import com.intec.grab.bike.utils.base.SETTING;
import com.intec.grab.bike.utils.helper.MyStringCallback;
import com.intec.grab.bike.utils.helper.StringHelper;
import com.intec.grab.bike.utils.log.Log;
import com.microsoft.maps.GPSMapLocationProvider;
import com.microsoft.maps.Geopath;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.Geoposition;
import com.microsoft.maps.MapElement;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapImage;
import com.microsoft.maps.MapLayer;
import com.microsoft.maps.MapPolyline;
import com.microsoft.maps.MapUserLocation;
import com.microsoft.maps.MapUserLocationTrackingState;
import com.microsoft.maps.MapView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.DisposableSubscriber;

public class GuestMapGUI {
    Context context;
    SETTING settings;
    SSLSettings sslSettings;

    // property: suggestion
    String[] suggestions;
    public String[] getSuggestions() { return suggestions; }
    public void setSuggestions(String[] suggestions) { this.suggestions = suggestions; }

    // property: distance & amount
    double distance = 0.0;
    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    double amount = 0.0;
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }


    public GuestMapGUI(Context context, SETTING settings, SSLSettings sslSettings) {
        this.context = context;
        this.settings = settings;
        this.sslSettings = sslSettings;
    }

    public void AttachPinOnMap(
            MapView mapView,
            String title,
            double lat,
            double lng,
            int r_image)
    {
        if (mapView == null || mapView.getLayers() == null) return;

        // remove old Pin === title (title is Phone number)
        for (MapLayer layer: mapView.getLayers()) {
            MapElementLayer _layer = (MapElementLayer)layer;
            for (MapElement element: _layer.getElements()) {
                if (element instanceof MapIcon) {
                    MapIcon icon = (MapIcon)element;
                    String _title = icon.getTitle();

                    if (_title.equals(title)) {
                        _layer.getElements().remove(element);
                        break;
                    }
                }
            }
        }

        // create new Pin
        Geopoint location = new Geopoint(lat, lng);             // your pin lat-long coordinates
        Bitmap pinBitmap = BitmapFactory
                .decodeResource(this.context.getResources(),    // your pin graphic (optional)
                        r_image);
        MapIcon pushpin = new MapIcon();
        pushpin.setLocation(location);
        pushpin.setTitle(title);
        pushpin.setImage(new MapImage(pinBitmap));

        // add new Pin into Map
        MapElementLayer mPinLayer = new MapElementLayer();
        mPinLayer.getElements().add(pushpin);
        mapView.getLayers().add(mPinLayer);
    }

    public DisposableSubscriber<Long> CreateIntervalSubscriber(
            Map<String, String> header,
            MapView mapView,
            Button btnBook,
            MyStringCallback callbackAfterEnd)
    {
        // Set subscriber
        return new DisposableSubscriber<Long>() {
            @Override
            public void onNext(Long aLong) {
                Log.i("------ Interval: Prepare (push notification) ------");

                SharedService.GuestMapApi(Constants.API_NET, sslSettings)
                    .IntervalGets(header,
                                    settings.currentLat(),
                                    settings.currentLng(),
                                    settings.sessionMap().OrderId)
                    .enqueue(Callback.call((rs) -> {
                        Log.i("Driver Position has been collected");

                        // get status
                        settings.sessionMap_SetStatus(rs.Status);

                        String messageStatus = MessageShared.RenderEnumFromOrderStatus(rs.Status);
                        btnBook.setText(Html.fromHtml(messageStatus));
                        btnBook.setOnClickListener(null);
                        btnBook.setOnTouchListener(null);

                        if (rs.Status.equals(MessageStatus.END) ||
                                rs.Status.equals(MessageStatus.CANCEL_BY_ADMIN) ||
                                rs.Status.equals(MessageStatus.CANCEL_BY_SYSTEM) ||
                                rs.Status.equals(MessageStatus.CANCEL_BY_USER) ||
                                rs.Status.equals(MessageStatus.CANCEL_BY_DRIVER)
                        )
                        {
                            if (callbackAfterEnd != null) callbackAfterEnd.execute("");
                            return;
                        }

                        // list of driver position
                        for (DriverPositionDto obj: rs.Positions) {
                            // attach PIN on Map
                            AttachPinOnMap(
                                    mapView,
                                    obj.Phone,
                                    obj.Lat,
                                    obj.Lng,
                                    R.drawable.ic_automobile_32);
                        }
                    }, (err) -> {
                        String msg = err.getCause() != null ? err.getCause().toString() : err.body().toString();
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        Log.i(msg);
                    }));
            }

            @Override
            public void onError(Throwable t) {
                Log.i("------ Interval: Error " + t.getMessage() + " ------");
            }

            @Override
            public void onComplete() {
                Log.i("------ Interval: Complete ------");
            }
        };

    }

    public void DrawLineOnMap(
            MapView mapView,
            String address1,
            String address2)
    {
        GuestBingMapApi.instance.drivingRoutePath(context, address1, address2, (result) -> {
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

    public void AutoComplete_InitData (
            AutoCompleteTextView autoCompleteItem,
            ArrayAdapter adapter)
    {
        autoCompleteItem.setAdapter(adapter);
        autoCompleteItem.setThreshold(1);
    }

    public void AutoComplete_ChangeData (
            AutoCompleteTextView autoCompleteItem,
            ArrayAdapter adapter)
    {
        autoCompleteItem.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void AutoComplete_OnChangeText(
            AutoCompleteTextView autoCompleteItem,
            MyStringCallback callback)
    {
        autoCompleteItem.addTextChangedListener(new TextWatcher() {
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
                callback.execute(s.toString());
            }
        });
    }

    public void AutoComplete_OnItemClick(
            AutoCompleteTextView autoCompleteItem,
            MyStringCallback callback)
    {
        autoCompleteItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // calculate Distance
                String address = getSuggestions()[position];

                // analyze address (no need to synchonize)
                GuestBingMapApi.instance.getLocationByAddress(context, address, (addressResult) -> {
                    callback.execute(addressResult + "_" + address);
                });
            }
        });
    }

    public void AutoComplete_CreateSuggestionListener(
            PublishSubject<String> publisher,
            MyStringCallback callback)
    {
        publisher
                .debounce(1000, TimeUnit.MILLISECONDS)
                .filter(text -> !text.isEmpty())
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(key -> {
                    Log.i("GuestMapActivity Key: " + key);
                    GuestBingMapApi.instance.suggestions(
                            context, key, settings.currentLat(), settings.currentLng(), (result) -> {
                                callback.execute(result);
                            });
                });
    }


    public void RequestGPSBingMap(
            MapView mapView,
            MyStringCallback callback)
    {
        MapUserLocation userLocation = mapView.getUserLocation();
        // show dialog to request permission
        MapUserLocationTrackingState userLocationTrackingState = userLocation.startTracking(
                new GPSMapLocationProvider.Builder(context).build());
        if (userLocationTrackingState == MapUserLocationTrackingState.PERMISSION_DENIED)
        {
            // request for user location permissions and then call startTracking again
        } else if (userLocationTrackingState == MapUserLocationTrackingState.READY)
        {
            // handle the case where location tracking was successfully started
            callback.execute("");
        } else if (userLocationTrackingState == MapUserLocationTrackingState.DISABLED)
        {
            // handle the case where all location providers were disabled
        }
    }

    public void GetDistanceAndAmount(
            Map<String, String> header,
            String fromCoordinate,
            String toCoordinate,
            MyStringCallback callback)
    {
        GuestBingMapApi.instance.distanceCalculation(context, fromCoordinate, toCoordinate, (distanceResult) -> {
            String[] itms = distanceResult.split("@");
            double distance = Math.round(Double.valueOf(itms[0]));     // km
            double duration = Math.round(Double.valueOf(itms[1]));     // second

            SharedService.GuestMapApi(Constants.API_NET, sslSettings)
                .GetPrice(header)
                .enqueue(Callback.call(price -> {
                    if (StringHelper.isNullOrEmpty(price)) {
                        Log.i("Price is null or empty");
                        return;
                    }
                    Double amount = Double.valueOf(distance) * Double.valueOf(price);

                    callback.execute(distance + "@" + amount);
                }, (error) -> {
                    Log.i("GetPrice: " + error.body());
                }));
        });
    }

    public void DisplayDistanceAndAmount(
            String distanceAndAmount,
            TextView lblDistance,
            TextView lblAmount)
    {
        String[] itms = distanceAndAmount.split("@");
        double distance = Double.valueOf(itms[0]);
        double amount = Double.valueOf(itms[1]);

        this.setDistance(distance);
        this.setAmount(amount);

        // set distance & amount to UI
        DecimalFormat F = new DecimalFormat("#,###,###,###");
        String _distance = "<span style=\"color: #ffffff\"><b>Distance: </b></span><span style=\"color: #00ffff\"><b>" + F.format(Math.round(distance)) + " (km)</b></span>";
        String _amount = "<span style=\"color: #ffffff\"><b>Amount: </b></span><span style=\"color: #00ffff\"><b>" + F.format(Math.round(amount)) + " (vnd)</b></span>";

        lblDistance.setText(Html.fromHtml(_distance));
        lblAmount.setText(Html.fromHtml(_amount));
    }
}
