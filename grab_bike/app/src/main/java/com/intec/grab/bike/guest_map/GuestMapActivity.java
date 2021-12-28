package com.intec.grab.bike.guest_map;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.intec.grab.bike.BuildConfig;
import com.intec.grab.bike.R;
import com.intec.grab.bike.utils.helper.BaseActivity;

import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapView;

public class GuestMapActivity extends BaseActivity {
    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_map);

        mMapView = new MapView(this, MapRenderMode.VECTOR);  // or use MapRenderMode.RASTER for 2D map
        mMapView.setCredentialsKey("AuZD1lfJajlhr_Cx6GVG9uR4jzS5Y-PF3EWWGrM0SgdGBUh_8D3fvER4D-Xxco2r");     //BuildConfig.CREDENTIALS_KEY
        ((FrameLayout)findViewById(R.id.map_view)).addView(mMapView);
        mMapView.onCreate(savedInstanceState);
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
