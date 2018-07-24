package com.example.sahin.enyakinkangrubu;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.sahin.enyakinkangrubu.Info.GoogleInfo;

import java.util.ArrayList;
import java.util.jar.Attributes;

/**
 * Created by sahin on 15.12.2017.
 */

public class OdaListAdapter extends ArrayAdapter<OdaList> {
    private static final String TAG = "OdaListAdapter";

    private Context mContext;
    int mResource;

    /**
     * Default constructor for the KanIlanListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public OdaListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<OdaList> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String NameSurname = getItem(position).getNameSurname();
        String ImgUrl = getItem(position).getImgUrl();


        OdaList oda = new OdaList(NameSurname,ImgUrl);

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource,parent,false);

        TextView tvName = (TextView)convertView.findViewById(R.id.textName);

        final ImageView imageView = (ImageView)convertView.findViewById(R.id.imagePic);


        tvName.setText(NameSurname);
        Glide.with(Mesajlarim.context).load(ImgUrl).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(Mesajlarim.context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
        return convertView;
    }

}
