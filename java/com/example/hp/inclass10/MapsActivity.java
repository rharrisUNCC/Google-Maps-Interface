/*
In Class Assignment #10
MapsActivity.java
Ryan Harris
 */
package com.example.hp.inclass10;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;


import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<Point> result = new ArrayList<>();
    JSONArray points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //String json = IOUtils.toString(connection.getInputStream(), "UTF-8");
        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("trip",
                        "raw", getPackageName()));
        String json;
        StringBuilder contentBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(ins));
        try {
            String readText;
            while ((readText = br.readLine()) != null)
            {
                Log.d("demo", readText);
                contentBuilder.append(readText).append("\n");
            }

            json = contentBuilder.toString();
            Log.d("array", "JSON: "+json);

            JSONObject root = new JSONObject(json);
            points = root.getJSONArray("points");
            for (int j = 0; j < points.length(); j++) {
                JSONObject pointJSON = points.getJSONObject(j);

                Point pointObjects = new Point();
                pointObjects.setLatitude(pointJSON.getDouble("latitude"));
                pointObjects.setLongitude(pointJSON.getDouble("longitude"));

                result.add(pointObjects);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        Log.d("array", "ArrayList Length: "+result.size());
        Log.d("array", "ArrayList: [");

        for(Point point : result){
            Log.d("array", "lat: "+point.getLatitude()+", long: "+point.getLongitude()+"\r");
        }
        Log.d ("array", "]");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(38, -120.0);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        PolylineOptions polylineOptions = new PolylineOptions();

        mMap.addMarker(new MarkerOptions().position(new LatLng(result.get(0).getLatitude(),result.get(0).getLongitude())).title("Start"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(result.get(result.size()-1).getLatitude(), result.get(result.size()-1).getLongitude())).title("Finish"));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for(Point point: result){
            polylineOptions.add(new LatLng(point.getLatitude(), point.getLongitude()));
            builder.include(new LatLng(point.getLatitude(), point.getLongitude()));
        }

        LatLngBounds bound = builder.build();

        polylineOptions.width(5);
        polylineOptions.color(Color.GREEN);

        Polyline polyline = mMap.addPolyline(polylineOptions);

        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bound, 15);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(cameraUpdate);
            }
        });
    }
}
