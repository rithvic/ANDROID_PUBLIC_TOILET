package com.quadrobay.qbayApps.toilet.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quadrobay.qbayApps.toilet.R;

/**
 * Created by sairaj on 17/11/17.
 */

public class MenuAdpter extends BaseAdapter {

    Context con;
    String[] title;
    Typeface helvatica;
    Integer[] icon;
    public MenuAdpter(Context c, String[] tit, Typeface hel, Integer[] icons) {
        this.con = c;
        this.title = tit;
        this.helvatica=hel;
        this.icon = icons;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return title.length;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return title[arg0];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = convertView;
        view = ((Activity) con).getLayoutInflater().inflate(
                R.layout.slidingmainmenu, parent, false);

        TextView txtTitle = (TextView) view.findViewById(R.id.title);
        ImageView imageView = (ImageView) view.findViewById(R.id.title_icon);
        imageView.setImageResource(icon[position]);
        txtTitle.setText(title[position]);
        txtTitle.setTypeface(helvatica);
        return view;
    }
}

