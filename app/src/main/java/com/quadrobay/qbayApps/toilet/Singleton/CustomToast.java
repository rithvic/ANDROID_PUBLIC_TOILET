package com.quadrobay.qbayApps.toilet.Singleton;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.quadrobay.qbayApps.toilet.R;

/**
 * Created by sairaj on 13/12/17.
 */

public class CustomToast {

    public CustomToast(Context context, String s){

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_layout, null);

        // set a message
        TextView text = (TextView) layout.findViewById(R.id.custom_toast_message);
        text.setText(s);

        //Creating the Toast object
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(layout);
        toast.show();

    }
}
