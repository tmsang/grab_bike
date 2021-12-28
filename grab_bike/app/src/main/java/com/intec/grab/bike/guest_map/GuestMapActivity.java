package com.intec.grab.bike.guest_map;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.intec.grab.bike.BuildConfig;
import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.utils.helper.BaseActivity;

import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GuestMapActivity extends BaseActivity {
    private MapView mMapView;

    private String[] destinations = {"Vietnam","England","Canada", "France","Australia"};
    private  AutoCompleteTextView autoCompleteDestination;
    private ArrayAdapter adapterDestinations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_map);

        autoCompleteDestination =(AutoCompleteTextView)findViewById(R.id.autoCompleteDestination);
        ArrayAdapter adapterDestinations
                = new ArrayAdapter(this,android.R.layout.simple_list_item_1, destinations);
        autoCompleteDestination.setAdapter(adapterDestinations);
        autoCompleteDestination.setThreshold(1);

        /*
        mMapView = new MapView(this, MapRenderMode.VECTOR);  // or use MapRenderMode.RASTER for 2D map
        mMapView.setCredentialsKey(Constants.BING_MAP_KEY);         // BuildConfig.CREDENTIALS_KEY
        ((FrameLayout)findViewById(R.id.map_view)).addView(mMapView);
        mMapView.onCreate(savedInstanceState);
*/
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
                retrieveData(s.toString());
            }
        });
    }

    private void retrieveData(String s)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://dev.virtualearth.net/REST/v1/Autosuggest" +
                "?query=El%20bur" +
                "&userLocation=47.668697,-122.376373,5" +
                "&includeEntityTypes=Address" +
                "&key=" + Constants.BING_MAP_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            response -> {
                try {
                    JSONObject jsnObject = new JSONObject(response);
                    JSONArray jsonArray = jsnObject.getJSONArray("resourceSets");
                    String[] items = {};
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

                            }
                        }
                    }
                    destinations = items;
                    adapterDestinations.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            },
            error -> {

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
