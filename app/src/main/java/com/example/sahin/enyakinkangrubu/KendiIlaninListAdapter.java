package com.example.sahin.enyakinkangrubu;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by sahin on 17.12.2017.
 */

public class KendiIlaninListAdapter extends ArrayAdapter<KendiIlaninList> {

    private static final String TAG = "KendiIlaninListAdapter";

    private Context mContext;
    int mResource;
    /**
     * Default constructor for the KendiIlaninListAdapter
     * @param context
     * @param resource
     * @param objects
     */

    public KendiIlaninListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<KendiIlaninList> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final String Id =getItem(position).getId();
        final String UserId=getItem(position).getUserId();
        final String HospitalName=getItem(position).getHospitalName();
        final String Declaration=getItem(position).getDeclaration();
        final String Date=getItem(position).getDate();
        final String Longitude =getItem(position).getLongitude();
        final String Latitude =getItem(position).getLatitude();
        final String UserName =getItem(position).getUserName();
        final String UserDate=getItem(position).getUserDate();
        final String UserPhone=getItem(position).getUserPhone();
        final String GenderName =getItem(position).getGenderName();
        final String BloodName =getItem(position).getBloodName();

        String[] tumtarih = Date.split(" ");
        String saat = tumtarih[1];
        String tarih = tumtarih[0];
        String[] dtarih = tarih.split("-");
        String yil = dtarih[0];
        String ay = dtarih[1];
        String gun = dtarih[2];

        KendiIlaninList kan = new KendiIlaninList(Id,UserId,HospitalName,Declaration,Date,Longitude,Latitude,UserName,UserDate,UserPhone,GenderName,BloodName);

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource,parent,false);

        TextView txthastaneadi = (TextView)convertView.findViewById(R.id.kendiilanhastane);
        TextView txtkangrubu = (TextView)convertView.findViewById(R.id.kendiilankangrubu);
        TextView txtaciklama = (TextView)convertView.findViewById(R.id.kendiilanaciklama);
        TextView txttarih = (TextView)convertView.findViewById(R.id.kendiilantarih);
        TextView txtadres = (TextView)convertView.findViewById(R.id.kendiilanadres);

        txthastaneadi.setText(HospitalName);
        txtkangrubu.setText(BloodName);
        txtaciklama.setText("Açıklama : "+Declaration);
        txttarih.setText(gun+"."+ay+"."+yil);
        txtadres.setText("Adres : "+adres_ata(Latitude,Longitude));


        return convertView;
    }
    private String adres_ata(String Latitude,String Longitude){
        String Adres="";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(mContext, Locale.getDefault());

        addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(Latitude),Double.parseDouble(Longitude),1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        int N = addresses.get(0).getMaxAddressLineIndex();
        for(int i=0;i<=N;i++)
            Adres+=" "+addresses.get(0).getAddressLine(i);

        return Adres;
    }

}
