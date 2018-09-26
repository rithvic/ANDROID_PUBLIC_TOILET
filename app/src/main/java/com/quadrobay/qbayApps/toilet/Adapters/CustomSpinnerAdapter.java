package com.quadrobay.qbayApps.toilet.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.quadrobay.qbayApps.toilet.R;

/**
 * Created by sairaj on 20/11/17.
 */

public class CustomSpinnerAdapter<T> extends ArrayAdapter<String> {

    public CustomSpinnerAdapter(Context context, int resource) {
        super(context, resource);
        // TODO Auto-generated constructor stub
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = super.getView(position, convertView, parent);

        if (position == 0) {

            ((TextView) v.findViewById(R.id.tv_custom_spinner_item)).setText("");
            ((TextView) v.findViewById(R.id.tv_custom_spinner_item)).setTextSize(18);
            ((TextView) v.findViewById(R.id.tv_custom_spinner_item)).setHint(getItem(0));
        }
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = null;
        if (position == 0) {

            TextView tv = new TextView(getContext());
            tv.setHeight(0);
            v = tv;
        } else {
            v = super.getDropDownView(position, null, parent);
        }
        return v;
    }
}
