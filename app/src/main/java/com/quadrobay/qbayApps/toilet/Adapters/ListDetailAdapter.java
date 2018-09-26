package com.quadrobay.qbayApps.toilet.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RatingBar;
import android.widget.TextView;

import com.quadrobay.qbayApps.toilet.Activitys.ToiletInfoClass;
import com.quadrobay.qbayApps.toilet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sairaj on 25/11/17.
 */

public class ListDetailAdapter extends RecyclerView.Adapter<ListDetailAdapter.ViewHolder> {
    private Context context;
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
    private int lastPosition = -1;

    public ListDetailAdapter(Context context, ArrayList<String> toiletID, ArrayList<Double> latitude, ArrayList<Double> longtitude, ArrayList<String> gender, ArrayList<String> hygenic, ArrayList<String> place, ArrayList<String> rating, ArrayList<String> disabledaccess, ArrayList<String> cost, ArrayList<String> address, ArrayList<String> firstimage, ArrayList<String> secondimage, ArrayList<String> thirdimage, ArrayList<JSONArray> commentarray) {
        this.context = context;
        this.ToiletID = toiletID;
        this.Latitude = latitude;
        this.Longtitude = longtitude;
        this.Gender = gender;
        this.Hygenic = hygenic;
        this.Place = place;
        this.Rating = rating;
        this.Disabledaccess = disabledaccess;
        this.Cost = cost;
        this.Address = address;
        this.First_Image = firstimage;
        this.Second_Image = secondimage;
        this.Third_Image = thirdimage;
        this.Comment_Array = commentarray;
    }

    @Override
    public ListDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(!Address.get(position).equals("")&&!Address.get(position).equals(null)) {
            holder.addresstextView.setText(Address.get(position));
        }
        int addrating = 0;
        try {
            if (Comment_Array.get(position) != null && Comment_Array.get(position).length() > 0){
                for (int index = 0; index < Comment_Array.get(position).length(); index++) {
                    JSONObject single_obj = Comment_Array.get(position).getJSONObject(index);
                    addrating += single_obj.getInt("Rating");
                }
                Float totalrating = (float)addrating/(float)Comment_Array.get(position).length();
                holder.ratingBar.setRating(totalrating);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setAnimation(holder.itemView, position);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    //Toast.makeText(context, "#" + position + " - " + alName.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
                } else {
                    Intent infointent = new Intent(context, ToiletInfoClass.class);
                    infointent.putExtra("Toilet_ID", ToiletID.get(position));
                    infointent.putExtra("Gender", Gender.get(position));
                    infointent.putExtra("Higenic", Hygenic.get(position));
                    infointent.putExtra("Place", Place.get(position));
                    infointent.putExtra("Rating", Rating.get(position));
                    infointent.putExtra("Disabled_Access", Disabledaccess.get(position));
                    infointent.putExtra("Address", Address.get(position));
                    infointent.putExtra("Image_1", First_Image.get(position));
                    infointent.putExtra("Image_2", Second_Image.get(position));
                    infointent.putExtra("Image_3", Third_Image.get(position));
                    infointent.putExtra("comment",Comment_Array.get(position).toString());
                    context.startActivity(infointent);
                    ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });

    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    public int getItemCount() {
        return Address.size();
    }







    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView addresstextView;
        public RatingBar ratingBar;
        private ItemClickListener clickListener;

        public ViewHolder(View view) {
            super(view);

            addresstextView = (TextView) view.findViewById(R.id.address);
            ratingBar = (RatingBar) view.findViewById(R.id.rating_stars);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getAdapterPosition(), true);
            return true;
        }
    }
}
