package com.quadrobay.qbayApps.toilet.Activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import com.quadrobay.qbayApps.toilet.R;

/**
 * Created by sairaj on 04/01/18.
 */

public class FeedbackInfoClass extends FragmentActivity implements View.OnTouchListener{

    Context context = this;
    ImageView toolbarLeftLogo, toolbarRightLogo, bannerImageview;
    TextView toolbarTextview,screenText,summaryText,suggestionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_info);

        toolbarTextview = (TextView) findViewById(R.id.toolbar_header);
        toolbarLeftLogo = (ImageView) findViewById(R.id.toolbar_logo);
        toolbarRightLogo = (ImageView) findViewById(R.id.toolbar_right_logo);
        toolbarLeftLogo.setImageResource(R.drawable.leftarrow);
        toolbarRightLogo.setVisibility(View.INVISIBLE);
        toolbarTextview.setText("Feedback Info");

        bannerImageview = (ImageView) findViewById(R.id.feed_image);
        screenText = (TextView) findViewById(R.id.screen_text);
        summaryText = (TextView) findViewById(R.id.summary_text);
        suggestionText = (TextView) findViewById(R.id.suggestion_text);

        toolbarLeftLogo.setOnTouchListener(this);

        Intent intent = getIntent();

        if (intent != null){
            if (!intent.getStringExtra("Screenshot").equals("") && !intent.getStringExtra("Screenshot").equals(null)) {
                Picasso.with(context)
                        .load(intent.getStringExtra("Screenshot"))
                        .placeholder(R.mipmap.appic)
                        .into(bannerImageview);
            }else{
                Picasso.with(context)
                        .load(R.drawable.notavailablegray)
                        .into(bannerImageview);
            }
            screenText.setText(intent.getStringExtra("Screen"));
            summaryText.setText(intent.getStringExtra("Summery"));
            suggestionText.setText(intent.getStringExtra("Suggestion"));
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
}
