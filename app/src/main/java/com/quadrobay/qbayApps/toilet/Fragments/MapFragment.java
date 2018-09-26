package com.quadrobay.qbayApps.toilet.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.quadrobay.qbayApps.toilet.Activitys.AddToiletClass;
import com.quadrobay.qbayApps.toilet.Activitys.ToiletInfoClass;
import com.quadrobay.qbayApps.toilet.MainActivity;
import com.quadrobay.qbayApps.toilet.R;
import com.quadrobay.qbayApps.toilet.Singleton.AppController;
import com.quadrobay.qbayApps.toilet.Singleton.ConnectivityReceiver;
import com.quadrobay.qbayApps.toilet.Singleton.DirectionsJSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by sairaj on 16/11/17.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, LocationListener,ConnectivityReceiver.ConnectivityReceiverListener {
    Context context;
    ImageView toolbarleftlogo, toolbarrihtlogo;
    GoogleMap googleMap;
    LocationManager locationManager;
    SupportMapFragment supportMapFragment;
    Boolean continouszooming = true,internetConnectivity,bDriving,bBiCycling,bWalking,networkError = false;
    LatLng latLng;
    Button editbutton,settings,allow;
    //AdView adView;
    Button maptype,searchlocation;
    TextView addressTextview, distancetextView,internetconnction;
    RatingBar ratingBar;
    JSONArray makesArray;
    LinearLayout linearLayout,phoneRelativelayout,appRelativelayout;
    final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    IntentFilter intentFilter;
    CustomReceiver customReceiver;
    private static Tracker sTracker;
    Dialog markerDialog;
    int mMode=0;
    final int MODE_DRIVING=0;
    final int MODE_BICYCLING=1;
    final int MODE_WALKING=2;
    PolylineOptions lineOptions = null;
    Polyline toiletrouite;

    Marker commonmarker;
    Location currentLocation;
    AlertDialog dialog;
    AlertDialog.Builder dialogBuilder;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private String[] permissionsRequired = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Satellite", "Terrain" ,"Hybrid"};
    private Circle mCircle;
    private Marker mMarker;

    Button internetCheck;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        linearLayout = (LinearLayout) view.findViewById(R.id.hole_layout);

        linearLayout.setAlpha(0.1f);
        toolbarleftlogo = (ImageView) view.findViewById(R.id.toolbar_logo);
        toolbarrihtlogo = (ImageView) view.findViewById(R.id.toolbar_right_logo);
        maptype=(Button)view.findViewById(R.id.changetype);
        searchlocation=(Button)view.findViewById(R.id.searchlocation);
        //linearLayout = (LinearLayout) view.findViewById(R.id.list_layout);
//        addressTextview = (TextView) view.findViewById(R.id.address);
//        distancetextView = (TextView) view.findViewById(R.id.distance_text);
//        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
//        editbutton = (Button) view.findViewById(R.id.Edit_logo);
//        editbutton.setOnClickListener(this);
//        linearLayout.setOnClickListener(this);
        toolbarrihtlogo.setOnClickListener(this);
        maptype.setOnClickListener(this);
        searchlocation.setOnClickListener(this);

        toolbarleftlogo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // TODO Auto-generated method stub
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        MainActivity.slidingMenu.toggle();
                        break;
                    case MotionEvent.ACTION_UP:
                        //set color back to default
                        break;
                }
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentFilter = new IntentFilter();
            intentFilter.addAction(CONNECTIVITY_ACTION);
            customReceiver = new CustomReceiver();
        }

        initMap();

        // Obtain the shared Tracker instance.
       AppController.getInstance().getDefaultTracker();
       return view;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
        super.onSaveInstanceState(outState);
    }

    private Boolean checkConnection() {

        boolean isConnected = ConnectivityReceiver.isConnected();
        return isConnected;
    }

    private void ShowProgressDialog() {

        dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.loading_progress_layout, null);

        internetCheck =(Button) dialogView.findViewById(R.id.internetRetry);
        internetCheck.setOnClickListener(this);


        phoneRelativelayout = (LinearLayout) dialogView.findViewById(R.id.phone_location);
        appRelativelayout = (LinearLayout) dialogView.findViewById(R.id.app_location);
        settings = (Button) dialogView.findViewById(R.id.settings_btn);
        allow = (Button) dialogView.findViewById(R.id.app_settings_btn);
        internetconnction = (TextView) dialogView.findViewById(R.id.internet_text);
        settings.setOnClickListener(this);
        allow.setOnClickListener(this);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        dialog = dialogBuilder.create();

        internetConnectivity = checkConnection();
        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;



        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {

        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {

            phoneRelativelayout.setVisibility(View.VISIBLE);
            appRelativelayout.setVisibility(View.GONE);
            internetconnction.setVisibility(View.GONE);
            internetCheck.setVisibility(View.GONE);

        }else if (!internetConnectivity){

            phoneRelativelayout.setVisibility(View.GONE);
            appRelativelayout.setVisibility(View.GONE);
            internetconnction.setVisibility(View.VISIBLE);

        }else if (ActivityCompat.checkSelfPermission(getContext(), permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED){

            phoneRelativelayout.setVisibility(View.GONE);
            appRelativelayout.setVisibility(View.VISIBLE);
            internetconnction.setVisibility(View.GONE);

        }/*else if (networkError){
            networkError = false;
        }*/else{

            locationservice();
        }

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

    }

    private void locationservice() {

        internetCheck.setVisibility(View.INVISIBLE);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
            dialog.dismiss();
            android.app.AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                builder = new android.app.AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
            }else {
                builder = new android.app.AlertDialog.Builder(getContext());
            }

            builder.setTitle("Please specify the error")
                    .setMessage("There is no location found")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            ShowProgressDialog();
                        }
                    })
                    .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }else {
            continouszooming = true;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000,1000, this);

        }

    }

    private void initMap() {

        supportMapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_right_logo:
                Intent intent = new Intent(getContext(), AddToiletClass.class);
                getContext().startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.settings_btn:
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getContext().startActivity(myIntent);
                break;
            case R.id.app_settings_btn:
                ActivityCompat.requestPermissions((Activity) getContext(), permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                break;
            case R.id.changetype:
                showMapTypeSelectorDialog();
                break;
            case R.id.searchlocation:
                searchlocationDialog();
                break;

            case R.id.internetRetry:
                Intent InIntent=new Intent(Settings.ACTION_SETTINGS);
                getContext().startActivity(InIntent);
        }

    }



    private void showMapTypeSelectorDialog() {
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        //int checkItem = googleMap.getMapType() - 1;
        int checkItem = googleMap.getMapType()-1;
        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }

    private void searchlocationDialog(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        View mView = layoutInflaterAndroid.inflate(R.layout.search_alert, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        String address = userInputDialogEditText.getText().toString();
                        if(!address.equals("")&&!address.equals(null)) {
                            searchlocationlatlong(address);
                            dialogBox.cancel();
                        }

                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    public void searchlocationlatlong(String address){
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        String result = null;
        try {
            List addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null && addressList.size() > 0) {
                Address location= (Address) addressList.get(0);
                double mylat =location.getLatitude();
                double mylng =  location.getLongitude();
                if (!String.valueOf(mylat).equals(null) && !String.valueOf(mylng).equals(null)) {
                    LatLng coordinate = new LatLng(mylat, mylng);
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    currentLocation = new Location("");
                    currentLocation.setLatitude(latLng.latitude);
                    currentLocation.setLongitude(latLng.longitude);
                    //CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(10).build();
                   // googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15), 10, null);
                    googleMap.addMarker(new MarkerOptions().position(coordinate));
                    postServerMethod();
                    continouszooming = false;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            if (continouszooming) {
                LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                currentLocation = new Location("");
                currentLocation.setLatitude(latLng.latitude);
                currentLocation.setLongitude(latLng.longitude);
                //CameraPosition cameraPosition = new CameraPosition.Builder().target(coordinate).zoom(15).build();
                //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15), 10, null);
                postServerMethod();
                //drawMarkerWithCircle(latLng);
                continouszooming = false;
                linearLayout.setAlpha(1.0f);
            }
        }
    }

   /* private void drawMarkerWithCircle(LatLng position) {
        double radiusInMeters = 500.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = googleMap.addCircle(circleOptions);
        MarkerOptions markerOptions = new MarkerOptions().position(position);
        mMarker = googleMap.addMarker(markerOptions);
    }*/

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

        Log.e("","");
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMapReady(GoogleMap Map) {
        googleMap = Map;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;

        }

        googleMap.setMyLocationEnabled(true);
        //googleMap.getMaxZoomLevel();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.setIndoorEnabled(true);

       // googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                commonmarker = marker;
                markerDialog(marker.getPosition().latitude, marker.getPosition().longitude,marker);
                //drawMarkerWithCircle(latLng);
                //clickableMarker(marker.getPosition().latitude, marker.getPosition().longitude);
                return true;
            }
        });

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void markerDialog(final double latitude, final double longitude, final Marker marker) {


        if (toiletrouite != null){
            toiletrouite.remove();
        }
        markerDialog = new Dialog(getContext(),R.style.NewDialog);
        markerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        markerDialog.setContentView(R.layout.info_window_layout);
        markerDialog.setCanceledOnTouchOutside(true);
        markerDialog.setCancelable(true);

        TextView textView = (TextView) markerDialog.findViewById(R.id.address);
        TextView textView1 = (TextView) markerDialog.findViewById(R.id.distance_text);
        RatingBar ratingBar = (RatingBar) markerDialog.findViewById(R.id.ratingBar);
        Button button = (Button) markerDialog.findViewById(R.id.cancelbutton);
        Button button1 = (Button) markerDialog.findViewById(R.id.okbutton);
        Button driving = (Button) markerDialog.findViewById(R.id.driving_btn);

        try {
            for (int index = 0; index < makesArray.length(); index++) {
                JSONObject single_obj = makesArray.getJSONObject(index);
                if (latitude == single_obj.getDouble("users_lat") && longitude == single_obj.getDouble("users_long")) {

                    textView.setText(single_obj.getString("Address"));
                    JSONArray commentArray = single_obj.getJSONArray("Comment");
                    int addrating = 0;
                    for (int j = 0; j < commentArray.length(); j++) {
                        JSONObject jsonObject = commentArray.getJSONObject(j);
                        addrating += jsonObject.getInt("Rating");
                    }
                    Float totalrating = (float) addrating / (float) commentArray.length();
                    ratingBar.setRating(totalrating);
                    Location location = new Location("");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    float distanceMeter = currentLocation.distanceTo(location);
                    textView1.setText(String.valueOf(distanceMeter) + " meters");

                }
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                marker.remove();
                markerDialog.dismiss();
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    for (int index = 0; index < makesArray.length(); index++) {
                        JSONObject single_obj = makesArray.getJSONObject(index);
                        if (latitude == single_obj.getDouble("users_lat") && longitude == single_obj.getDouble("users_long")) {
                            Intent infointent = new Intent(getContext(), ToiletInfoClass.class);
                            infointent.putExtra("Toilet_ID", single_obj.getString("Toilet_ID"));
                            infointent.putExtra("Gender", single_obj.getString("Gender"));
                            infointent.putExtra("Higenic", single_obj.getString("Higenic"));
                            infointent.putExtra("Place", single_obj.getString("Place"));
                            infointent.putExtra("Rating", single_obj.getString("Rating"));
                            infointent.putExtra("Disabled_Access", single_obj.getString("Disabled_Access"));
                            infointent.putExtra("Address", single_obj.getString("Address"));
                            infointent.putExtra("Image_1", single_obj.getString("First_Image"));
                            infointent.putExtra("Image_2", single_obj.getString("Second_Image"));
                            infointent.putExtra("Image_3", single_obj.getString("Third_Image"));
                            infointent.putExtra("comment", single_obj.getJSONArray("Comment").toString());
                            getContext().startActivity(infointent);
                            ((Activity) getContext()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            markerDialog.dismiss();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        driving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bDriving = true;
                LatLng orgin = new LatLng(latitude,longitude);

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(orgin, latLng);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);

                markerDialog.dismiss();

            }
        });

        markerDialog.show();

    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Travelling Mode
        String mode = "mode=driving";

        if(bDriving){
            mode = "mode=driving";
            mMode = 0 ;
        }else if(bBiCycling){
            mode = "mode=bicycling";
            mMode = 1;
        }else if(bWalking){
            mode = "mode=walking";
            mMode = 2;
        }

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);

                // Changing the color polyline according to the mode
                if(mMode==MODE_DRIVING)
                    lineOptions.color(Color.RED);
                else if(mMode==MODE_BICYCLING)
                    lineOptions.color(Color.GREEN);
                else if(mMode==MODE_WALKING)
                    lineOptions.color(Color.BLUE);
            }

            if(result.size()<1){
                Toast.makeText(getContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Drawing polyline in the Google Map for the i-th route
           toiletrouite = googleMap.addPolyline(lineOptions);
        }
    }

    private void clickableMarker(double latitude, double longitude) {

        try {
            for (int index = 0; index < makesArray.length(); index++) {
                JSONObject single_obj = makesArray.getJSONObject(index);
                if (latitude == single_obj.getDouble("users_lat") && longitude == single_obj.getDouble("users_long")) {
                    addressTextview.setText(single_obj.getString("Address"));
                    //ratingBar.setRating(single_obj.getInt("Rating"));
                    JSONArray commentArray = single_obj.getJSONArray("Comment");
                    int addrating = 0;
                    for (int j = 0; j < commentArray.length(); j++) {
                        JSONObject jsonObject = commentArray.getJSONObject(j);
                        addrating += jsonObject.getInt("Rating");
                    }
                    Float totalrating = (float) addrating / (float) commentArray.length();
                    Log.e("total_Rating", String.valueOf(totalrating));
                    ratingBar.setRating(totalrating);
                    Location location = new Location("");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    float distanceMeter = currentLocation.distanceTo(location);
                    distancetextView.setText(String.valueOf(distanceMeter) + " meters");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void postServerMethod() {
        //double lati,longi;
        JSONObject finalobj = new JSONObject();

        try {
            if (latLng != null) {
                if (latLng.latitude != 0 && latLng.longitude != 0) {
                    Log.e("Lat_Long", String.valueOf(latLng.latitude) + " " + String.valueOf(latLng.longitude));
                    finalobj.put("Latitude", latLng.latitude);
                    finalobj.put("Longitude", latLng.longitude);
                }else {
                    finalobj.put("Latitude", 0);
                    finalobj.put("Longitude", 0);
                }
            }else {
                finalobj.put("Latitude", 0);
                finalobj.put("Longitude", 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int SocketTimeout = 30000;
        RetryPolicy retry = new DefaultRetryPolicy(SocketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://quadrobay.co.in/Toilet_App/public/api/showroom/toilet-get-data", finalobj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Server Res", response.toString());
                        makesArray = null;
                        JSONObject edmundjson = null;
                        try {
                            edmundjson = new JSONObject(response.toString());
                            makesArray = edmundjson.getJSONArray("Response");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (isAdded()) {
                            parseMethod();
                            dialog.dismiss();
                        }
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Server err", error.toString());
                        error.printStackTrace();

                        dialog.dismiss();
                        //networkError = true;
                        String message = null;

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            //Toast.makeText(context, "Timeout Error!", Toast.LENGTH_SHORT).show();
                            message = "Timeout Error!.";

                        } else if (error instanceof AuthFailureError) {
                            //Toast.makeText(context, "Authentication Error!", Toast.LENGTH_SHORT).show();
                            message = "Authentication Error!.";

                        } else if (error instanceof ServerError) {
                            //Toast.makeText(context, "Server Side Error!", Toast.LENGTH_SHORT).show();
                            message = "Server Side Error!.";
                        } else if (error instanceof NetworkError) {
                            //Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show();
                            message = "Network Error!";
                        } else if (error instanceof ParseError) {
                            //Toast.makeText(context, "Parse Error!", Toast.LENGTH_SHORT).show();
                            message = "Parse Error!.";
                        }

                        android.app.AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                            builder = new android.app.AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                        }else {
                            builder = new android.app.AlertDialog.Builder(getContext());
                        }

                        builder.setTitle("Please specify the error")
                                .setMessage(message+" Please try again")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                        //ShowProgressDialog();
                                        postServerMethod();
                                    }
                                })
                                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                    }


                }

        );

        request.setRetryPolicy(retry);
        AppController.getInstance().addToRequestQueue(request, "json_req_method");
    }

    private void parseMethod() {

        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.wc);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        googleMap.clear();
        try {
            if (makesArray != null && makesArray.length() > 0) {
                Log.e("Parse_array_length", String.valueOf(makesArray.length()));
                for (int index = 0; index < makesArray.length(); index++) {
                    JSONObject makes_obj = makesArray.getJSONObject(index);
                    LatLng newlatlong = new LatLng(makes_obj.getDouble("users_lat"), makes_obj.getDouble("users_long"));
                    googleMap.addMarker(new MarkerOptions().position(newlatlong).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                }
            } else {
                //addressTextview.setText("");
                //ratingBar.setRating(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);
        if (permsRequestCode == PERMISSION_CALLBACK_CONSTANT){
            //check if all permissions are granted
            boolean allgranted = false;
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted){
                locationservice();
            }else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(),permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(),permissionsRequired[1]) || ActivityCompat.checkSelfPermission((Activity) getContext(), permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED){

                AlertDialog.Builder builder = new AlertDialog.Builder((Activity) getContext());
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs phone state and Location permissions.");
                builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions((Activity) getContext(),permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load data and do stuff
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getActivity().registerReceiver(customReceiver,intentFilter);
        }else{
            AppController.getInstance().setConnectivityListener(this);
        }
        //dialog.dismiss();
        ShowProgressDialog();
        supportMapFragment.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        dialog.dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();

        dialog.dismiss();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getActivity().unregisterReceiver(customReceiver);
        }

        locationManager.removeUpdates(this);
        supportMapFragment.onStop();

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected){
           // PermissionCheck();
           // dialog.dismiss();
           // ShowProgressDialog();
        }

    }

    private class CustomReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String actionOfIntent = intent.getAction();
            boolean isConnected = checkConnection();
            if(actionOfIntent.equals(CONNECTIVITY_ACTION)){
                if (isConnected){



                }
            }
        }
    }
}
