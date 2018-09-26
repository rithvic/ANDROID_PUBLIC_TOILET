package com.quadrobay.qbayApps.toilet.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.quadrobay.qbayApps.toilet.Activitys.AddToiletClass;
import com.quadrobay.qbayApps.toilet.Adapters.ListDetailAdapter;
import com.quadrobay.qbayApps.toilet.MainActivity;
import com.quadrobay.qbayApps.toilet.R;
import com.quadrobay.qbayApps.toilet.Singleton.AppController;
import com.quadrobay.qbayApps.toilet.Singleton.ConnectivityReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

/**
 * Created by sairaj on 16/11/17.
 */

public class ToiletListFragment extends Fragment implements View.OnClickListener, LocationListener, ConnectivityReceiver.ConnectivityReceiverListener {
    Context context;
    ImageView toolbarleftlogo, toolbarrihtlogo;
    TextView textView,internetconnction;
    LinearLayout phoneRelativelayout,appRelativelayout;
    Button settings,allow;
    LocationManager locationManager;
    LatLng latLng;
    Boolean continouszooming = true,internetConnectivity;
    RecyclerView recyclerView;
   // ArrayList<GettersetterClass> gettersetterClassArrayList = new ArrayList<GettersetterClass>();
    ArrayList<String> ToiletID = new ArrayList<>();
    ArrayList<Double> Latitude = new ArrayList<>();
    ArrayList<Double> Longtitude = new ArrayList<>();
    ArrayList<String> Gender = new ArrayList<>();
    ArrayList<String> Hygenic = new ArrayList<>();
    ArrayList<String> Place = new ArrayList<>();
    ArrayList<String> Rating = new ArrayList<>();
    ArrayList<String> Disabledaccess = new ArrayList<>();
    ArrayList<String> Cost = new ArrayList<>();
    ArrayList<String> Address = new ArrayList<>();
    ArrayList<String> First_Image = new ArrayList<>();
    ArrayList<String> Second_Image = new ArrayList<>();
    ArrayList<String> Third_Image = new ArrayList<>();
    ArrayList<JSONArray> Comment_Array = new ArrayList<>();
    ListDetailAdapter listDetailAdapter;
    AlertDialog dialog;
    AlertDialog.Builder dialogBuilder;
    SharedPreferences sharedPreferences;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private String[] permissionsRequired = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    CustomReceiver customReceiver;
    IntentFilter intentFilter;
Button internetCheck;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        toolbarleftlogo = (ImageView) view.findViewById(R.id.toolbar_logo);
        toolbarrihtlogo = (ImageView) view.findViewById(R.id.toolbar_right_logo);
        toolbarrihtlogo.setOnClickListener(this);
        textView = (TextView) view.findViewById(R.id.toolbar_header);
        textView.setText("Toilet List");
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

        //ShowProgressDialog();
        initviews(view);

        //postServerMethod();
        return view;
    }

    private void ShowProgressDialog()

    {




        dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.loading_progress_layout, null);
