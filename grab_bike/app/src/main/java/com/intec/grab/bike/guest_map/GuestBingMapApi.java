package com.intec.grab.bike.guest_map;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.utils.helper.MyStringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GuestBingMapApi
{
    public static GuestBingMapApi instance = new GuestBingMapApi();

    public GuestBingMapApi() {
    }

    public void getDestinations(
            Context context,
            String key,
            String currentLat,
            String currentLng,
            MyStringCallback callback)
    {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://dev.virtualearth.net/REST/v1/Autosuggest" +
                "?query=" + key +
                "&userLocation=" + currentLat + "," + currentLng +
                "&includeEntityTypes=Address" +
                "&key=" + Constants.BING_MAP_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsnObject = new JSONObject(response);
                        JSONArray jsonArray = jsnObject.getJSONArray("resourceSets");
                        String result = "";

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
                                    result += formattedAddress + "@";
                                }
                            }
                        }

                        if (result.length() > 0) {
                            result = result.substring(0, result.length() - 1);
                        }

                        callback.execute(result);

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

    public void distanceCalculation(
            Context context,
            String location1,
            String location2,
            MyStringCallback callback
    ) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://dev.virtualearth.net/REST/V1/Routes" +
                "?wp.0=" + location1 +
                "&wp.1=" + location2 +
                "&key=" + Constants.BING_MAP_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsnObject = new JSONObject(response);
                        JSONArray jsonArray = jsnObject.getJSONArray("resourceSets");
                        String result = "";

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject itemI = jsonArray.getJSONObject(i);

                            JSONArray jsonResources = itemI.getJSONArray("resources");
                            for (int j = 0; j < jsonResources.length(); j++) {
                                JSONObject itemJ = jsonResources.getJSONObject(j);

                                double travelDistance = itemJ.getDouble("travelDistance");
                                double travelDuration = itemJ.getDouble("travelDuration");
                                double travelDurationTraffic = itemJ.getDouble("travelDurationTraffic");

                                result = travelDistance + "@" + travelDuration + "@" + travelDurationTraffic;
                                callback.execute(result);
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        Log.i("GuestMapApi", "Cannot calculate distance - as cannot reach by driving");
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.i("GuestMapActivity", error.getMessage());
                }
        );
        queue.add(stringRequest);
    }

    public void getAddressByLocation(
            Context context,
            String lat,
            String lng,
            MyStringCallback callback
    ) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://dev.virtualearth.net/REST/v1/Locations" +
                "/" + lat + "," + lng +
                "?o=json" +
                "&key=" + Constants.BING_MAP_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsnObject = new JSONObject(response);
                        JSONArray jsonArray = jsnObject.getJSONArray("resourceSets");
                        String result = "";

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject itemI = jsonArray.getJSONObject(i);

                            JSONArray jsonResources = itemI.getJSONArray("resources");
                            for (int j = 0; j < jsonResources.length(); j++) {
                                JSONObject itemJ = jsonResources.getJSONObject(j);

                                JSONObject address = itemJ.getJSONObject("address");
                                String formattedAddress = address.getString("formattedAddress");

                                result = formattedAddress;
                                callback.execute(result);
                                return;
                            }
                        }
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

    public void getLocationByAddress(
            Context context,
            String address,
            MyStringCallback callback
    ) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://dev.virtualearth.net/REST/v1/Locations" +
                "?q=" + address +
                "&o=json" +
                "&key=" + Constants.BING_MAP_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsnObject = new JSONObject(response);
                        JSONArray jsonArray = jsnObject.getJSONArray("resourceSets");
                        String result = "";

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject itemI = jsonArray.getJSONObject(i);

                            JSONArray jsonResources = itemI.getJSONArray("resources");
                            for (int j = 0; j < jsonResources.length(); j++) {
                                JSONObject itemJ = jsonResources.getJSONObject(j);

                                JSONObject point = itemJ.getJSONObject("point");
                                JSONArray coordinates = point.getJSONArray("coordinates");

                                result = coordinates.getDouble(0) + "_" + coordinates.getDouble(1);
                                callback.execute(result);
                                return;
                            }
                        }
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

    public void drivingRoutePath(
            Context context,
            String address1,
            String address2,
            MyStringCallback callback
    ) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://dev.virtualearth.net/REST/V1/Routes/Driving" +
                "?wp.0=" + address1 +
                "&wp.1=" + address2 +
                "&optmz=distance" +
                "&routeAttributes=routePath" +
                "&key=" + Constants.BING_MAP_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsnObject = new JSONObject(response);
                        JSONArray jsonArray = jsnObject.getJSONArray("resourceSets");
                        String result = "";

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject itemI = jsonArray.getJSONObject(i);

                            JSONArray jsonResources = itemI.getJSONArray("resources");
                            for (int j = 0; j < jsonResources.length(); j++) {
                                JSONObject itemJ = jsonResources.getJSONObject(j);

                                JSONObject routePath = itemJ.getJSONObject("routePath");
                                JSONObject line = routePath.getJSONObject("line");
                                JSONArray coordinates = line.getJSONArray("coordinates");

                                for (int k = 0; k < coordinates.length(); k++) {
                                    JSONArray pointItem = coordinates.getJSONArray(k);
                                    double lat = pointItem.getDouble(0);
                                    double lng = pointItem.getDouble(1);
                                    result += lat + "_" + lng + "@";
                                }

                                if (result.length() > 0) result = result.substring(0, result.length() - 1);
                                callback.execute(result);
                                return;
                            }
                        }
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

    
}
