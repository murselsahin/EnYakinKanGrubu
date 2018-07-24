package com.example.sahin.enyakinkangrubu;

import android.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sahin.enyakinkangrubu.ButtonEffect.Effect;
import com.example.sahin.enyakinkangrubu.Info.BloodInfo;
import com.example.sahin.enyakinkangrubu.Info.UserInfo;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class KanIlanlarinaGozat extends AppCompatActivity {
    public static Context context;
    public Spinner spinnerKanGrubu,spinnerUzaklik;
    public ArrayList<String> bloodNameList,uzaklikNameList;
    public ArrayList<Integer> bloodIdList,uzaklikIdList;
    public ArrayList<String> list_Id,list_UserId,list_HospitalName,list_Declaration,list_Date,list_Longitude,list_Latitude,list_UserName,list_UserDate,list_UserPhone,list_GenderName,list_Uzaklik,list_BloodName,list_ImgUrl;
    public  ListView listView_kanilan;
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
        setContentView(R.layout.activity_kan_ilanlarina_gozat);
       ActionBar actionBar = getSupportActionBar();
       actionBar.hide();
       menu_butonlari();
       context=this;
       ChatInfo.chat_sayfasi_acikmi=false;
        spinnerKanGrubu = (Spinner)findViewById(R.id.sskangrubu);
        spinnerUzaklik = (Spinner)findViewById(R.id.suzaklik);

        Button blistele = (Button)findViewById(R.id.blistele);



        uzaklikIdList = new ArrayList<>();
        uzaklikNameList=new ArrayList<>();

        int[] uzakliklar = {-1,3,5,10,15,30,50,100,150,200};
        for(int i=0;i<uzakliklar.length;i++)
        {
            if(uzakliklar[i]==-1)
            {
                uzaklikNameList.add("∞ KM");
                uzaklikIdList.add(uzakliklar[i]);
            }else {
                uzaklikNameList.add("Max " +uzakliklar[i]+" KM");
                uzaklikIdList.add(uzakliklar[i]);
            }

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, uzaklikNameList);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        spinnerUzaklik.setAdapter(adapter);

        spinnerUzaklik.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        listView_kanilan = (ListView)findViewById(R.id.list_kanilanlari);




        blistele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(new Database(getApplicationContext(),"",null,1).coordinat_bosmu())
                {
                    Toast.makeText(getApplicationContext(),"Sistemde Konum Bilgileriniz Bulunmuyor. Lütfen Konumunuzu Güncelleyiniz",Toast.LENGTH_LONG).show();
                    return;
                }
                int BloodGroupId  = bloodIdList.get(spinnerKanGrubu.getSelectedItemPosition());
                int Uzaklik =  uzaklikIdList.get(spinnerUzaklik.getSelectedItemPosition());
                String[] coordinates = UserInfo.Coordinates.split(" ");
                String Longitude = coordinates[0];
                String Latitude = coordinates[1];
                int UserId = UserInfo.Id;

                new kan_ilanlarini_getir().execute("http://acilkan.iskenderunteknik.com/uygulama/kanilanigetir.php?BloodGroupId="+BloodGroupId+"&Uzaklik="+Uzaklik+"&Latitude="+Latitude+"&Longitude="+Longitude+"&UserId="+UserId+"");

            }
        });

        Effect.buttonEffect(blistele);


    kan_gruplari_doldur();
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
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, 1);
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


    private void kan_gruplari_doldur()
    {
        bloodNameList= BloodInfo.listBloodGroupName;
        bloodIdList=BloodInfo.listBloodGroupId;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, bloodNameList);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        spinnerKanGrubu.setAdapter(adapter);

        spinnerKanGrubu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }




    class kan_ilanlarini_getir extends AsyncTask<String, String, String> {

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
                if(sonuc.equals("yok"))
                {
                    list_Id=new ArrayList<>();
                    list_UserId=new ArrayList<>();
                    list_HospitalName=new ArrayList<>();
                    list_Declaration=new ArrayList<>();
                    list_Date=new ArrayList<>();
                    list_Longitude=new ArrayList<>();
                    list_Latitude=new ArrayList<>();
                    list_UserName=new ArrayList<>();
                    list_UserDate=new ArrayList<>();
                    list_UserPhone=new ArrayList<>();
                    list_GenderName=new ArrayList<>();
                    list_Uzaklik=new ArrayList<>();
                    list_BloodName=new ArrayList<>();
                    list_ImgUrl=new ArrayList<>();

                    listView_kanilan.setAdapter(null);

                    Toast.makeText(getApplicationContext(),"İlan Yok",Toast.LENGTH_LONG).show();
                    return;
                }

                String Id = new JSONObject(sonuc).getString("Id");
                String UserId = new JSONObject(sonuc).getString("UserId");
                String HospitalName = new JSONObject(sonuc).getString("HospitalName");
                String Declaration = new JSONObject(sonuc).getString("Declaration");
                String Date = new JSONObject(sonuc).getString("Date");
                String Longitude = new JSONObject(sonuc).getString("Longitude");
                String Latitude = new JSONObject(sonuc).getString("Latitude");
                String UserName = new JSONObject(sonuc).getString("UserName");
                String UserDate = new JSONObject(sonuc).getString("UserDate");
                String UserPhone = new JSONObject(sonuc).getString("UserPhone");
                String GenderName = new JSONObject(sonuc).getString("GenderName");
                String Uzaklik = new JSONObject(sonuc).getString("Uzaklik");
                String BloodName = new JSONObject(sonuc).getString("BloodName");
                String ImgUrl = new JSONObject(sonuc).getString("ImgUrl");

                String[] dId = Id.split(";");
                String[] dUserId = UserId.split(";");
                String[] dHospitalName = HospitalName.split(";");
                String[] dDeclaration = Declaration.split(";");
                String[] dDate = Date.split(";");
                String[] dLongitude = Longitude.split(";");
                String[] dLatitude = Latitude.split(";");
                String[] dUserName = UserName.split(";");
                String[] dUserDate = UserDate.split(";");
                String[] dUserPhone = UserPhone.split(";");
                String[] dGenderName = GenderName.split(";");
                String[] dUzaklik = Uzaklik.split(";");
                String[] dBloodName = BloodName.split(";");
                String[] dImgUrl=ImgUrl.split(";");

                list_Id=new ArrayList<>();
                list_UserId=new ArrayList<>();
                list_HospitalName=new ArrayList<>();
                list_Declaration=new ArrayList<>();
                list_Date=new ArrayList<>();
                list_Longitude=new ArrayList<>();
                list_Latitude=new ArrayList<>();
                list_UserName=new ArrayList<>();
                list_UserDate=new ArrayList<>();
                list_UserPhone=new ArrayList<>();
                list_GenderName=new ArrayList<>();
                list_Uzaklik=new ArrayList<>();
                list_BloodName=new ArrayList<>();
                list_ImgUrl=new ArrayList<>();

                for(int i=0;i<dId.length;i++)
                {
                    list_Id.add(dId[i]);
                    list_UserId.add(dUserId[i]);
                    list_HospitalName.add(dHospitalName[i]);
                    list_Declaration.add(dDeclaration[i]);
                    list_Date.add(dDate[i]);
                    list_Longitude.add(dLongitude[i]);
                    list_Latitude.add(dLatitude[i]);
                    list_UserName.add(dUserName[i]);
                    list_UserDate.add(dUserDate[i]);
                    list_UserPhone.add(dUserPhone[i]);
                    list_GenderName.add(dGenderName[i]);
                    list_Uzaklik.add(dUzaklik[i]);
                    list_BloodName.add(dBloodName[i]);
                    list_ImgUrl.add(dImgUrl[i]);

                }
                ArrayList<KanIlanList> list = new ArrayList<>();
                for(int i=0;i<list_UserName.size();i++)
                {
                    double uzaklik = Double.parseDouble(list_Uzaklik.get(i));
                    String yuzaklik = new DecimalFormat("##.##").format(uzaklik);

                    KanIlanList k = new KanIlanList(list_Id.get(i),list_UserId.get(i),list_HospitalName.get(i),list_Declaration.get(i),list_Date.get(i),list_Longitude.get(i)
                    , list_Latitude.get(i) , list_UserName.get(i) , list_UserDate.get(i),list_UserPhone.get(i),list_GenderName.get(i),yuzaklik,list_BloodName.get(i),list_ImgUrl.get(i));
                    list.add(k);

                }
                KanIlanListAdapter adapter1 = new KanIlanListAdapter(context,R.layout.adapter_bagisci_layout,list);
                listView_kanilan.setAdapter(adapter1);



                listView_kanilan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                }
                });









            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
            }

        }
    }
}
