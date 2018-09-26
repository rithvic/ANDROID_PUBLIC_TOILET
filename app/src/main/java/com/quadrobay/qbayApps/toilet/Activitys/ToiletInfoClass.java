package com.quadrobay.qbayApps.toilet.Activitys;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.quadrobay.qbayApps.toilet.Adapters.CommantListAdapter;
import com.quadrobay.qbayApps.toilet.Adapters.CustomSpinnerAdapter;
import com.quadrobay.qbayApps.toilet.Adapters.ImageListAdapter;
import com.quadrobay.qbayApps.toilet.R;
import com.quadrobay.qbayApps.toilet.Singleton.AppController;
import com.quadrobay.qbayApps.toilet.Singleton.ConnectivityReceiver;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

/**
 * Created by sairaj on 29/11/17.
 */

public class ToiletInfoClass extends FragmentActivity implements View.OnTouchListener,View.OnClickListener,AdapterView.OnItemSelectedListener,ConnectivityReceiver.ConnectivityReceiverListener {
    Button InternetCheck;
    Context context = this;
    ImageView toolbarLeftLogo, toolbarRightLogo,firststarImageview,secondstarImageview,thirdstarImageview,fourthstarImageview,fifthstarImageview,imageView1,imageView2,imageView3,leftImageView,rightImageView;
    TextView toolbarTextview,noimagetext,genderTextview,placeTextview,hygenicTextview,disableTextview,addressTextview,internetconnction;
    EditText editText;
    RatingBar ratingBar;
    Button donebtn,cancelbtn,reportdonebtn,reportcancelbtn;
    Spinner reportSpinner,correctSpinner;
    RelativeLayout reportLayout,commentLayout;
    RecyclerView recyclerView,recyclerCommant;
    LinearLayoutManager commantlayoutManager;
    ArrayList<String> imageArrayList = new ArrayList<>();
    ArrayList<String> commentList = new ArrayList<>();
    ArrayList<String> reportList = new ArrayList<>();
    ArrayList<String> correctList = new ArrayList<>();
    ImageListAdapter imageListAdapter;
    CommantListAdapter commantListAdapter;
    CustomSpinnerAdapter<String> reportListAdapter,correctListAdapter;
    String commentpath = "",comments = "",Toilet_ID,reportString,itemString = "",loadingdialog = "";
    Dialog dialog;
    LinearLayout linearLayout,linearImage;
    int rating = 1;
    int addrating = 0;
    android.support.v7.app.AlertDialog alertdialog;
    android.support.v7.app.AlertDialog.Builder dialogBuilder;
    Boolean internetConnectivity;
    CustomReceiver customReceiver;
    IntentFilter intentFilter;
    Button internetRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_toilet);

        toolbarTextview = (TextView) findViewById(R.id.toolbar_header);
        toolbarLeftLogo = (ImageView) findViewById(R.id.toolbar_logo);
        toolbarRightLogo = (ImageView) findViewById(R.id.toolbar_right_logo);
        //recyclerView = (RecyclerView) findViewById(R.id.recyclerImage);
        recyclerCommant = (RecyclerView) findViewById(R.id.recyclerComments);
        noimagetext = (TextView) findViewById(R.id.text2);
        genderTextview = (TextView) findViewById(R.id.price_text);
        hygenicTextview = (TextView) findViewById(R.id.higenic_text);
        placeTextview = (TextView) findViewById(R.id.place_text);
        disableTextview = (TextView) findViewById(R.id.disabled_text);
        addressTextview = (TextView) findViewById(R.id.address_text);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        linearImage = (LinearLayout) findViewById(R.id.image_list);
        imageView1 = (ImageView) findViewById(R.id.image_view_1);
        imageView2 = (ImageView) findViewById(R.id.image_view_2);
        imageView3 = (ImageView) findViewById(R.id.image_view_3);
        leftImageView = (ImageView) findViewById(R.id.LeftComent);
        rightImageView = (ImageView) findViewById(R.id.RightComent);

