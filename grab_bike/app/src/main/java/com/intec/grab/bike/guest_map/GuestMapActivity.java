package com.intec.grab.bike.guest_map;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.utils.helper.BaseActivity;

import com.intec.grab.bike.utils.helper.GPSTracker;
import com.microsoft.maps.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class GuestMapActivity extends BaseActivity {
    private MapView mMapView;


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

        /*
        // Show Bing-Map
        mMapView = new MapView(this, MapRenderMode.VECTOR);  // or use MapRenderMode.RASTER for 2D map
        mMapView.setCredentialsKey(Constants.BING_MAP_KEY);         // BuildConfig.CREDENTIALS_KEY
        ((FrameLayout)findViewById(R.id.map_view)).addView(mMapView);
        mMapView.onCreate(savedInstanceState);
        */

        // Request location permission
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted)
                            {
                                // Precise location access granted.
                                GPSTracker gps = new GPSTracker(this);
                                if (gps.canGetLocation()) {
                                    double lat = gps.getLatitude();
                                    double lng = gps.getLongitude();
                                    settings.currentLat(lat + "");
                                    settings.currentLng(lng + "");
                                }
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                Toast("Only approximate location access granted");
                            } else {
                                // No location access granted.
                                Toast("No location access granted");
                            }
                        }
                );

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

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

        autoCompleteDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // calculate Distance

                // calculate Amount

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                    retrieveData(key);
                });
    }

    private void retrieveData(String s)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://dev.virtualearth.net/REST/v1/Autosuggest" +
                "?query=" + s +
                "&userLocation=" + settings.currentLat() + "," + settings.currentLng() +
                "&includeEntityTypes=Address" +
                "&key=" + Constants.BING_MAP_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            response -> {
                try {
                    JSONObject jsnObject = new JSONObject(response);
                    JSONArray jsonArray = jsnObject.getJSONArray("resourceSets");
                    ArrayList<String> items = new ArrayList<String>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject itemI = jsonArray.getJSONObject(i);

                        JSONArray jsonResources = itemI.getJSONArray("resources");
                        for (int j = 0; j < jsonResources.length(); j++) {
                            JSONObject itemJ = jsonResources.getJSONObject(j);

                            JSONArray jsonValue = itemJ.getJSONArray("value");
                            for (int k = 0; k < jsonValue.length(); k++) {
                                JSONObject itemK = jsonValue.getJSONObject(k);

                                JSONObject jsonAddress = itemK.getJSONObject("address");
                                String formattedAddress = jsonAddress.getString("formattedAddress");
                                items.add(formattedAddress);
                            }
                        }
                    }

                    String[] itemArrays = new String[items.size()];
                    itemArrays = items.toArray(itemArrays);

                    destinations = itemArrays;
                    destinationAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, destinations);
                    autoCompleteDestination.setAdapter(destinationAdapter);
                    destinationAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                 }

            },
            error -> {
                Log.i("GuestMapActivity", error.getMessage());
            }
        );
        queue.add(stringRequest);
    }



/*
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

 */
}
