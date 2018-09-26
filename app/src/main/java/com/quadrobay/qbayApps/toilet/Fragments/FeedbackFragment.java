package com.quadrobay.qbayApps.toilet.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.quadrobay.qbayApps.toilet.Activitys.FeedbackListclass;
import com.quadrobay.qbayApps.toilet.Adapters.CustomSpinnerAdapter;
import com.quadrobay.qbayApps.toilet.MainActivity;
import com.quadrobay.qbayApps.toilet.R;
import com.quadrobay.qbayApps.toilet.Singleton.AppController;
import com.quadrobay.qbayApps.toilet.Singleton.ConnectivityReceiver;
import com.quadrobay.qbayApps.toilet.Singleton.CustomToast;
import com.quadrobay.qbayApps.toilet.Singleton.FeedBackClass;
import com.quadrobay.qbayApps.toilet.Singleton.VolleyMultiPartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

/**
 * Created by sairaj on 11/12/17.
 */

public class FeedbackFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemSelectedListener,ConnectivityReceiver.ConnectivityReceiverListener {

    Context context;
    ImageView toolbarleftlogo, toolbarrihtlogo,screenshotImageview;
    TextView textView,internetconnction;
    Spinner screenSpinner;
    EditText summaryEdittext,descriptionEdittext,suggestionEdittext;
    Button submitButton;
    ArrayList<String> screenList = new ArrayList<>();
    CustomSpinnerAdapter screenAdapter;
    private static final int SELECT_FILE = 0;
    Bitmap bm=null;
    String spinnerStr = "",summaryStr = "",descriptionStr = "",suggestionStr = "",unique_id = "";
    android.support.v7.app.AlertDialog alertdialog;
    android.support.v7.app.AlertDialog.Builder dialogBuilder;
    Boolean internetConnectivity;
    CustomReceiver customReceiver;
    IntentFilter intentFilter;
    FeedBackClass feedBackClass;
    Button internetCheck;
    Button settingss;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_fragment, container, false);
        toolbarleftlogo = (ImageView) view.findViewById(R.id.toolbar_logo);
        toolbarrihtlogo = (ImageView) view.findViewById(R.id.toolbar_right_logo);
        textView = (TextView) view.findViewById(R.id.toolbar_header);
        textView.setText("Feedback");
        toolbarrihtlogo.setImageResource(R.drawable.feedback_list);
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

        toolbarrihtlogo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Intent intent = new Intent(getContext(), FeedbackListclass.class);
                        getContext().startActivity(intent);
                        ((Activity) getContext()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

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

        initViews(view);
        spinnerSetup();
        feedBackClass = new FeedBackClass();

        return view;
    }

    private void ShowProgressDialog() {

        dialogBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.loading_progress_layout, null);
        internetconnction = (TextView) dialogView.findViewById(R.id.internet_text);
        internetCheck=(Button) dialogView.findViewById(R.id.internetRetry);
        settingss=(Button) dialogView.findViewById(R.id.settings_btn);
        internetCheck.setVisibility(View.GONE);
        settingss.setVisibility(View.GONE);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        alertdialog = dialogBuilder.create();

