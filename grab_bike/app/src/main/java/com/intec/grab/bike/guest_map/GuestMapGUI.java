package com.intec.grab.bike.guest_map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.intec.grab.bike.utils.log.Log;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapElement;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapImage;
import com.microsoft.maps.MapLayer;
import com.microsoft.maps.MapView;

public class GuestMapGUI {
    Context context;

    public GuestMapGUI(Context context) {
        this.context = context;
    }

    public void AttachPinOnMap(MapView mapView, String title, double lat, double lng, int r_image) {
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
}
