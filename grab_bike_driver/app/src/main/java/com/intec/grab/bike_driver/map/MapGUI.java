package com.intec.grab.bike_driver.map;

import android.content.Context;
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
import android.widget.TextView;

import com.intec.grab.bike_driver.R;
import com.intec.grab.bike_driver.configs.Constants;
import com.intec.grab.bike_driver.shared.SharedService;
import com.intec.grab.bike_driver.utils.api.Callback;
import com.intec.grab.bike_driver.utils.api.SSLSettings;
import com.intec.grab.bike_driver.utils.base.SETTING;
import com.intec.grab.bike_driver.utils.helper.MyStringCallback;
import com.intec.grab.bike_driver.utils.helper.StringHelper;
import com.intec.grab.bike_driver.utils.log.Log;
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

public class MapGUI {
    Context context;
    SETTING settings;
    SSLSettings sslSettings;

    public MapGUI(Context context, SETTING settings, SSLSettings sslSettings) {
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

    public DisposableSubscriber<Long> CreatePushDriverPositionIntervalSubscriber(
            Map<String, String> header,
            MapView mapView)
    {
        // Set subscriber
        return new DisposableSubscriber<Long>() {
            @Override
            public void onNext(Long aLong) {
                Log.i("------ Interval: Prepare (push notification) ------");
                SharedService.MapApi(Constants.API_NET, sslSettings)
                    .PushPosition(header, settings.currentLat(), settings.currentLng())
                    .enqueue(Callback.call((rs) -> {
                        Log.i("Driver Position has been pushed");
                    }, (err) -> {
                        Log.i("API DriverPositions cannot reach");
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
        BingMapApi.instance.drivingRoutePath(context, address1, address2, (result) -> {
            // clear old line route
            ClearLineRoutePath(mapView);

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

    public void ClearLineRoutePath(MapView mapView) {
        // clear old line route
        for (MapLayer layer: mapView.getLayers()) {
            MapElementLayer _layer = (MapElementLayer)layer;
            for (MapElement element: _layer.getElements()) {
                if (element instanceof MapPolyline) {
                    _layer.getElements().remove(element);
                }
            }
        }
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
}
