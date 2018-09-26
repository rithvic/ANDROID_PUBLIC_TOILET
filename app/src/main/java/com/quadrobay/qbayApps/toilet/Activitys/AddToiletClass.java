package com.quadrobay.qbayApps.toilet.Activitys;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quadrobay.qbayApps.toilet.Adapters.CustomSpinnerAdapter;
import com.quadrobay.qbayApps.toilet.R;
import com.quadrobay.qbayApps.toilet.Singleton.AppController;
import com.quadrobay.qbayApps.toilet.Singleton.ConnectivityReceiver;
import com.quadrobay.qbayApps.toilet.Singleton.VolleyMultiPartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

/**
 * Created by sairaj on 18/11/17.
 */

public class AddToiletClass extends FragmentActivity implements View.OnTouchListener, LocationListener, OnMapReadyCallback, AdapterView.OnItemSelectedListener, View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {
    Context context = this;
    ImageView toolbarLeftLogo, toolbarRightLogo, firststarImageview, secondstarImageview, thirdstarImageview, fourthstarImageview, fifthstarImageview;
    TextView toolbarTextview, areatextview, citytextview, internetconnction;
    GoogleMap googleMap;
    LocationManager locationManager;
    SupportMapFragment supportMapFragment;
    LatLng latLng;
    double latitude, longitude;
    String locationAddress, city, state, genderString, higenicString, placeString, feesString, disableString, comments = "";
    int rating = 1;
    Spinner genderSpinner, higenicSpinner, placeSpinner, feesSpinner, disableSpinner;
    EditText editTextcomment;
    Button addbutton, settings, allow, nextbutton, cancelbutton;
    CustomSpinnerAdapter<String> genderAdapter, higenicAdapter, placeAdapter, feesAdapter, disableAdapter;
    ArrayList<String> genderList = new ArrayList<>();
    ArrayList<String> higenicList = new ArrayList<>();
    ArrayList<String> placeList = new ArrayList<>();
    ArrayList<String> feesList = new ArrayList<>();
    ArrayList<String> disableList = new ArrayList<>();
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    //Button selectbutton;
    ImageView tvImageview1, tvImageview2, tvImageview3;
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_FILE = 0;
    Bitmap firstbitmap, secondbitmap, thirdbitmap;
    String indentity = "All", toiletID = "";
    android.support.v7.app.AlertDialog dialog,informationDialog;
    android.support.v7.app.AlertDialog.Builder dialogBuilder,informationDialogBuilder;
    LinearLayout phoneRelativelayout, appRelativelayout;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private String[] permissionsRequired = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    Boolean internetConnectivity,isShownDialog = false;
    CustomReceiver customReceiver;
    IntentFilter intentFilter;
    Boolean continouszooming = true;
    LinearLayout linearLayout;
    RelativeLayout addLayout,cancelLayout;
    Button internetcheck;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  super.onSaveInstanceState(savedInstanceState);
        //super.onRestoreInstanceState(savedInstanceState);
        setContentView(R.layout.activity_add_toilet);

        linearLayout = (LinearLayout) findViewById(R.id.hole_layout);
        linearLayout.setAlpha(0.1f);
        toolbarTextview = (TextView) findViewById(R.id.toolbar_header);
        toolbarLeftLogo = (ImageView) findViewById(R.id.toolbar_logo);
        toolbarRightLogo = (ImageView) findViewById(R.id.toolbar_right_logo);
        areatextview = (TextView) findViewById(R.id.Filter_icon_text);
        citytextview = (TextView) findViewById(R.id.city_icon_text);
//        genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
//        higenicSpinner = (Spinner) findViewById(R.id.hygienic_spinner);
//        placeSpinner = (Spinner) findViewById(R.id.place_spinner);
//        feesSpinner = (Spinner) findViewById(R.id.fees_spinner);
//        disableSpinner = (Spinner) findViewById(R.id.Disable_spinner);
//        editTextcomment = (EditText) findViewById(R.id.comment_id);
//        addbutton = (Button) findViewById(R.id.add_button);
//        firststarImageview = (ImageView) findViewById(R.id.first_star);
//        secondstarImageview = (ImageView) findViewById(R.id.second_star);
//        thirdstarImageview = (ImageView) findViewById(R.id.third_star);
//        fourthstarImageview = (ImageView) findViewById(R.id.fourth_star);
//        fifthstarImageview = (ImageView) findViewById(R.id.fifth_star);
        nextbutton = (Button) findViewById(R.id.next_button);
        toolbarLeftLogo.setImageResource(R.drawable.leftarrow);
        toolbarRightLogo.setVisibility(View.INVISIBLE);
        toolbarTextview.setText("Add Toilet");
        toolbarLeftLogo.setOnTouchListener(this);
        nextbutton.setOnClickListener(this);
        //nextbutton.setBackgroundTintList(context.getResources().getColorStateList(R.color.Dark_gray));
//        genderSpinner.setOnItemSelectedListener(this);
//        higenicSpinner.setOnItemSelectedListener(this);
//        placeSpinner.setOnItemSelectedListener(this);
//        feesSpinner.setOnItemSelectedListener(this);
//        disableSpinner.setOnItemSelectedListener(this);
//        addbutton.setOnClickListener(this);
//        firststarImageview.setOnClickListener(this);
//        secondstarImageview.setOnClickListener(this);
//        thirdstarImageview.setOnClickListener(this);
//        fourthstarImageview.setOnClickListener(this);
//        fifthstarImageview.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentFilter = new IntentFilter();
            intentFilter.addAction(CONNECTIVITY_ACTION);
            customReceiver = new CustomReceiver();
        }
