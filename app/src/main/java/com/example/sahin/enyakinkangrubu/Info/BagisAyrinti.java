package com.example.sahin.enyakinkangrubu.Info;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sahin.enyakinkangrubu.Ayarlar;
import com.example.sahin.enyakinkangrubu.ButtonEffect.Effect;
import com.example.sahin.enyakinkangrubu.Chat;
import com.example.sahin.enyakinkangrubu.KanAriyorum;
import com.example.sahin.enyakinkangrubu.KanBagisliyorum;
import com.example.sahin.enyakinkangrubu.Mesajlarim;
import com.example.sahin.enyakinkangrubu.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class BagisAyrinti extends AppCompatActivity implements OnMapReadyCallback {

    String UserId, Username, UserPhone, BloodName, GenderName, Smsizin, Aramaizin, Chatizin, Latitude, Longitude, Uzaklik;
    GoogleMap googleMap;
    MapFragment mapFragment;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bagis_ayrinti);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        menu_butonlari();
        degiskenleri_ata();
        context=this;
        try {

            if (googleMap == null) {

                mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map2));
                mapFragment.getMapAsync(this);

            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        Button btn_chat = (Button) findViewById(R.id.btnchat2);
        if (Chatizin.equals("0"))
            btn_chat.setVisibility(View.GONE);

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Chat.class);
                i.putExtra("UserId", UserId);
                i.putExtra("bilgi","bagisayrinti");
                startActivity(i);
            }
        });

        Button btn_sms = (Button) findViewById(R.id.button7);
        if (Smsizin.equals("0"))
            btn_sms.setVisibility(View.GONE);

        Button btn_ara = (Button) findViewById(R.id.button8);
        if (Aramaizin.equals("0"))
            btn_ara.setVisibility(View.GONE);

        btn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + "+90" + UserPhone));
                intent.putExtra("sms_body", "Acil " + BloodName + " Kana İhtiyacım Var");
                startActivity(intent);
            }
        });

        btn_ara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:+90" + UserPhone));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

        isPermissionGranted();



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
    private void degiskenleri_ata() {
        UserId = getIntent().getExtras().getString("UserId");
        Username = getIntent().getExtras().getString("Username");
        UserPhone = getIntent().getExtras().getString("UserPhone");
        BloodName = getIntent().getExtras().getString("BloodName");
        GenderName = getIntent().getExtras().getString("GenderName");
        Smsizin = getIntent().getExtras().getString("Smsizin");
        Aramaizin = getIntent().getExtras().getString("Aramaizin");
        Chatizin = getIntent().getExtras().getString("Chatizin");
        Latitude = getIntent().getExtras().getString("Latitude");
        Longitude = getIntent().getExtras().getString("Longitude");
        Uzaklik = getIntent().getExtras().getString("Uzaklik");
    }
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
        Marker TP = googleMap.addMarker(new MarkerOptions().position(TutorialsPoint).title(Username));
    }
}
