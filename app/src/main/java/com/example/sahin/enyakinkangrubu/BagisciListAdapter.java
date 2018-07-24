package com.example.sahin.enyakinkangrubu;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.sahin.enyakinkangrubu.ButtonEffect.Effect;
import com.example.sahin.enyakinkangrubu.Info.BagisAyrinti;

import java.util.ArrayList;

/**
 * Created by sahin on 17.12.2017.
 */

public class BagisciListAdapter extends ArrayAdapter<BagisciList> {
    private static final String TAG = "KanIlanListAdapter";

    private Context mContext;
    int mResource;

    /**
     * Default constructor for the KanIlanListAdapter
     * @param context
     * @param resource
     * @param objects
     */

    public BagisciListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<BagisciList> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {




        final String UserId=getItem(position).getUserId();
        final String Name=getItem(position).getName();
        final String BloodName=getItem(position).getBloodName();
        final String Chatizin=getItem(position).getChatizin();
        final String Aramaizin=getItem(position).getAramaizin();
        final String Smsizin=getItem(position).getSmsizin();
        final String Longitude=getItem(position).getLongitude();
        final String Latitude=getItem(position).getLatitude();
        final String UserPhone=getItem(position).getUserPhone();
        final String GenderName=getItem(position).getGenderName();
        final String Uzaklik=getItem(position).getUzaklik();
        String Url=getItem(position).getUrl();


        BagisciList kan = new BagisciList(UserId,Name,BloodName,Chatizin,Aramaizin,Smsizin,Longitude,Latitude,UserPhone,GenderName,Uzaklik,Url);

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource,parent,false);

        Button btnchat = (Button)convertView.findViewById(R.id.bagiscichat);
        Button btnara= (Button)convertView.findViewById(R.id.bagisciara);
        Button btnsms= (Button)convertView.findViewById(R.id.bagiscisms);
        Button btnkonum= (Button)convertView.findViewById(R.id.bagiscikonum);

        TextView tvBagisciAd = (TextView)convertView.findViewById(R.id.bagisciisim);
        TextView tvKanGrubu = (TextView)convertView.findViewById(R.id.bagiscikangrubu);
        TextView tvBagisciUzaklik = (TextView)convertView.findViewById(R.id.bagisciuzaklik);
        final ImageView imageView = (ImageView)convertView.findViewById(R.id.imgpic);

        tvBagisciAd.setText(Name);
        tvKanGrubu.setText(BloodName);
        tvBagisciUzaklik.setText(Uzaklik+" KM");

        Glide.with(BagiscilaraBak.context).load(Url).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(BagiscilaraBak.context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });

        if(Chatizin.equals("0"))
            btnchat.setVisibility(View.GONE);
        if(Aramaizin.equals("0"))
            btnara.setVisibility(View.GONE);
        if(Smsizin.equals("0"))
            btnsms.setVisibility(View.GONE);


        Effect.buttonEffectmavi(btnchat);
        Effect.buttonEffectmavi(btnara);
        Effect.buttonEffectmavi(btnkonum);
        Effect.buttonEffectmavi(btnsms);

        btnkonum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BagiscilaraBak.context,BagisAyrinti.class);
                intent.putExtra("UserId",UserId);
                intent.putExtra("Username",Name);
                intent.putExtra("UserPhone",UserPhone);
                intent.putExtra("BloodName",BloodName);
                intent.putExtra("GenderName",GenderName);
                intent.putExtra("Smsizin",Smsizin);
                intent.putExtra("Aramaizin",Aramaizin);
                intent.putExtra("Chatizin",Chatizin);
                intent.putExtra("Latitude",Latitude);
                intent.putExtra("Longitude",Longitude);
                intent.putExtra("Uzaklik",Uzaklik);
                mContext.startActivity(intent);
            }
        });

        btnsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + "+90" + UserPhone));
                intent.putExtra("sms_body", "Acil " + BloodName + " Kana İhtiyacım Var");
                mContext.startActivity(intent);
            }
        });

        btnara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:+90" + UserPhone));
                if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mContext.startActivity(callIntent);

            }
        });




        btnchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BagiscilaraBak.context, Chat.class);
                i.putExtra("UserId", UserId);
                i.putExtra("bilgi","bagisayrinti");
                mContext.startActivity(i);
            }
        });



        return convertView;

    }




}