//        spinnerSetup();
        initMap();
//        tvImageview1 = (ImageView) findViewById(R.id.ivImage1);
//        tvImageview2 = (ImageView) findViewById(R.id.ivImage2);
//        tvImageview3 = (ImageView) findViewById(R.id.ivImage3);
//        tvImageview1.setOnClickListener(this);
//        tvImageview2.setOnClickListener(this);
//        tvImageview3.setOnClickListener(this);
        ShowProgressDialog();
    }

    private void ShowProgressDialog() {


        dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.loading_progress_layout, null);

        internetcheck=(Button) dialogView.findViewById(R.id.internetRetry);
        internetcheck.setVisibility(View.GONE);

        internetcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent InIntent=new Intent(Settings.ACTION_SETTINGS);
                getApplicationContext().startActivity(InIntent);
            }
        });

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
       // settings.setVisibility(View.GONE);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent1);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        locationManager = (LocationManager) getSystemService(context.LOCATION_SERVICE);
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

            dialog.show();

        } else if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
            phoneRelativelayout.setVisibility(View.GONE);
            appRelativelayout.setVisibility(View.VISIBLE);
            internetconnction.setVisibility(View.GONE);

            dialog.show();
        }
//        else if (!internetConnectivity) {
//            phoneRelativelayout.setVisibility(View.GONE);
//            appRelativelayout.setVisibility(View.GONE);
//            internetconnction.setVisibility(View.VISIBLE);
//
//            dialog.show();
//        }
        else {
            dialog.show();
//            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        }

    }

    /* Take Image from Gallery or Camera */

    private void selectImage() {


        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddToiletClass.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //boolean result = Utility.checkpermission(AddToiletClass.this);
                if (items[i].equals("Take Photo")) {
                    //userChoosenTask="Take Photo";
                    //if (result)
                    cameraIntent();
                } else if (items[i].equals("Choose from Library")) {
                    //userChoosenTask="Choose from Library";
                    //if (result)
                    galleryIntent();
                } else if (items[i].equals("Cancel")) {

                    dialogInterface.dismiss();

                }

            }
        });
        builder.show();
    }

    private void cameraIntent() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == CAMERA_REQUEST)
                onCaptureImageResult(data);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            Uri extras = data.getData();
            if (extras != null) {
//                String path = getPathFromUri(extras);
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 4;
//                bm = BitmapFactory.decodeFile(path, options);
                ParcelFileDescriptor parcelFD = null;
                try {
                    parcelFD = getContentResolver().openFileDescriptor(extras, "r");
                    FileDescriptor imageSource = parcelFD.getFileDescriptor();

                    // Decode image size
                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    BitmapFactory.decodeFileDescriptor(imageSource, null, o);

                    // the new size we want to scale to
                    final int REQUIRED_SIZE = 1024;

                    // Find the correct scale value. It should be the power of 2.
                    int width_tmp = o.outWidth, height_tmp = o.outHeight;
                    int scale = 1;
                    while (true) {
                        if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
                            break;
                        }
                        width_tmp /= 2;
                        height_tmp /= 2;
                        scale *= 2;
                    }

                    // decode with inSampleSize
                    BitmapFactory.Options o2 = new BitmapFactory.Options();
                    o2.inSampleSize = scale;
                    bm = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);


                } catch (FileNotFoundException e) {
                    // handle errors
                } catch (IOException e) {
                    // handle errors
                } finally {
                    if (parcelFD != null)
                        try {
                            parcelFD.close();
                        } catch (IOException e) {
                            // ignored
                        }
                }
            }
        }
        //tvImageview.setImageBitmap(bm);
        if (indentity.equals("First")) {
            firstbitmap = bm;
            tvImageview1.setImageBitmap(bm);
            bitmaps.add(firstbitmap);
        } else if (indentity.equals("Second")) {
            secondbitmap = bm;
            tvImageview2.setImageBitmap(bm);
            bitmaps.add(secondbitmap);
        } else if (indentity.equals("Third")) {
            thirdbitmap = bm;
            tvImageview3.setImageBitmap(bm);
            bitmaps.add(thirdbitmap);
        }

    }

    private String getPathFromUri(Uri extras) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(extras, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        //return cursor.getString(column_index);
        cursor.close();
        return res;
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 99, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (indentity.equals("First")) {
            firstbitmap = thumbnail;
            tvImageview1.setImageBitmap(thumbnail);
            bitmaps.add(firstbitmap);
        } else if (indentity.equals("Second")) {
            secondbitmap = thumbnail;
            tvImageview2.setImageBitmap(thumbnail);
            bitmaps.add(secondbitmap);
        } else if (indentity.equals("Third")) {
            thirdbitmap = thumbnail;
            tvImageview3.setImageBitmap(thumbnail);
            bitmaps.add(thirdbitmap);
        }
    }

    private void spinnerSetup() {

        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Both");

        higenicList.add("Clean");
        higenicList.add("Good");
        higenicList.add("Dirty");

        placeList.add("Restaurant");
        placeList.add("Public");
        placeList.add("Shopping center");
        placeList.add("Gas station");

        feesList.add("Free");
        feesList.add("Pay");

        disableList.add("Yes");
        disableList.add("No");


        genderAdapter = new CustomSpinnerAdapter<String>(context, R.layout.custom_spinner_item);
        genderAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        genderAdapter.add("Gender");
        genderAdapter.addAll(genderList);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setSelection(0);

        higenicAdapter = new CustomSpinnerAdapter<String>(context, R.layout.custom_spinner_item);
        higenicAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        higenicAdapter.add("Hygienic status");
        higenicAdapter.addAll(higenicList);
        higenicSpinner.setAdapter(higenicAdapter);
        higenicSpinner.setSelection(0);

        placeAdapter = new CustomSpinnerAdapter<String>(context, R.layout.custom_spinner_item);
        placeAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        placeAdapter.add("Place");
        placeAdapter.addAll(placeList);
        placeSpinner.setAdapter(placeAdapter);
        placeSpinner.setSelection(0);

        feesAdapter = new CustomSpinnerAdapter<String>(context, R.layout.custom_spinner_item);
        feesAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        feesAdapter.add("Free/Pay");
        feesAdapter.addAll(feesList);
        feesSpinner.setAdapter(feesAdapter);
        feesSpinner.setSelection(0);

        disableAdapter = new CustomSpinnerAdapter<String>(context, R.layout.custom_spinner_item);
        disableAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        disableAdapter.add("Disabled access");
        disableAdapter.addAll(disableList);
        disableSpinner.setAdapter(disableAdapter);
        disableSpinner.setSelection(0);
    }

    private void getLocationAddress() {
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                //locationAddress = addresses.get(0).getAddressLine(0);
                locationAddress = addresses.get(0).getThoroughfare();
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                //Log.e("Location",addresses.get(0).getThoroughfare());
                areatextview.setText(locationAddress);
                citytextview.setText(city + ", " + state);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initMap() {

        supportMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.toolbar_logo:
                onBackPressed();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onMapReady(GoogleMap Map) {

        googleMap = Map;
//        int height = 50;
//        int width = 50;
//        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.wc1);
//        Bitmap b = bitmapdraw.getBitmap();
//        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//
//        //if (latLng != null) {
//            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
//            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            //googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//            googleMap.addMarker(new MarkerOptions().position(latLng).title(locationAddress).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLngmap) {
                    int height = 100;
                    int width = 100;
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.wc);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    googleMap.clear();
                    latLng = new LatLng(latLngmap.latitude, latLngmap.longitude);
                    if (!latLng.equals(null)) {
                        latitude = latLngmap.latitude;
                        longitude = latLngmap.longitude;
                        getLocationAddress();
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(locationAddress).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                    }
                        //nextbutton.setClickable(true);
                    //nextbutton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPrimary));
                    //nextbutton.setOnClickListener(AddToiletClass.this);
                }
            });

            //dialog.dismiss();
        //}

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        Spinner spinner = (Spinner) adapterView;
        int sp_id = spinner.getId();
        switch (sp_id) {
            case R.id.gender_spinner:
                if (i != 0) {
                    genderString = genderList.get(i - 1);
                    Log.e("Gender", genderString);
                }
                break;
            case R.id.hygienic_spinner:
                if (i != 0) {
                    higenicString = higenicList.get(i - 1);
                    Log.e("Higenic", higenicString);
                }
                break;
            case R.id.place_spinner:
                if (i != 0) {
                    placeString = placeList.get(i - 1);
                    Log.e("Place", placeString);
                }
                break;
            case R.id.fees_spinner:
                if (i != 0) {
                    feesString = feesList.get(i - 1);
                    Log.e("Fees", feesString);
                }
                break;
            case R.id.Disable_spinner:
                if (i != 0) {
                    disableString = disableList.get(i - 1);
                    Log.e("Disabled", disableString);
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_button:
                informationPopup();
                break;
//            case R.id.add_button:
//                //Toast.makeText(context,"Clicked",Toast.LENGTH_SHORT).show();
//                postServerMethod();
//                break;
//            case R.id.cancel_button:
//                isShownDialog = false;
//                informationDialog.dismiss();
//                break;
            case R.id.add_button_view:
                //Toast.makeText(context,"Clicked",Toast.LENGTH_SHORT).show();
                postServerMethod();
                break;
            case R.id.cancel_button_view:
                isShownDialog = false;
                informationDialog.dismiss();
                break;
            case R.id.first_star:
                rating = 1;
                firststarImageview.setImageResource(R.drawable.ratestar);
                secondstarImageview.setImageResource(R.drawable.star);
                thirdstarImageview.setImageResource(R.drawable.star);
                fourthstarImageview.setImageResource(R.drawable.star);
                fifthstarImageview.setImageResource(R.drawable.star);
                break;
            case R.id.second_star:
                rating = 2;
                firststarImageview.setImageResource(R.drawable.ratestar);
                secondstarImageview.setImageResource(R.drawable.ratestar);
                thirdstarImageview.setImageResource(R.drawable.star);
                fourthstarImageview.setImageResource(R.drawable.star);
                fifthstarImageview.setImageResource(R.drawable.star);
                break;
            case R.id.third_star:
                rating = 3;
                firststarImageview.setImageResource(R.drawable.ratestar);
                secondstarImageview.setImageResource(R.drawable.ratestar);
                thirdstarImageview.setImageResource(R.drawable.ratestar);
                fourthstarImageview.setImageResource(R.drawable.star);
                fifthstarImageview.setImageResource(R.drawable.star);
                break;
            case R.id.fourth_star:
                rating = 4;
                firststarImageview.setImageResource(R.drawable.ratestar);
                secondstarImageview.setImageResource(R.drawable.ratestar);
                thirdstarImageview.setImageResource(R.drawable.ratestar);
                fourthstarImageview.setImageResource(R.drawable.ratestar);
                fifthstarImageview.setImageResource(R.drawable.star);
                break;
            case R.id.fifth_star:
                rating = 5;
                firststarImageview.setImageResource(R.drawable.ratestar);
                secondstarImageview.setImageResource(R.drawable.ratestar);
                thirdstarImageview.setImageResource(R.drawable.ratestar);
                fourthstarImageview.setImageResource(R.drawable.ratestar);
                fifthstarImageview.setImageResource(R.drawable.ratestar);
                break;
            case R.id.ivImage1:
                indentity = "First";
                selectImage();
                break;
            case R.id.ivImage2:
                indentity = "Second";
                selectImage();
                break;
            case R.id.ivImage3:
                indentity = "Third";
                selectImage();
                break;
        }

    }

    private void informationPopup() {

        informationDialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.information_popup_layout, null);

        genderSpinner = (Spinner) dialogView.findViewById(R.id.gender_spinner);
        higenicSpinner = (Spinner) dialogView.findViewById(R.id.hygienic_spinner);
        placeSpinner = (Spinner) dialogView.findViewById(R.id.place_spinner);
        feesSpinner = (Spinner) dialogView.findViewById(R.id.fees_spinner);
        disableSpinner = (Spinner) dialogView.findViewById(R.id.Disable_spinner);
        editTextcomment = (EditText) dialogView.findViewById(R.id.comment_id);
