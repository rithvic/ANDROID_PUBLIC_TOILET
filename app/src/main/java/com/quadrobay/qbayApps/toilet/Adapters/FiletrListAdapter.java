package com.quadrobay.qbayApps.toilet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

import com.quadrobay.qbayApps.toilet.Activitys.FeedbackInfoClass;
import com.quadrobay.qbayApps.toilet.R;

/**
 * Created by sairaj on 04/01/18.
 */

public class FiletrListAdapter extends RecyclerView.Adapter<FiletrListAdapter.ViewHolder> {

    private Context context;
    ArrayList<String> Device_ID = new ArrayList<>();
    ArrayList<String> Screen = new ArrayList<>();
    ArrayList<String> Summary = new ArrayList<>();
    ArrayList<String> Screenshot= new ArrayList<>();
    ArrayList<String> Suggestion = new ArrayList<>();
    ArrayList<String> Date = new ArrayList<>();
    private int lastPosition = -1;

    public FiletrListAdapter(Context context, ArrayList<String> device_id, ArrayList<String> screen, ArrayList<String> summary, ArrayList<String> suggestion, ArrayList<String> screenshot, ArrayList<String> date) {

        this.context = context;
        this.Device_ID = device_id;
        this.Screen = screen;
        this.Summary = summary;
        this.Screenshot = screenshot;
        this.Suggestion = suggestion;
        this.Date = date;

    }

    @Override
    public FiletrListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FiletrListAdapter.ViewHolder holder, int position) {
        holder.addresstextView.setText("Updated on: " + Date.get(position));
        setAnimation(holder.itemView, position);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    //Toast.makeText(context, "#" + position + " - " + alName.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(context, FeedbackInfoClass.class);
                    intent.putExtra("Device_ID",Device_ID.get(position));
                    intent.putExtra("Screen",Screen.get(position));
                    intent.putExtra("Summery",Summary.get(position));
                    intent.putExtra("Screenshot",Screenshot.get(position));
                    intent.putExtra("Suggestion",Suggestion.get(position));
                    intent.putExtra("Date",Date.get(position));
                    context.startActivity(intent);
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
    @Override
    public int getItemCount() {
        return Device_ID.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView addresstextView;
        private ItemClickListener clickListener;

        public ViewHolder(View view) {
            super(view);

            addresstextView = (TextView) view.findViewById(R.id.feedlist_item);
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
