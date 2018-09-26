package com.quadrobay.qbayApps.toilet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.quadrobay.qbayApps.toilet.Adapters.MenuAdpter;
import com.quadrobay.qbayApps.toilet.Fragments.FeedbackFragment;
import com.quadrobay.qbayApps.toilet.Fragments.MapFragment;
import com.quadrobay.qbayApps.toilet.Fragments.ToiletListFragment;

public class MainActivity extends FragmentActivity implements LocationListener {
    protected Context context = this;
    MenuAdpter adpter;
    FragmentTransaction ft;
    ListView list, subList;
    Typeface helvaticabold;
    String[] lvMenuItems = {"Map", "List", "Feedback"};
    Integer[] icons = {R.drawable.map,R.drawable.feedback_list,R.drawable.feedback};
    public static SlidingMenu slidingMenu;
    LocationManager locationManager;

    //Permissions
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private String[] permissionsRequired = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionStatus = getSharedPreferences("PermissionStatus", MODE_PRIVATE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[1])
                         || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[2])) {
                //Create alert. Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Application needs permission");
                builder.setMessage("This app needs location services");
                builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                        proceedAfterPermission();
                    }
                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.cancel();
//                       // proceedAfterPermission();
//                    }
//                });
                builder.show();
            }else {
                //just request the permission
                ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(permissionsRequired[0], true);
                editor.commit();
            }
        } else {
            proceedAfterPermission();
        }

        adView = (AdView) findViewById(R.id.adView);
        String deviceid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("Device_ID", deviceid);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        Fragment fr = new MapFragment();
//        ft.replace(R.id.content_fragment, fr);
//        ft.commit();

        setSlidingMenu();
        getSlidingMenu();
    }

    private View setSlidingMenu() {

        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        /* Swipe functionality work */
        //slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        /* Swipe functionality doesn't work */
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);

        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(getSlidingMenu());

        return null;

    }

        @SuppressLint("RestrictedApi")
        public View getSlidingMenu() {

            adpter = new MenuAdpter(context,lvMenuItems,helvaticabold,icons);
            View v = getLayoutInflater().inflate(R.layout.slidingcontent_lay, null);
            list = (ListView) v.findViewById(R.id.drawer_list);

            list.setAdapter(adpter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    slidingMenu.toggle();
                    FragmentManager fm = getSupportFragmentManager();
                     ft = fm.beginTransaction();
                    Fragment fr = null;

                    switch (position) {
                        case 0:
                            fr = new MapFragment();

                            FragmentManager fragmentManager1=getSupportFragmentManager();
                            FragmentTransaction transaction=fragmentManager1.beginTransaction();
                            transaction.add(R.id.content_fragment,fr);
                            transaction.replace(R.id.content_fragment, fr);
                            transaction.commit();
                            //InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            //inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                            break;

                        case 1:
                            fr = new ToiletListFragment();
                            FragmentManager fragmentManager2=getSupportFragmentManager();
                            FragmentTransaction transaction2=fragmentManager2.beginTransaction();
                            transaction2.add(R.id.content_fragment,fr);



                            transaction2.replace(R.id.content_fragment, fr);
                            transaction2.commit();

                           // InputMethodManager inputMethodManager1 = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                           // inputMethodManager1.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                            break;
                        case 2:
                            fr = new FeedbackFragment();

                            FragmentManager fragmentManager3=getSupportFragmentManager();
                            FragmentTransaction transaction3=fragmentManager3.beginTransaction();
                            transaction3.add(R.id.content_fragment,fr);


                            transaction3.replace(R.id.content_fragment,fr);
                            transaction3.commit();

                            InputMethodManager inputMethodManager2 = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager2.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);


                            break;
                    }
                }
            });

            return v;
        }

        @Override
        public void onLocationChanged(Location location) {
        //        SharedPreferences.Editor editor = permissionStatus.edit();
        //        editor.putString("Latitute", String.valueOf(location.getLatitude()));
        //        editor.putString("Longitute", String.valueOf(location.getLongitude()));
        //        editor.commit();
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
                proceedAfterPermission();
            }else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[1]) || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED){

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs phone state and Location permissions.");
                builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
                    }
                });

                builder.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs phone state and Location permissions.");
                builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.show();
                proceedAfterPermission();
                //Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[1]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission proceed
                proceedAfterPermission();
            }
        }
    }

    private void proceedAfterPermission() {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fr = new MapFragment();
        ft.replace(R.id.content_fragment, fr);
        ft.commitAllowingStateLoss();
        //ft.commit();

    }



    }


