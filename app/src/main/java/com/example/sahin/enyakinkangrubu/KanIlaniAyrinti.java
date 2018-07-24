package com.example.sahin.enyakinkangrubu;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.media.midi.MidiManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahin.enyakinkangrubu.ButtonEffect.Effect;
import com.example.sahin.enyakinkangrubu.Info.BloodInfo;
import com.example.sahin.enyakinkangrubu.Info.UserInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KanIlaniAyrinti extends AppCompatActivity implements OnMapReadyCallback {

    String Id,UserId,HospitalName,Declaration,Date,Longitude,Latitude,UserName,UserDate,UserPhone,GenderName,Uzaklik,BloodName;
    String Adres;
    GoogleMap googleMap;
    MapFragment mapFragment;
    Button btn_chat;
    Context context;
    private void menu_butonlari()
    {
        Button bkanariyorum=(Button) findViewById(R.id.bmenuarama);
        Button bkanbagis=(Button)findViewById(R.id.bmenubagis);
        Button bchat = (Button)findViewById(R.id.bmenuchat);
        Button bayarlar = (Button)findViewById(R.id.bmenuayarlar);
        bkanariyorum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),KanAriyorum.class);
                startActivity(i);
            }
        });
        bkanbagis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),KanBagisliyorum.class);
                startActivity(i);
            }
        });
        bchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Mesajlarim.class);
                startActivity(i);
            }
        });
        bayarlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Ayarlar.class);
                startActivity(i);
            }
        });
        Effect.buttonEffectiki(bkanariyorum);
        Effect.buttonEffectiki(bkanbagis);
        Effect.buttonEffectiki(bchat);
        Effect.buttonEffectiki(bayarlar);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kan_ilani_ayrinti);
        ChatInfo.chat_service=true;

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        context=this;
        UserInfolari_Doldur();
        ChatInfo.chat_sayfasi_acikmi=false;
        btn_chat=(Button)findViewById(R.id.button10);
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),Chat.class);
                i.putExtra("UserId",UserId);
                i.putExtra("bilgi","bagisayrinti");
                startActivity(i);

            }
        });
        Effect.buttonEffectmavi(btn_chat);

        degiskenleri_ata();
        adres_ata();
        menu_butonlari();
        TextView t = (TextView)findViewById(R.id.tadres);
        t.setText(Adres);


        //showGoogleMap(Double.parseDouble(Latitude),Double.parseDouble(Longitude),UserName);

        try {

            if (googleMap == null) {

                mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
                mapFragment.getMapAsync(this);

            }

        } catch (Exception e) {

            e.printStackTrace();
        }


        Button btnsms=(Button)findViewById(R.id.button11);
        Button btnara = (Button)findViewById(R.id.button12);
        Effect.buttonEffectmavi(btnsms);
        Effect.buttonEffectmavi(btnara);

        btnsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + "+90" + UserPhone));
                intent.putExtra("sms_body",  BloodName + " Kan Verebilirim");
                startActivity(intent);
            }
        });

        btnara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:+90" + UserPhone));
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);
            }
        });

        TextView txtAciklama = (TextView)findViewById(R.id.textView13);
        txtAciklama.setText("Açıklama : "+Declaration);

        TextView txtHospitalName = (TextView)findViewById(R.id.textView24);
        txtHospitalName.setText("Hastane Adı :"+HospitalName);

        isPermissionGranted();

        //Intent iservice = new Intent(KanIlaniAyrinti.this,Deneme_Service.class);
        //startService(iservice);

        Intent iservice = new Intent(KanIlaniAyrinti.this,ChatService.class);
        startService(iservice);


    }

    private void UserInfolari_Doldur() {
        Database db = new Database(getApplicationContext(),"",null,1);
        int userId=db.userid_getir();
        SQLiteDatabase db_oku = db.getReadableDatabase();
        String sql="select GoogleId,Name,SurName,Date,Phone,BloodName,GenderName,Mail,ImgUrl,IlanHaber FROM USERINFO where UserId="+userId+" ";
        Cursor cursor = db_oku.rawQuery(sql,null);
        while (cursor.moveToNext())
        {
            UserInfo.Id=userId;
            UserInfo.GoogleId=cursor.getString(0);
            UserInfo.Name=cursor.getString(1);
            UserInfo.Surname=cursor.getString(2);
            UserInfo.Date=cursor.getString(3);
            UserInfo.Phone=cursor.getString(4);
            UserInfo.BloodName=cursor.getString(5);
            UserInfo.GenderName=cursor.getString(6);
            UserInfo.Mail=cursor.getString(7);
            UserInfo.ImgUrl=cursor.getString(8);
            UserInfo.IlanHaber=cursor.getInt(9);
            break;
        }
        sql="select * from COORDINATES";
        cursor=db_oku.rawQuery(sql,null);
        while (cursor.moveToNext())
        {
            UserInfo.Coordinates=cursor.getString(0);
            break;
        }

        sql="select Id,Name from BLOODGROUPS";
        cursor=db_oku.rawQuery(sql,null);
        BloodInfo.listBloodGroupName=new ArrayList<>();
        BloodInfo.listBloodGroupId=new ArrayList<>();
        while (cursor.moveToNext())
        {
            BloodInfo.listBloodGroupId.add(cursor.getInt(0));
            BloodInfo.listBloodGroupName.add(cursor.getString(1));
        }

    }

    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "İzin Alındı", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "İzin Reddedildi", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }




    private void adres_ata(){
        Adres="";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(Latitude),Double.parseDouble(Longitude),1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        int N = addresses.get(0).getMaxAddressLineIndex();
        for(int i=0;i<=N;i++)
            Adres+=" "+addresses.get(0).getAddressLine(i);

    }
    private void degiskenleri_ata() {
        Id = getIntent().getExtras().getString("Id");
        UserId = getIntent().getExtras().getString("UserId");
        HospitalName = getIntent().getExtras().getString("HospitalName");
        Declaration = getIntent().getExtras().getString("Declaration");
        Date = getIntent().getExtras().getString("Date");
        Longitude = getIntent().getExtras().getString("Longitude");
        Latitude = getIntent().getExtras().getString("Latitude");
        UserName = getIntent().getExtras().getString("UserName");
        UserDate = getIntent().getExtras().getString("UserDate");
        UserPhone = getIntent().getExtras().getString("UserPhone");
        GenderName = getIntent().getExtras().getString("GenderName");
        Uzaklik = getIntent().getExtras().getString("Uzaklik");
        BloodName = getIntent().getExtras().getString("BloodName");
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng TutorialsPoint = new LatLng(Double.parseDouble(Latitude),Double.parseDouble(Longitude));
        googleMap = map;
        //Haritanın üzerinde bulunan, haritayı büyütüp küçültmek için kullanılan zooming button aktif ettim
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        //Harita üzerinde işaretlenmiş konumlara haritayı büyüterek yani zoomlama yaparak fokuslanmasını yapan kod
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude))).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //--
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //Google Map üzerinde konum işaretlemeyi sağlayan imleci olusturan kod
        //title metodu; imlec konulan yere isim vermenizi sağlar
        Marker TP = googleMap.addMarker(new MarkerOptions().position(TutorialsPoint).title(UserName));
    }
}
