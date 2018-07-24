package com.example.sahin.enyakinkangrubu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahin.enyakinkangrubu.ButtonEffect.Effect;
import com.example.sahin.enyakinkangrubu.Info.BloodInfo;
import com.example.sahin.enyakinkangrubu.Info.GoogleInfo;
import com.example.sahin.enyakinkangrubu.Info.UserInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Kanilanver extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;

    public ArrayList<String> bloodNameList;
    public ArrayList<Integer> bloodIdList;
    String currentTime;
    public EditText eHospitalName,eDeclaration;
    public Spinner sBloodGroup;
    public TextView tNowCoordinates;
    Button btn_konumuguncelle;


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
        setContentView(R.layout.activity_kanilanver);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ChatInfo.chat_sayfasi_acikmi=false;
        menu_butonlari();

        eHospitalName=(EditText)findViewById(R.id.eHastaneAdi);
        eDeclaration=(EditText)findViewById(R.id.eAciklama);

        sBloodGroup = (Spinner)findViewById(R.id.spinnerkangrubu);

        tNowCoordinates = (TextView)findViewById(R.id.tsuankikonum);
        if(UserInfo.Coordinates.equals("a"))
        tNowCoordinates.setText("Su Anki Konum : Bulunamadi ");

        if(!UserInfo.Coordinates.equals("a"))
        {
            adres_hesapla();
        }



        Button btn_ilani_ver = (Button)findViewById(R.id.bilaniver);

        btn_ilani_ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database db = new Database(getApplicationContext(),"",null,1);
                if(db.coordinat_bosmu())
                {
                    Toast.makeText(getApplicationContext(),"Konumunuz Bulunmuyor. Konumunuzu Güncelleyiniz . ", Toast.LENGTH_LONG).show();
                    return;
                }

                ilani_ver();
            }
        });

       Effect.buttonEffect(btn_ilani_ver);


       btn_konumuguncelle = (Button)findViewById(R.id.bkonumuguncelle);

        Effect.buttonEffect(btn_konumuguncelle);

        if(!runtime_permissions()) {
            enable_buttons();

        }

        kan_gruplari_doldur();



    }


    private void kan_gruplari_doldur()
    {
        bloodNameList= BloodInfo.listBloodGroupName;
        bloodIdList=BloodInfo.listBloodGroupId;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, bloodNameList);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        sBloodGroup.setAdapter(adapter);

        sBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void adres_hesapla()
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        String[] coordinates = UserInfo.Coordinates.split(" ");
        Longitude = coordinates[0];
        Latitude = coordinates[1];
        addresses = new ArrayList<Address>();
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(Latitude),Double.parseDouble(Longitude),1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        int N = addresses.get(0).getMaxAddressLineIndex();
        String adres="";
        for(int i=0;i<=N;i++)
            adres+=" "+addresses.get(0).getAddressLine(i);



        String mahalle = addresses.get(0).getAddressLine(0);
        String sokak = addresses.get(0).getAddressLine(1);
        String posta_kodu_ilce_il = addresses.get(0).getAddressLine(2);
        String ulke = addresses.get(0).getAddressLine(3);

        tNowCoordinates.setText(adres);
    }

    public String HospitalName,Declaration,Date,Longitude,Latitude;
    public int BloodGroupId;
    private void ilani_ver()
    {
        if(UserInfo.Coordinates.equals("a"))
        {
            Toast.makeText(getApplicationContext(),"Konumu Belirleyin",Toast.LENGTH_SHORT).show();
            return;
        }

        HospitalName = eHospitalName.getText().toString().replace(';',' ');
        Declaration = eDeclaration.getText().toString().replace(';',' ');
        java.util.Date dt = new java.util.Date();

        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        currentTime = sdf.format(dt);
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date = formatter.format(today);
        BloodGroupId = bloodIdList.get(sBloodGroup.getSelectedItemPosition());
        String[] coordinates = UserInfo.Coordinates.split(" ");
        Longitude = coordinates[0];
        Latitude = coordinates[1];

        if(HospitalName.length()>0 && Declaration.length()>0 && Date.length()>0 && sBloodGroup.getSelectedItemPosition()>-1 && Longitude.length()>0 && Latitude.length()>0)
        {
            new kanilaniver().execute("http://acilkan.iskenderunteknik.com/uygulama/kanilaniekle.php");

        }else
        {
            Toast.makeText(getApplicationContext(),"Bilgileri Dogru Giriniz",Toast.LENGTH_SHORT).show();
        }



    }


    class kanilaniver extends AsyncTask<String, String, String> {



        @Override
        protected String doInBackground(String... strings) {
            //ilk elemani sunucu adresi URL
            HttpURLConnection connection = null;
            BufferedReader br = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);



                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("UserId",UserInfo.Id+"")
                        .appendQueryParameter("HospitalName", HospitalName)
                        .appendQueryParameter("BloodGroupId", BloodGroupId+"")
                        .appendQueryParameter("Declaration",Declaration)
                        .appendQueryParameter("Date",currentTime)
                        .appendQueryParameter("Longitude", Longitude)
                        .appendQueryParameter("Latitude",Latitude);
                String query = builder.build().getEncodedQuery();


                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();


                connection.connect();


                InputStream is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                String satir;
                String dosya = "";
                while ((satir = br.readLine()) != null) {
                    Log.d("satir", satir);
                    dosya += satir;
                }
                return dosya;
            } catch (Exception e) {
                e.printStackTrace();
                return e+"";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                String sonuc = new JSONObject(s).getString("bilgi");
                if(sonuc.equals("eklendi"))
                {


                    Toast.makeText(getApplicationContext(),"İlanınız Eklendi",Toast.LENGTH_LONG).show();

                    eHospitalName.setText("");
                    eDeclaration.setText("");


                }else
                {
                    Toast.makeText(getApplicationContext(),"Eklenemedi",Toast.LENGTH_LONG).show();

                }
            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
            }

        }
    }



    private void enable_buttons() {

        btn_konumuguncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getApplicationContext(),GPS_Service.class);
                startService(i);

            }
        });


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
    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
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

                    adres_hesapla();

                    Toast.makeText(getApplicationContext(),"Konum Güncellendi", Toast.LENGTH_LONG).show();

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


}