//        setting_Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                getContext().startActivity(myIntent);
//            }
//        });

        if (!internetConnectivity){
            internetconnction.setVisibility(View.VISIBLE);

            internetCheck.setVisibility(View.GONE);
        }

        alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        alertdialog.show();
    }

    private void initViews(View view) {

        screenSpinner = (Spinner) view.findViewById(R.id.screen_spinner);
        summaryEdittext = (EditText) view.findViewById(R.id.summary_text);
        //descriptionEdittext = (EditText) view.findViewById(R.id.desc_text);
        suggestionEdittext = (EditText) view.findViewById(R.id.suggestion_text);
        screenshotImageview = (ImageView) view.findViewById(R.id.screenshot);
        submitButton = (Button) view.findViewById(R.id.submit_button);
        screenSpinner.setOnItemSelectedListener(this);
        screenshotImageview.setOnClickListener(this);
        submitButton.setOnClickListener(this);

    }

    private void spinnerSetup() {

        screenList.clear();

        screenList.add("Home");
        screenList.add("Toilet List");
        screenList.add("Add Toilet");
        screenList.add("Toilet Info");
        screenList.add("Others");

        screenAdapter = new CustomSpinnerAdapter<String>(getContext(), R.layout.custom_spinner_item);
        screenAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        screenAdapter.add("Screens");
        screenAdapter.addAll(screenList);
        screenSpinner.setAdapter(screenAdapter);
        screenSpinner.setSelection(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.screenshot:
                galleryIntent();
                break;
            case R.id.submit_button:
                //Toast.makeText(getContext(),"submit",Toast.LENGTH_SHORT);
                //summaryStr      = summaryEdittext.getText().toString();
                //suggestionStr   = suggestionEdittext.getText().toString();
                postRequest();
                break;
            case R.id.settings_btn:
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getContext().startActivity(myIntent);
                break;

        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {

        if (data != null) {
            Uri extras = data.getData();
//            if (extras != null){
//                String path = getPathFromUri(extras);
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 4;
//                bm = BitmapFactory.decodeFile(path,options);
//            }

            ParcelFileDescriptor parcelFD = null;
            try {
                parcelFD = getActivity().getContentResolver().openFileDescriptor(extras, "r");
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
                //bm = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);
                feedBackClass.setBitmap(BitmapFactory.decodeFileDescriptor(imageSource, null, o2));

                //screenshotImageview.setImageBitmap(bm);
                screenshotImageview.setImageBitmap(feedBackClass.getBitmap());

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

        //screenshotImageview.setImageBitmap(bm);
    }

    private String getPathFromUri(Uri extras) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContext().getContentResolver().query(extras,  proj, null, null, null);
        if (cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        Spinner spinner = (Spinner) adapterView;
        int sp_id = spinner.getId();
        switch (sp_id) {
            case R.id.screen_spinner:
                if (i != 0) {
                    //spinnerStr = screenList.get(i - 1);
                    feedBackClass.setScreen(screenList.get(i - 1));
                }else {
                    feedBackClass.setScreen("");
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public byte[] getFileDataFromDrawableJpg(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 99, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void postRequest() {

        feedBackClass.setSummary(summaryEdittext.getText().toString());
        feedBackClass.setSuggestion(suggestionEdittext.getText().toString());


        //if (summaryStr.equals("") && suggestionStr.equals("")){
        if (feedBackClass.getSummary().equals("") && feedBackClass.getSuggestion().equals("")){
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
            }else {
                builder = new AlertDialog.Builder(getContext());
            }

            builder.setTitle("Please specify the error")
                    .setMessage("Please fill up the Error summary or Suggestion")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }else {


            internetConnectivity = checkConnection();
            if (!internetConnectivity) {
                ShowProgressDialog();

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                }else {
                    builder = new AlertDialog.Builder(getContext());
                }

                builder.setTitle("Please specify the error")
                        .setMessage("There is no internet connection. Please try again")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                postRequest();
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
                // postRequest();
                // }
                //}

                ShowProgressDialog();


                unique_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final String dateformatstr = dateFormat.format(calendar.getTime());

                int SocketTimeout = 50000;
                RetryPolicy retry = new DefaultRetryPolicy(SocketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                );

                //our custom volley request
                VolleyMultiPartRequest volleyMultipartRequest = new VolleyMultiPartRequest(Request.Method.POST, "http://quadrobay.co.in/Toilet_App/public/api/showroom/toilet-add-feedback",
                        new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                try {

                                    String res = new String(response.data);
                                    Log.e("response", res);
                                    alertdialog.dismiss();
                                    CustomToast customToast = new CustomToast(getContext(), "Thank you for your feedback, we will update ASAP");
                                    //spinnerStr = "";
                                    feedBackClass.setScreen("");
                                    spinnerSetup();
                                    //summaryStr = "";
                                    feedBackClass.setSummary("");
                                    summaryEdittext.setText("");
                                    //suggestionStr = "";
                                    feedBackClass.setSuggestion("");
                                    suggestionEdittext.setText("");
                                    //bm=null;
                                    feedBackClass.setBitmap(null);
                                    screenshotImageview.setImageResource(R.drawable.addimage);
                                    JSONObject obj = new JSONObject(new String(response.data));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                alertdialog.dismiss();
                               // CustomToast customToast = new CustomToast(getContext(), "Error in report feedback");
                                AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                                }else {
                                    builder = new AlertDialog.Builder(getContext());
                                }

                                builder.setTitle("Please specify the error")
                                        .setMessage("There is some error in internal server. Please try again")
                                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                                postRequest();
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
                        }) {


                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Device", unique_id);
//                params.put("Screen", spinnerStr);
//                params.put("Summary", summaryStr);
//                params.put("Suggestion", suggestionStr);
                        params.put("Screen", feedBackClass.getScreen());
                        params.put("Summary", feedBackClass.getSummary());
                        params.put("Suggestion", feedBackClass.getSuggestion());
                        params.put("Date", dateformatstr);
                        return params;
                    }


                    @Override
                    protected Map<String, DataPart> getByteData() {
                        Map<String, DataPart> params = new HashMap<>();
                        //long imagename = System.currentTimeMillis();
                        //if (bm != null) {
                        if (feedBackClass.getBitmap() != null) {
                            String uniqueID = UUID.randomUUID().toString();
                            //params.put("pic", new DataPart(uniqueID + ".png", getFileDataFromDrawableJpg(bm)));
                            params.put("pic", new DataPart(uniqueID + ".png", getFileDataFromDrawableJpg(feedBackClass.getBitmap())));
                        }
                        return params;
                    }
                };

                //adding the request to volley
                volleyMultipartRequest.setRetryPolicy(retry);
                AppController.getInstance().addToRequestQueue(volleyMultipartRequest);
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
            getActivity().registerReceiver(customReceiver, intentFilter);
        } else {
            AppController.getInstance().setConnectivityListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getActivity().unregisterReceiver(customReceiver);
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
//        if (isConnected){
//            alertdialog.dismiss();
//            postRequest();
//        }
    }
}