//        addbutton = (Button) dialogView.findViewById(R.id.add_button);
//        cancelbutton = (Button) dialogView.findViewById(R.id.cancel_button);
        addLayout = (RelativeLayout) dialogView.findViewById(R.id.add_button_view);
        cancelLayout = (RelativeLayout) dialogView.findViewById(R.id.cancel_button_view);
        firststarImageview = (ImageView) dialogView.findViewById(R.id.first_star);
        secondstarImageview = (ImageView) dialogView.findViewById(R.id.second_star);
        thirdstarImageview = (ImageView) dialogView.findViewById(R.id.third_star);
        fourthstarImageview = (ImageView) dialogView.findViewById(R.id.fourth_star);
        fifthstarImageview = (ImageView) dialogView.findViewById(R.id.fifth_star);


        genderSpinner.setOnItemSelectedListener(this);
        higenicSpinner.setOnItemSelectedListener(this);
        placeSpinner.setOnItemSelectedListener(this);
        feesSpinner.setOnItemSelectedListener(this);
        disableSpinner.setOnItemSelectedListener(this);
//        addbutton.setOnClickListener(this);
//        cancelbutton.setOnClickListener(this);
        addLayout.setOnClickListener(this);
        cancelLayout.setOnClickListener(this);
        firststarImageview.setOnClickListener(this);
        secondstarImageview.setOnClickListener(this);
        thirdstarImageview.setOnClickListener(this);
        fourthstarImageview.setOnClickListener(this);
        fifthstarImageview.setOnClickListener(this);


        genderList.clear();
        higenicList.clear();
        placeList.clear();
        feesList.clear();
        disableList.clear();
        rating = 1;
        genderString = null;
        higenicString = null;
        placeString = null;
        feesString = null;
        disableString = null;
        firstbitmap = null;
        secondbitmap = null;
        thirdbitmap = null;

        spinnerSetup();

        tvImageview1 = (ImageView) dialogView.findViewById(R.id.ivImage1);
        tvImageview2 = (ImageView) dialogView.findViewById(R.id.ivImage2);
        tvImageview3 = (ImageView) dialogView.findViewById(R.id.ivImage3);
        tvImageview1.setOnClickListener(this);
        tvImageview2.setOnClickListener(this);
        tvImageview3.setOnClickListener(this);


        informationDialogBuilder.setView(dialogView);
        informationDialogBuilder.setCancelable(false);
        informationDialog = informationDialogBuilder.create();

        isShownDialog = true;
        informationDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        informationDialog.show();

    }

    private void postServerMethod() {

        comments = editTextcomment.getText().toString();
        //imageUploadTask();
        if (genderString != null && higenicString  != null && disableString != null && feesString != null && placeString != null){

            internetConnectivity = checkConnection();
            if (!internetConnectivity) {

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                }else {
                    builder = new AlertDialog.Builder(context);
                }

                builder.setTitle("Please specify the error")
                        .setMessage("There is no internet connection. Please try again")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
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

            } else {
                //postServerMethod();
                JSONObject finalobj = new JSONObject();
                try {
                    if (latitude != 0 && longitude != 0) {
                        finalobj.put("Latitude", latitude);
                        finalobj.put("Longitude", longitude);
                    } else {
                        finalobj.put("Latitude", 0);
                        finalobj.put("Longitude", 0);
                    }

                    if (genderString != null) {
                        finalobj.put("Gender", genderString);
                    } else {
                        finalobj.put("Gender", "");
                    }

                    if (higenicString != null) {
                        finalobj.put("Higenic", higenicString);
                    } else {
                        finalobj.put("Higenic", "");
                    }

                    if (placeString != null) {
                        finalobj.put("Place", placeString);
                    } else {
                        finalobj.put("Place", "");
                    }

                    if (rating != 0) {
                        finalobj.put("Rating", rating);
                        Log.e("Rating", String.valueOf(rating));
                    } else {
                        finalobj.put("Rating", rating);
                        Log.e("No Rating", String.valueOf(rating));
                    }

                    if (disableString != null) {
                        finalobj.put("Disabled_Access", disableString);
                    } else {
                        finalobj.put("Disabled_Access", "");
                    }

                    if (feesString != null) {
                        finalobj.put("Cost", feesString);
                    } else {
                        finalobj.put("Cost", "");
                    }

                    if (locationAddress != null && city != null && state != null) {
                        finalobj.put("Address", locationAddress + ", " + city + ", " + state);
                    } else {
                        finalobj.put("Address", "");
                    }

                    if (comments != "") {
                        finalobj.put("Comment", comments);
                    } else {
                        finalobj.put("Comment", "");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                int SocketTimeout = 30000;
                RetryPolicy retry = new DefaultRetryPolicy(SocketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                );
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://quadrobay.co.in/Toilet_App/public/api/showroom/toilet-project", finalobj,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("Server Res", response.toString());
                                imageUploadTask(response.toString());
                            }
                        },

                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Server err", error.toString());
                                error.printStackTrace();

                                AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                                }else {
                                    builder = new AlertDialog.Builder(context);
                                }

                                builder.setTitle("Please specify the error")
                                        .setMessage("There is some error in internal server. Please try again")
                                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
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

        }else {

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
            }else {
                builder = new AlertDialog.Builder(context);
            }

            builder.setTitle("Please specify the error")
                    .setMessage("Please fill up all the required information")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }

    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] getFileDataFromDrawableJpg(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 99, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void imageUploadTask(String response) {

        isShownDialog = false;
        informationDialog.dismiss();

        JSONObject edmundjson = null;
        JSONArray makesArray;
        try {
            if (!response.equals(null)){
                edmundjson = new JSONObject(response.toString());
            makesArray = edmundjson.getJSONArray("Response");
            //Log.e("Array",makesArray.toString());
            for (int index = 0; index < makesArray.length(); index++) {
                JSONObject single_obj = makesArray.getJSONObject(index);
                toiletID = single_obj.getString("Toilet_ID");
            }
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //getting the tag from the edittext
        final String tags = "Picture";

        int SocketTimeout = 50000;
        RetryPolicy retry = new DefaultRetryPolicy(SocketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );

        //our custom volley request
        VolleyMultiPartRequest volleyMultipartRequest = new VolleyMultiPartRequest(Request.Method.POST, "http://quadrobay.co.in/Toilet_App/public/api/showroom/toilet-upload-images",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            if(!response.equals(null)) {
                                String res = new String(response.data);
                                Log.e("response", res);
                                Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_SHORT).show();
                                JSONObject obj = new JSONObject(new String(response.data));
                                onBackPressed();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Toilet_ID", toiletID);
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                //long imagename = System.currentTimeMillis();
                for (int index = 0; index < bitmaps.size(); index++) {
                    if (toiletID != "") {
                        params.put("pic" + index, new DataPart(toiletID + "_" + index + ".png", getFileDataFromDrawableJpg(bitmaps.get(index))));
                    }
                }
                return params;
            }
        };

        //adding the request to volley
        volleyMultipartRequest.setRetryPolicy(retry);
        AppController.getInstance().addToRequestQueue(volleyMultipartRequest);
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
            registerReceiver(customReceiver, intentFilter);
        } else {
            AppController.getInstance().setConnectivityListener(this);
        }

        if (!isShownDialog) {
            dialog.dismiss();
            supportMapFragment.onResume();
            continouszooming = true;
            ShowProgressDialog();
            //nextbutton.setClickable(false);
            //nextbutton.setBackgroundTintList(context.getResources().getColorStateList(R.color.Dark_gray));
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            unregisterReceiver(customReceiver);
        }

        locationManager.removeUpdates(this);
        supportMapFragment.onStop();

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

//        if (isConnected) {
//            dialog.dismiss();
//            postServerMethod();
//           // onBackPressed();
//        }

    }

    @Override
    public void onLocationChanged(Location location) {

        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.wc);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        if (!location.equals(null)) {
            if (continouszooming) {

                LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                getLocationAddress();
                googleMap.clear();
                //CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
                //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15), 10, null);
                googleMap.addMarker(new MarkerOptions().position(latLng).title(locationAddress).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                continouszooming = false;
                dialog.dismiss();
                linearLayout.setAlpha(1.0f);
            }

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
                dialog.dismiss();
                ShowProgressDialog();
            }else if (ActivityCompat.shouldShowRequestPermissionRationale(AddToiletClass.this,permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(AddToiletClass.this,permissionsRequired[1]) || ActivityCompat.checkSelfPermission(AddToiletClass.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED){

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs phone state and Location permissions.");
                builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(AddToiletClass.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
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

}