internetCheck=(Button) dialogView.findViewById(R.id.internetRetry);
        phoneRelativelayout = (LinearLayout) dialogView.findViewById(R.id.phone_location);
        appRelativelayout = (LinearLayout) dialogView.findViewById(R.id.app_location);
        internetconnction = (TextView) dialogView.findViewById(R.id.internet_text);
        settings = (Button) dialogView.findViewById(R.id.settings_btn);
        allow = (Button) dialogView.findViewById(R.id.app_settings_btn);
        settings.setOnClickListener(this);
        allow.setOnClickListener(this);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        dialog = dialogBuilder.create();

        internetCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent InIntent=new Intent(Settings.ACTION_SETTINGS);
                getContext().startActivity(InIntent);
            }
        });

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


        } else if (ActivityCompat.checkSelfPermission(getContext(), permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED){

            phoneRelativelayout.setVisibility(View.GONE);
            appRelativelayout.setVisibility(View.VISIBLE);
            internetCheck.setVisibility(View.GONE);

            internetconnction.setVisibility(View.GONE);

        }else if (!internetConnectivity){

            phoneRelativelayout.setVisibility(View.GONE);
            appRelativelayout.setVisibility(View.GONE);
            internetconnction.setVisibility(View.VISIBLE);


        }
        else {

            locationservice();

        }

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void locationservice() {
        internetCheck.setVisibility(View.GONE);


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }

    }

    private void initviews(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void postServerMethod() {
        JSONObject finalobj = new JSONObject();

        try {
            if (latLng.latitude != 0 && latLng.longitude != 0) {
                Log.e("Lat_Long", String.valueOf(latLng.latitude)+" "+String.valueOf(latLng.longitude));
                finalobj.put("Latitude", latLng.latitude);
                finalobj.put("Longitude", latLng.longitude);
            } else {
                finalobj.put("Latitude", 0);
                finalobj.put("Longitude", 0);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }



        int SocketTimeout = 30000;
        RetryPolicy retry= new DefaultRetryPolicy(SocketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://quadrobay.co.in/Toilet_App/public/api/showroom/toilet-get-data", finalobj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Server List Res", response.toString());
                        parseMethod(response.toString());
                        dialog.dismiss();
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Server err", error.toString());
                        error.printStackTrace();
                    }


                }

        );

        request.setRetryPolicy(retry);
        AppController.getInstance().addToRequestQueue(request,"json_req_method");
    }

    private void parseMethod(String response) {

        ToiletID.clear();
        Latitude.clear();
        Longtitude.clear();
        Gender.clear();
        Hygenic.clear();
        Place.clear();
        Rating.clear();
        Disabledaccess.clear();
        Cost.clear();
        Address.clear();
        First_Image.clear();
        Second_Image.clear();
        Third_Image.clear();
        Comment_Array.clear();

        JSONObject responsejson = null;
        JSONArray responseArray;
        try {
            responsejson = new JSONObject(response.toString());
            responseArray = responsejson.getJSONArray("Response");
            for (int index = 0; index < responseArray.length(); index++) {
                JSONObject arrayjson = responseArray.getJSONObject(index);
               /* GettersetterClass gettersetterClass = new GettersetterClass();
                gettersetterClass.setToiletID(arrayjson.getString("Toilet_ID"));
                gettersetterClass.setLatitude(arrayjson.getString("users_lat"));
                gettersetterClass.setLongtitude(arrayjson.getString("users_long"));
                gettersetterClass.setGender(arrayjson.getString("Gender"));
                gettersetterClass.setHygenic(arrayjson.getString("Higenic"));
                gettersetterClass.setPlace(arrayjson.getString("Place"));
                gettersetterClass.setRating(arrayjson.getString("Rating"));
                gettersetterClass.setDisabledaccess(arrayjson.getString("Disabled_Access"));
                gettersetterClass.setCost(arrayjson.getString("Cost"));
                gettersetterClass.setAddress(arrayjson.getString("Address"));
                gettersetterClass.setFirst_Image(arrayjson.getString("First_Image"));
                gettersetterClass.setSecond_Image(arrayjson.getString("Second_Image"));
                gettersetterClass.setThird_Image(arrayjson.getString("Third_Image"));
                gettersetterClass.setComment_Array(arrayjson.getJSONArray("Comment"));
                gettersetterClassArrayList.add(gettersetterClass);
                int addrating = 0;
                try {
                    for (int i = 0; i < gettersetterClassArrayList.size(); i++) {
                        //  Toast.makeText(this,gettersetterClassArrayList.get(j).+"hii",Toast.LENGTH_SHORT).show();
                        JSONObject single_obj = gettersetterClassArrayList.get(i).getComment_Array().getJSONObject(index);
                        addrating += single_obj.getInt("Rating");
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }*/

                ToiletID.add(arrayjson.getString("Toilet_ID"));
                Latitude.add(arrayjson.getDouble("users_lat"));
                Longtitude.add(arrayjson.getDouble("users_long"));
                Gender.add(arrayjson.getString("Gender"));
                Hygenic.add(arrayjson.getString("Higenic"));
                Place.add(arrayjson.getString("Place"));
                Rating.add(arrayjson.getString("Rating"));
                Disabledaccess.add(arrayjson.getString("Disabled_Access"));
                Cost.add(arrayjson.getString("Cost"));
                Address.add(arrayjson.getString("Address"));
                First_Image.add(arrayjson.getString("First_Image"));
                Second_Image.add(arrayjson.getString("Second_Image"));
                Third_Image.add(arrayjson.getString("Third_Image"));
                Comment_Array.add(arrayjson.getJSONArray("Comment"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listDetailAdapter = new ListDetailAdapter(getContext(),ToiletID,Latitude,Longtitude,Gender,Hygenic,Place,Rating,Disabledaccess,Cost,Address,First_Image,Second_Image,Third_Image,Comment_Array);
        recyclerView.setAdapter(listDetailAdapter);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.toolbar_right_logo:
                Intent intent = new Intent(getContext(), AddToiletClass.class);
                getContext().startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
            case R.id.settings_btn:
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getContext().startActivity(myIntent);
                break;

            case R.id.app_settings_btn:
                ActivityCompat.requestPermissions((Activity) getContext(), permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                break;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if (continouszooming) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            postServerMethod();
            continouszooming = false;
        }
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

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
                //oast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    private Boolean checkConnection() {

        boolean isConnected = ConnectivityReceiver.isConnected();
        return isConnected;
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
       // dialog.dismiss();
        continouszooming = true;
        ShowProgressDialog();
    }


    @Override
    public void onStop() {
        super.onStop();

        dialog.dismiss();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getActivity().unregisterReceiver(customReceiver);
        }

        locationManager.removeUpdates(this);

    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected){
            // PermissionCheck();
            //locationservice();
            dialog.dismiss();
            ShowProgressDialog();
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
