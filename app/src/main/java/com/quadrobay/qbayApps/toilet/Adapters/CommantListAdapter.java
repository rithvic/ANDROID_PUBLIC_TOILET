package com.quadrobay.qbayApps.toilet.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quadrobay.qbayApps.toilet.R;
import com.quadrobay.qbayApps.toilet.Singleton.OnBottomReachedListener;

import java.util.ArrayList;

/**
 * Created by sairaj on 02/12/17.
 */

public class CommantListAdapter extends RecyclerView.Adapter<CommantListAdapter.ViewHolder> {

    private Context context;
    ArrayList<String> commentList = new ArrayList<>();
    OnBottomReachedListener onBottomReachedListener;

    public CommantListAdapter(Context context, ArrayList<String> commentList) {
        this.context = context;
        this.commentList = commentList;

    }

    @Override
    public CommantListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.commant_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        holder.textView.setText(commentList.get(position));
    }

    public int getItemCount() {
        return commentList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.commant_list);
        }
    }


}
