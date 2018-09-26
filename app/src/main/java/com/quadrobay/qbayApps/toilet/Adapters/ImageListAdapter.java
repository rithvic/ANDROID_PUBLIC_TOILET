package com.quadrobay.qbayApps.toilet.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.quadrobay.qbayApps.toilet.R;

/**
 * Created by sairaj on 29/11/17.
 */

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder>{
    private Context context;
    ArrayList<String> ImageList = new ArrayList<>();

    public ImageListAdapter(Context context, ArrayList<String> imageArrayList) {
        this.context = context;
        this.ImageList = imageArrayList;
    }

    @Override
    public ImageListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Picasso.with(context)
                .load("http://quadrobay.co.in/Toilet_App/public/"+ImageList.get(position))
                .placeholder(R.mipmap.appic)
                .into(holder.imageView);
    }


    public int getItemCount() {
        return ImageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_src);
        }
    }
}