//        addbutton = (Button) findViewById(R.id.info_add_button);
//        reportbutton = (Button) findViewById(R.id.report_button);
        reportLayout = (RelativeLayout) findViewById(R.id.report_button_view);
        commentLayout = (RelativeLayout) findViewById(R.id.comment_button_view);
        //recyclerView.setHasFixedSize(true);


        //RecyclerView.LayoutManager imagelayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        commantlayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        //recyclerView.setLayoutManager(imagelayoutManager);

        commantlayoutManager.setReverseLayout(true);
        commantlayoutManager.setStackFromEnd(true);

        recyclerCommant.setLayoutManager(commantlayoutManager);
        toolbarLeftLogo.setImageResource(R.drawable.leftarrow);
        toolbarRightLogo.setVisibility(View.VISIBLE);
        toolbarTextview.setText("Toilet Info");
        toolbarLeftLogo.setOnTouchListener(this);
//        addbutton.setOnClickListener(this);
//        reportbutton.setOnClickListener(this);
        reportLayout.setOnClickListener(this);
        commentLayout.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentFilter = new IntentFilter();
            intentFilter.addAction(CONNECTIVITY_ACTION);
            customReceiver = new CustomReceiver();
        }

        Intent intent = getIntent();
        if (intent != null){
            Toilet_ID = intent.getStringExtra("Toilet_ID");
            String image1 = intent.getStringExtra("Image_1");
            String image2 = intent.getStringExtra("Image_2");
            String image3 = intent.getStringExtra("Image_3");
//            imageArrayList.add(image1);
//            imageArrayList.add(image2);
//            imageArrayList.add(image3);

            if (image1.equals("") && image2.equals("") && image3.equals("")){
                linearImage.setVisibility(View.VISIBLE);
                //noimagetext.setVisibility(View.VISIBLE);
                //recyclerView.setVisibility(View.GONE);
                //linearImage.setVisibility(View.GONE);
            }else{
//                imageListAdapter = new ImageListAdapter(context,imageArrayList);
//                recyclerView.setAdapter(imageListAdapter);
//                recyclerView.setVisibility(View.VISIBLE);
                if (!image1.equals("")){
                    Picasso.with(context)
                            .load("http://quadrobay.co.in/Toilet_App/public/"+image1)
                            .placeholder(R.drawable.notavailablegray)
                            .into(imageView1);
                    imageView1.setOnClickListener(this);
                }

                if (!image2.equals("")){
                    Picasso.with(context)
                            .load("http://quadrobay.co.in/Toilet_App/public/"+image2)
                            .placeholder(R.drawable.notavailablegray)
                            .into(imageView2);
                    imageView2.setOnClickListener(this);
                }
                if (!image3.equals("")){
                    Picasso.with(context)
                            .load("http://quadrobay.co.in/Toilet_App/public/"+image3)
                            .placeholder(R.drawable.notavailablegray)
                            .into(imageView3);
                    imageView3.setOnClickListener(this);
                }
            }

            commentpath = intent.getStringExtra("comment");
            Log.e("comment",commentpath);
            JSONArray jsonArray = null;
            commentList.clear();
            try {
                jsonArray = new JSONArray(commentpath);
                if (jsonArray != null && jsonArray.length() > 0){
                    for (int index = 0; index < jsonArray.length(); index++) {
                        JSONObject single_obj = jsonArray.getJSONObject(index);
                        if (!single_obj.getString("Command").equals("")) {
                            commentList.add(single_obj.getString("Command"));
                        }
                        addrating += single_obj.getInt("Rating");
                    }
                    Float totalrating = (float)addrating/(float)jsonArray.length();
                    ratingBar.setRating(totalrating);
                    Log.e("commant size", String.valueOf(commentList.size()));
                    if (commentList.size() == 0){
                        commentList.add("No Comments");
                    }
                    commantListAdapter = new CommantListAdapter(context,commentList);
                    recyclerCommant.setAdapter(commantListAdapter);
                }else {
                    commentList.add("No Comments");
                    commantListAdapter = new CommantListAdapter(context,commentList);
                    recyclerCommant.setAdapter(commantListAdapter);
                }


                recyclerCommant.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        int review_position = commantlayoutManager.findFirstVisibleItemPosition();

                        if (review_position == 0){
                            rightImageView.setVisibility(View.INVISIBLE);
                            leftImageView.setVisibility(View.VISIBLE);
                        }else if (review_position == commentList.size()-1){
                            rightImageView.setVisibility(View.VISIBLE);
                            leftImageView.setVisibility(View.INVISIBLE);
                        }else {
                            rightImageView.setVisibility(View.VISIBLE);
                            leftImageView.setVisibility(View.VISIBLE);
                        }

                    }
                });



            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (!intent.getStringExtra("Gender").equals("")) {
                genderTextview.setText(intent.getStringExtra("Gender"));
            }
            if (!intent.getStringExtra("Higenic").equals("")) {
                hygenicTextview.setText(intent.getStringExtra("Higenic"));
            }
            if (!intent.getStringExtra("Place").equals("")) {
                placeTextview.setText(intent.getStringExtra("Place"));
            }
            if (!intent.getStringExtra("Disabled_Access").equals("")) {
                disableTextview.setText(intent.getStringExtra("Disabled_Access"));
            }
            if (!intent.getStringExtra("Address").equals("")) {
                addressTextview.setText(intent.getStringExtra("Address"));
            }

        }

    }

    private void ShowProgressDialog() {

        dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.loading_progress_layout, null);

        internetRetry=(Button) dialogView.findViewById(R.id.internetRetry);









        internetconnction = (TextView) dialogView.findViewById(R.id.internet_text);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        alertdialog = dialogBuilder.create();

        if (!internetConnectivity){
            internetconnction.setVisibility(View.VISIBLE);

        }


        alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        alertdialog.show();


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

    private void commantDialogBox() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_commant_layout);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        donebtn = (Button) dialog.findViewById(R.id.done_btn);
        cancelbtn = (Button) dialog.findViewById(R.id.cancel_btn);
        firststarImageview = (ImageView) dialog.findViewById(R.id.first_star);
        secondstarImageview = (ImageView) dialog.findViewById(R.id.second_star);
        thirdstarImageview = (ImageView) dialog.findViewById(R.id.third_star);
        fourthstarImageview = (ImageView) dialog.findViewById(R.id.fourth_star);
        fifthstarImageview = (ImageView) dialog.findViewById(R.id.fifth_star);
        editText = (EditText) dialog.findViewById(R.id.edit_text);
        firststarImageview.setOnClickListener(this);
        secondstarImageview.setOnClickListener(this);
        thirdstarImageview.setOnClickListener(this);
        fourthstarImageview.setOnClickListener(this);
        fifthstarImageview.setOnClickListener(this);
        donebtn.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);
        dialog.show();
    }

    private void reportDialogBox() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_report_layout);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        reportcancelbtn = (Button) dialog.findViewById(R.id.report_cancel_btn);
        reportdonebtn = (Button) dialog.findViewById(R.id.report_done_btn);
        reportSpinner = (Spinner) dialog.findViewById(R.id.report_spinner);
        linearLayout = (LinearLayout) dialog.findViewById(R.id.corrct_info_layout);
        correctSpinner = (Spinner) dialog.findViewById(R.id.correct_spinner);
        reportdonebtn.setOnClickListener(this);
        reportcancelbtn.setOnClickListener(this);
        reportSpinner.setOnItemSelectedListener(this);
        correctSpinner.setOnItemSelectedListener(this);

        reportList.clear();
        reportList.add("No Toilet");
        reportList.add("Wrong Place");
        reportList.add("Wrong Gender");
        reportList.add("Wrong Disabled Access");
        reportList.add("Wrong Hygenic");

        reportListAdapter = new CustomSpinnerAdapter<String>(context,R.layout.custom_spinner_item);
        reportListAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        reportListAdapter.add("Report");
        reportListAdapter.addAll(reportList);
        reportSpinner.setAdapter(reportListAdapter);
        reportSpinner.setSelection(0);

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.info_add_button:
//                commantDialogBox();
//                break;
//            case R.id.report_button:
//                reportDialogBox();
//                break;
            case R.id.report_button_view:
                reportDialogBox();
                break;
            case R.id.comment_button_view:
                commantDialogBox();
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
            case R.id.done_btn:
                comments = editText.getText().toString();
                Log.e("comment",comments);
                loadingdialog = "Comments";
                newCommentRationg();
                //dialog.dismiss();
                break;
            case R.id.cancel_btn:
                dialog.dismiss();
                break;
            case R.id.report_cancel_btn:
                dialog.dismiss();
                break;
            case R.id.report_done_btn:
                dialog.dismiss();
                internetConnectivity = checkConnection();
                loadingdialog = "Reports";
                if (!internetConnectivity){
                    ShowProgressDialog();
                }else {
                    reportPostRequest();
                }
                break;
            case R.id.image_view_1:
                previewImage(imageView1);
                break;
            case R.id.image_view_2:
                previewImage(imageView2);
                break;
            case R.id.image_view_3:
                previewImage(imageView3);
                break;
        }
    }

    private void previewImage(ImageView iView) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.image_preview, null);
        final ImageView prvimage = (ImageView) dialogView.findViewById(R.id.preview_image);
        prvimage.setImageDrawable(iView.getDrawable());
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Image preview");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

                //prvimage.set
                dialog.cancel();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();

    }

    private void reportPostRequest() {

        if (reportString != null && Toilet_ID != null){

            JSONObject finalobj = new JSONObject();

            try {
                finalobj.put("Report",reportString);
                finalobj.put("Item",itemString);
                finalobj.put("Toilet_ID",Toilet_ID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int SocketTimeout = 30000;
            RetryPolicy retry = new DefaultRetryPolicy(SocketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            );
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://quadrobay.co.in/Toilet_App/public/api/showroom/add-report", finalobj,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("Server Res", response.toString());
                            Toast.makeText(getApplicationContext(), "Report Added Successfully", Toast.LENGTH_SHORT).show();
                        }
                    },

                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Server err", error.toString());
                            Toast.makeText(getApplicationContext(), "Error Adding Report", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }
            );

            request.setRetryPolicy(retry);
            AppController.getInstance().addToRequestQueue(request, "json_req_method");
        }
    }

    private void newCommentRationg() {
        if (comments.equals("")){
            Toast.makeText(getApplicationContext(), "Please Comment and Rate this Toilet", Toast.LENGTH_SHORT).show();
        }else {
            Log.e("rating", String.valueOf(rating));
            int sum = addrating + rating;
            Float totalrating = (float)sum/2;
            Log.e("total_Rating", String.valueOf(totalrating));
            ratingBar.setRating(totalrating);
            Log.e("comment",comments);
            if (commentList.get(0) == "No Comments"){
                commentList.clear();
                commentList.add(comments);
            }else {
                commentList.add(comments);
            }
            commantListAdapter = new CommantListAdapter(context,commentList);
            recyclerCommant.setAdapter(commantListAdapter);
            dialog.dismiss();
            internetConnectivity = checkConnection();
            if (!internetConnectivity){
                ShowProgressDialog();
            }else {
                postRequest();
            }

        }
    }

    private void postRequest() {
        JSONObject finalobj = new JSONObject();
        if (Toilet_ID != null) {
            try {
                finalobj.put("Toilet_ID", Toilet_ID);
                finalobj.put("Rating", rating);
                finalobj.put("Comment", comments);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int SocketTimeout = 30000;
            RetryPolicy retry = new DefaultRetryPolicy(SocketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            );
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://quadrobay.co.in/Toilet_App/public/api/showroom/add-toilet-comment", finalobj,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("Server Res", response.toString());
                            Toast.makeText(getApplicationContext(), "Added Rate and Comment Successfully", Toast.LENGTH_SHORT).show();
                        }
                    },

                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Server err", error.toString());
                            Toast.makeText(getApplicationContext(), "Error comment and Rate", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }


                    }

            );

            request.setRetryPolicy(retry);
            AppController.getInstance().addToRequestQueue(request, "json_req_method");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        int sp_id = spinner.getId();
        switch (sp_id){
            case R.id.report_spinner:
                if (i != 0){
                   reportString = reportList.get(i-1);
                    Log.e("Report",reportString);
                    if (reportString.equals("Wrong Place")){

                        correctList.clear();
                        correctList.add("Restaurant");
                        correctList.add("Public");
                        correctList.add("Shopping center");
                        correctList.add("Gas station");

                        correctListAdapter = new CustomSpinnerAdapter<String>(context,R.layout.custom_spinner_item);
                        correctListAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
                        correctListAdapter.add("Info");
                        correctListAdapter.addAll(correctList);
                        correctSpinner.setAdapter(correctListAdapter);
                        correctSpinner.setSelection(0);

                        linearLayout.setVisibility(View.VISIBLE);

                    }else if (reportString.equals("Wrong Gender")){

                        correctList.clear();
                        correctList.add("Male");
                        correctList.add("Female");
                        correctList.add("Both");

                        correctListAdapter = new CustomSpinnerAdapter<String>(context,R.layout.custom_spinner_item);
                        correctListAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
                        correctListAdapter.add("Info");
                        correctListAdapter.addAll(correctList);
                        correctSpinner.setAdapter(correctListAdapter);
                        correctSpinner.setSelection(0);

                        linearLayout.setVisibility(View.VISIBLE);

                    } else if (reportString.equals("Wrong Disabled Access")){

                        correctList.clear();
                        correctList.add("Yes");
                        correctList.add("No");

                        correctListAdapter = new CustomSpinnerAdapter<String>(context,R.layout.custom_spinner_item);
                        correctListAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
                        correctListAdapter.add("Info");
                        correctListAdapter.addAll(correctList);
                        correctSpinner.setAdapter(correctListAdapter);
                        correctSpinner.setSelection(0);

                        linearLayout.setVisibility(View.VISIBLE);

                    }else if (reportString.equals("Wrong Hygenic")){

                        correctList.clear();
                        correctList.add("Free");
                        correctList.add("Pay");

                        correctListAdapter = new CustomSpinnerAdapter<String>(context,R.layout.custom_spinner_item);
                        correctListAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
                        correctListAdapter.add("Info");
                        correctListAdapter.addAll(correctList);
                        correctSpinner.setAdapter(correctListAdapter);
                        correctSpinner.setSelection(0);

                        linearLayout.setVisibility(View.VISIBLE);

                    }else if (reportString.equals("No Toilet")){
                        linearLayout.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.correct_spinner:
                if (i != 0) {
                    Log.e("Info",correctList.get(i-1));
                    itemString = correctList.get(i-1);
                }
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
    }

    @Override
    public void onStop() {
        super.onStop();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            unregisterReceiver(customReceiver);
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
        if (isConnected){
            if (!loadingdialog.equals("")){
                if (loadingdialog.equals("Comments")){
                    alertdialog.dismiss();
                    postRequest();
                }else if (loadingdialog.equals("Reports")){
                    alertdialog.dismiss();
                    reportPostRequest();
                }
            }
        }
    }
}
