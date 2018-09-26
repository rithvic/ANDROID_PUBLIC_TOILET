package com.quadrobay.qbayApps.toilet.Activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.quadrobay.qbayApps.toilet.Adapters.FiletrListAdapter;
import com.quadrobay.qbayApps.toilet.R;
import com.quadrobay.qbayApps.toilet.Singleton.AppController;
import com.quadrobay.qbayApps.toilet.Singleton.ConnectivityReceiver;
import com.quadrobay.qbayApps.toilet.Singleton.CustomToast;
import com.quadrobay.qbayApps.toilet.Singleton.VolleyMultiPartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

/**
 * Created by sairaj on 04/01/18.
 */

public class FeedbackListclass extends FragmentActivity implements View.OnTouchListener, ConnectivityReceiver.ConnectivityReceiverListener {

    Context context = this;
    ImageView toolbarLeftLogo, toolbarRightLogo;
    TextView toolbarTextview, internetconnction;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FiletrListAdapter filetrListAdapter;
    String unique_id = "";
    ArrayList<String> Device_ID = new ArrayList<>();
    ArrayList<String> Screen = new ArrayList<>();
    ArrayList<String> Summary = new ArrayList<>();
    ArrayList<String> Screenshot= new ArrayList<>();
    ArrayList<String> Suggestion = new ArrayList<>();
    ArrayList<String> Date = new ArrayList<>();
    android.support.v7.app.AlertDialog dialog;
    android.support.v7.app.AlertDialog.Builder dialogBuilder;
    Boolean internetConnectivity = false;
    CustomReceiver customReceiver;
    IntentFilter intentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);

        toolbarTextview = (TextView) findViewById(R.id.toolbar_header);
        toolbarLeftLogo = (ImageView) findViewById(R.id.toolbar_logo);
        toolbarRightLogo = (ImageView) findViewById(R.id.toolbar_right_logo);
        toolbarTextview.setText("Feedback List");
        toolbarRightLogo.setVisibility(View.INVISIBLE);
        toolbarLeftLogo.setImageResource(R.drawable.leftarrow);

        toolbarLeftLogo.setOnTouchListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentFilter = new IntentFilter();
            intentFilter.addAction(CONNECTIVITY_ACTION);
            customReceiver = new CustomReceiver();
        }

        //loadData();
        ShowProgressDialog();
    }

    private void ShowProgressDialog() {

        dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.loading_progress_layout, null);

        internetconnction = (TextView) dialogView.findViewById(R.id.internet_text);
        Button setings=(Button) dialogView.findViewById(R.id.settings_btn);
        setings.setVisibility(View.INVISIBLE);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        dialog = dialogBuilder.create();

        internetConnectivity = checkConnection();
        if(!internetConnectivity){
            internetconnction.setVisibility(View.VISIBLE);
        }else {
            loadData();
        }

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void loadData() {

        unique_id = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("Server List Res", unique_id);


        int SocketTimeout = 30000;
        RetryPolicy retry= new DefaultRetryPolicy(SocketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );

        VolleyMultiPartRequest volleyMultipartRequest = new VolleyMultiPartRequest(Request.Method.POST, "http://quadrobay.co.in/Toilet_App/public/api/showroom/toilet-get-feedback",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {

                            String res = new String(response.data);
                            Log.e("response",res);
                            JSONObject obj = new JSONObject(new String(response.data));
                            parseMethod(res);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Device", unique_id);
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                //long imagename = System.currentTimeMillis();
                return params;
            }
        };

        //adding the request to volley
        volleyMultipartRequest.setRetryPolicy(retry);
        AppController.getInstance().addToRequestQueue(volleyMultipartRequest);

    }

    private void parseMethod(String s) {

        dialog.dismiss();

        try {
            JSONObject jsonObject = new JSONObject(s);
            //JSONArray jsonArray = jsonObject.getJSONArray("Response");

            //if (jsonArray != null && jsonArray.length() > 0){
            if(jsonObject.optJSONArray("Response") != null){
                JSONArray jsonArray = jsonObject.getJSONArray("Response");
                Log.e("length", String.valueOf(jsonArray.length()));
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject makes_obj = jsonArray.getJSONObject(index);

                    Device_ID.add(makes_obj.getString("Device_ID"));
                    Screen.add(makes_obj.getString("Screen"));
                    Summary.add(makes_obj.getString("Summary"));
                    Suggestion.add(makes_obj.getString("Suggestion"));
                    Screenshot.add(makes_obj.getString("Screenshot"));
                    Date.add(makes_obj.getString("Updated_Time"));

                }

                filetrListAdapter = new FiletrListAdapter(context,Device_ID,Screen,Summary,Suggestion,Screenshot,Date);
                recyclerView.setAdapter(filetrListAdapter);

            }else {

                CustomToast customToast = new CustomToast(context,"No data found");
                onBackPressed();

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (view.getId()){

            case R.id.toolbar_logo:
                onBackPressed();
                break;

        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load data and do stuff
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(customReceiver, intentFilter);
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            unregisterReceiver(customReceiver);
        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected) {
            dialog.dismiss();
            ShowProgressDialog();
        }

    }

    private Boolean checkConnection() {

        boolean isConnected = ConnectivityReceiver.isConnected();
        return isConnected;
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
