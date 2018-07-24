package com.example.sahin.enyakinkangrubu;

import android.*;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahin.enyakinkangrubu.ButtonEffect.Effect;
import com.example.sahin.enyakinkangrubu.Info.GoogleInfo;
import com.example.sahin.enyakinkangrubu.Info.UserInfo;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AnaSayfa extends AppCompatActivity {

    private Button btn_start, btn_stop;
    private GoogleApiClient mGoogleApiClient ;
    private BroadcastReceiver broadcastReceiver;

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
        Effect.buttonEffect(bayarlar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {


                        String s = intent.getExtras().get("coordinates")+"";
                        new Database(getApplicationContext(),"",null,1).coordinatekle(s);
                        UserInfo.Coordinates=s;
                        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                        stopService(i);



                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Uyarı");
        builder.setMessage("Çıkmak İstediğinizden Emin misiniz ?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setCancelable(false);

        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Database(getApplicationContext(),"",null,1).remember_userId_sil();
                dialogInterface.dismiss();

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);

                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {


                    }
                });

            }
        });

        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();



    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa);
        ChatInfo.chat_sayfasi_acikmi=false;
        ChatInfo.chat_service=true;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
       // Intent iservice = new Intent(AnaSayfa.this,Deneme_Service.class);
        //startService(iservice);

        Intent iservice = new Intent(AnaSayfa.this,ChatService.class);
        startService(iservice);

        Intent iiservice = new Intent(AnaSayfa.this,IlanServis.class);
        startService(iiservice);

        menu_butonlari();


        Button btn_hakkinda = (Button)findViewById(R.id.buygulamahakkinda);
        Effect.buttonEffect(btn_hakkinda);
        btn_hakkinda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),UygulumaHakkinda.class);
                startActivity(i);
            }
        });



        Button btn_mesajlarim = (Button)findViewById(R.id.bmesajlarim);
        btn_mesajlarim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Mesajlarim.class);
                startActivity(i);
            }
        });

        Button btn_kanariyorum = (Button)findViewById(R.id.bkanariyorum);
        Button btn_kanbagisliyorum = (Button)findViewById(R.id.bkanbagisliyorum);

        Effect.buttonEffect(btn_kanariyorum);
        Effect.buttonEffect(btn_kanbagisliyorum);
        Effect.buttonEffect(btn_mesajlarim);

        Button btn_ayarlarim=(Button)findViewById(R.id.bayarlarim);
        btn_ayarlarim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Ayarlar.class);
                startActivity(i);

            }
        });

        Effect.buttonEffect(btn_ayarlarim);

        btn_kanbagisliyorum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),KanBagisliyorum.class);
                startActivity(intent);
            }
        });

        btn_kanariyorum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),KanAriyorum.class);
                startActivity(intent);
            }
        });
        if(new Database(getApplicationContext(),"",null,1).coordinat_bosmu()) {
            if (!runtime_permissions()) {
                enable_buttons();
            }
        }else
        {
            UserInfo.Coordinates=new Database(getApplicationContext(),"",null,1).coordinat_getir();
        }
     }






    private void enable_buttons() {
                  Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                    startService(i);
    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }else {
                runtime_permissions();
            }
        }
    }



}
