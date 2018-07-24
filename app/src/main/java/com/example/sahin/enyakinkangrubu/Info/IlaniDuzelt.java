package com.example.sahin.enyakinkangrubu.Info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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

import com.example.sahin.enyakinkangrubu.Ayarlar;
import com.example.sahin.enyakinkangrubu.ButtonEffect.Effect;
import com.example.sahin.enyakinkangrubu.Database;
import com.example.sahin.enyakinkangrubu.GPS_Service;
import com.example.sahin.enyakinkangrubu.KanAriyorum;
import com.example.sahin.enyakinkangrubu.KanBagisliyorum;
import com.example.sahin.enyakinkangrubu.KanIlaniniDuzelt;
import com.example.sahin.enyakinkangrubu.MainActivity;
import com.example.sahin.enyakinkangrubu.Mesajlarim;
import com.example.sahin.enyakinkangrubu.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IlaniDuzelt extends AppCompatActivity {
    private BroadcastReceiver broadcastReceiver;
    public ArrayList<String> bloodNameList;
    public ArrayList<Integer> bloodIdList;
    String Id,UserId,HospitalName,Declaration,Date,Longitude,Latitude,UserName,UserDate,UserPhone,GenderName,BloodName,Adres;
    TextView txtAdres;
    EditText edtHastaneAdi,edtAciklama;
    Spinner skangrubu;
    Button bkonumu_guncelle,bilani_guncelle,bilani_sil;
    Context context = this;
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
        setContentView(R.layout.activity_ilani_duzelt);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        menu_butonlari();
        degiskenleri_ata();

        edtHastaneAdi=(EditText)findViewById(R.id.eHastaneAdi);
        edtAciklama=(EditText)findViewById(R.id.editText2);
        txtAdres=(TextView)findViewById(R.id.textView11);
        skangrubu=(Spinner)findViewById(R.id.spinner);
        bkonumu_guncelle=(Button)findViewById(R.id.button2);
        bilani_guncelle=(Button)findViewById(R.id.button3);
        bilani_sil=(Button)findViewById(R.id.button5);

        txtAdres.setText(Adres);
        edtHastaneAdi.setText(HospitalName);
        edtAciklama.setText(Declaration);

        kan_gruplari_doldur();

        if(!runtime_permissions()) {
            enable_buttons();

        }

        spinner_kangrubu_secilen_duzenle();

        bilani_guncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtHastaneAdi.getText().toString().length()==0 || edtAciklama.getText().toString().length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Bilgileri Doldurunuz .",Toast.LENGTH_LONG).show();
                    return;
                }

                ilani_guncelle();
            }
        });

        bilani_sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Uyarı");
                builder.setMessage("İlanınız Silinsin mi");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setCancelable(false);

                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new ilanisil().execute("http://acilkan.iskenderunteknik.com/uygulama/ilanisil.php?Id="+Id+"");

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
        });

    }
    class ilanisil extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            //ilk elemani sunucu adresi URL
            HttpURLConnection connection = null;
            BufferedReader br = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
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

                if(sonuc.equals("silindi"))
                {
                    Toast.makeText(getApplicationContext(),"İlanınız Silindi",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(),KanIlaniniDuzelt.class);
                    startActivity(i);
                }else
                {
                    Toast.makeText(getApplicationContext(),"Bir Hata Oluştu",Toast.LENGTH_LONG).show();
                }





            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
            }

        }
    }


    class ilaniguncelle extends AsyncTask<String, String, String> {



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
                        .appendQueryParameter("Id", Id)
                        .appendQueryParameter("HospitalName", edtHastaneAdi.getText().toString().replace(';',' '))
                        .appendQueryParameter("BloodGroupId", bloodIdList.get(skangrubu.getSelectedItemPosition()).toString())
                        .appendQueryParameter("Declaration",edtAciklama.getText().toString().replace(';',' '))
                        .appendQueryParameter("Longitude",Longitude)
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
                if(sonuc.equals("guncellendi"))
                {

                    Toast.makeText(getApplicationContext(),"Güncellendi",Toast.LENGTH_LONG).show();

                }else
                {
                    Toast.makeText(getApplicationContext(),"Güncellenemedi",Toast.LENGTH_LONG).show();

                }
            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
            }

        }
    }

    private void ilani_guncelle()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Uyarı");
        builder.setMessage("İlanınız Güncellensin mn");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setCancelable(false);

        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                new ilaniguncelle().execute("http://acilkan.iskenderunteknik.com/uygulama/ilaniduzelt.php");
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

    private void enable_buttons() {

        bkonumu_guncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getApplicationContext(),GPS_Service.class);
                startService(i);

            }
        });


    }
    private void spinner_kangrubu_secilen_duzenle()
    {
        int secilen=0;
        for(int i=0;i<bloodNameList.size();i++)
        {
            if(bloodNameList.get(i).equals(BloodName))
            {
                secilen=i;
                break;
            }
        }
        skangrubu.setSelection(secilen);
    }

    private void kan_gruplari_doldur()
    {
        bloodNameList= BloodInfo.listBloodGroupName;
        bloodIdList=BloodInfo.listBloodGroupId;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, bloodNameList);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        skangrubu.setAdapter(adapter);

        skangrubu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), KanIlaniniDuzelt.class);
        startActivity(i);

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
        BloodName = getIntent().getExtras().getString("BloodName");

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
///////////////////////////////////////////////////////////////////////KONUM ISLMELRI /////////////////////////////////////
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

    txtAdres.setText(adres);
}
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
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

}
