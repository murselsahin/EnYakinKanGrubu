package com.example.sahin.enyakinkangrubu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahin on 9.11.2017.
 */

public class KanIlanListAdapter extends ArrayAdapter<KanIlanList> {

    private static final String TAG = "KanIlanListAdapter";

    private Context mContext;
    int mResource;

    /**
     * Default constructor for the KanIlanListAdapter
     * @param context
     * @param resource
     * @param objects
     */

    public KanIlanListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<KanIlanList> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final String Id=getItem(position).getId();
        final String UserId=getItem(position).getUserId();
        final String HospitalName=getItem(position).getHospitalName();
        final String Declaration=getItem(position).getDeclaration();
        final String Date =getItem(position).getDate();
        final String Longitude=getItem(position).getLongitude();
        final String Latitude =getItem(position).getLatitude();
        final String UserName =getItem(position).getUserName();
        final String UserDate =getItem(position).getUserDate();
        final String UserPhone=getItem(position).getUserPhone();
        final String GenderName=getItem(position).getGenderName();
        final String Uzaklik =getItem(position).getUzaklik();
        final String BloodName=getItem(position).getBloodName();
        final String ImgUrl =getItem(position).getImgUrl();

        String[] tumtarih = Date.split(" ");
        String saat = tumtarih[1];
        String tarih = tumtarih[0];
        String[] dtarih = tarih.split("-");
        String yil = dtarih[0];
        String ay = dtarih[1];
        String gun = dtarih[2];


        KanIlanList kan = new KanIlanList(Id,UserId,HospitalName,Declaration,Date,Longitude,Latitude,UserName,UserDate,UserPhone,GenderName,Uzaklik,BloodName,ImgUrl);

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

        tvBagisciAd.setText(UserName);
        tvKanGrubu.setText(gun+"."+ay+"."+yil);
        tvBagisciUzaklik.setText(Uzaklik+" KM");

        Glide.with(KanIlanlarinaGozat.context).load(ImgUrl).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(KanIlanlarinaGozat.context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });

        Effect.buttonEffectmavi(btnchat);
        Effect.buttonEffectmavi(btnara);
        Effect.buttonEffectmavi(btnkonum);
        Effect.buttonEffectmavi(btnsms);

        btnkonum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KanIlanlarinaGozat.context,KanIlaniAyrinti.class);
                intent.putExtra("Id",Id);
                intent.putExtra("UserId",UserId);
                intent.putExtra("HospitalName",HospitalName);
                intent.putExtra("Declaration",Declaration);
                intent.putExtra("Date",Date);
                intent.putExtra("Longitude",Longitude);
                intent.putExtra("Latitude",Latitude);
                intent.putExtra("UserName",UserName);
                intent.putExtra("UserDate",UserDate);
                intent.putExtra("UserPhone",UserPhone);
                intent.putExtra("GenderName",GenderName);
                intent.putExtra("Uzaklik",Uzaklik);
                intent.putExtra("BloodName",BloodName);
                mContext.startActivity(intent);
            }
        });

        btnsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + "+90" + UserPhone));
                intent.putExtra("sms_body",  BloodName + " Kan Verebilirim");
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
                Intent i = new Intent(KanIlanlarinaGozat.context, Chat.class);
                i.putExtra("UserId", UserId);
                i.putExtra("bilgi","asdasd");
                mContext.startActivity(i);
            }
        });

        return convertView;
    }
}
