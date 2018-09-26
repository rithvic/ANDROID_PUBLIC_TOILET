package com.quadrobay.qbayApps.toilet.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.quadrobay.qbayApps.toilet.R;

/**
 * Created by sairaj on 13/03/18.
 */

public class InviteFragmenet extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.invite_layout_activity, container, false);

        Button but=(Button) view.findViewById(R.id.but1);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent inten1=new Intent(android.content.Intent.ACTION_SEND);
                inten1.setType("text/plain");
                String shareBodyText = "Check it out. Your message goes here";
                inten1.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject here");
                inten1.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(inten1, "Shearing Option"));

               return;

            }
        });

        return view;
    }


    }