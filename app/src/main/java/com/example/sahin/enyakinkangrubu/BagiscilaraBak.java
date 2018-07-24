package com.example.sahin.enyakinkangrubu;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
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
import com.example.sahin.enyakinkangrubu.Info.BagisAyrinti;
import com.example.sahin.enyakinkangrubu.Info.BloodInfo;
import com.example.sahin.enyakinkangrubu.Info.UserInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class BagiscilaraBak extends AppCompatActivity {
    public static Context context;
    public Spinner spinnerKanGrubu,spinnerUzaklik;
    public ArrayList<String> bloodNameList,uzaklikNameList;
    public ArrayList<Integer> bloodIdList,uzaklikIdList;
    public ArrayList<String> list_UserId,list_Username,list_UserPhone,list_BloodName,list_GenderName,list_Smsizin,list_Aramaizin,list_Chatizin,list_Latitude,list_Longitude,list_Uzaklik,list_ImgUrl;
    public ListView listView_kanilan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bagiscilara_bak);
        ChatInfo.chat_sayfasi_acikmi=false;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        menu_butonlari();
        context=this;

        spinnerKanGrubu = (Spinner)findViewById(R.id.sskangrubu2);
        spinnerUzaklik = (Spinner)findViewById(R.id.suzaklik2);

        Button blistele = (Button)findViewById(R.id.blistele2);

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

        listView_kanilan = (ListView)findViewById(R.id.list_kanilanlari2);

        Effect.buttonEffect(blistele);

        blistele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int BloodGroupId  = bloodIdList.get(spinnerKanGrubu.getSelectedItemPosition());
                int Uzaklik =  uzaklikIdList.get(spinnerUzaklik.getSelectedItemPosition());
                if(new Database(getApplicationContext(),"",null,1).coordinat_bosmu())
                {
                    Toast.makeText(getApplicationContext(),"Konumunuz Bulunamamıştır . Lütfen Konum Bilgilerinizi Güncelleyiniz",Toast.LENGTH_LONG).show();
                    return;
                }
                String[] coordinates = UserInfo.Coordinates.split(" ");
                String Longitude = coordinates[0];
                String Latitude = coordinates[1];
                int UserId = UserInfo.Id;
                new BagiscilariGetir().execute("http://acilkan.iskenderunteknik.com/uygulama/bagiscilarigetir.php?BloodGroupId="+BloodGroupId+"&Uzaklik="+Uzaklik+"&UserId="+UserId+"&Longitude="+Longitude+"&Latitude="+Latitude+"");


            }
        });

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

    class BagiscilariGetir extends AsyncTask<String, String, String> {

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
                    list_UserId=new ArrayList<>();
                    list_Username=new ArrayList<>();
                    list_UserPhone=new ArrayList<>();
                    list_BloodName=new ArrayList<>();
                    list_GenderName=new ArrayList<>();
                    list_Smsizin=new ArrayList<>();
                    list_Aramaizin=new ArrayList<>();
                    list_Chatizin=new ArrayList<>();
                    list_Latitude=new ArrayList<>();
                    list_Longitude=new ArrayList<>();
                    list_Uzaklik=new ArrayList<>();
                    list_ImgUrl=new ArrayList<>();


                    listView_kanilan.setAdapter(null);

                    Toast.makeText(getApplicationContext(),"İlan Yok",Toast.LENGTH_LONG).show();
                    return;
                }

                String UserId = new JSONObject(sonuc).getString("UserId");
                String UserName = new JSONObject(sonuc).getString("UserName");
                String UserPhone = new JSONObject(sonuc).getString("UserPhone");
                String BloodName = new JSONObject(sonuc).getString("BloodName");
                String GenderName = new JSONObject(sonuc).getString("GenderName");
                String Smsizin = new JSONObject(sonuc).getString("Smsizin");
                String Aramaizin = new JSONObject(sonuc).getString("Aramaizin");
                String Chatizin = new JSONObject(sonuc).getString("Chatizin");
                String Latitude = new JSONObject(sonuc).getString("Latitude");
                String Longitude = new JSONObject(sonuc).getString("Longitude");
                String Uzaklik = new JSONObject(sonuc).getString("dUzaklik");
                String ImgUrl = new JSONObject(sonuc).getString("ImgUrl");


                String[] dUserId = UserId.split(";");
                String[] dUserName = UserName.split(";");
                String[] dUserPhone = UserPhone.split(";");
                String[] dBloodName = BloodName.split(";");
                String[] dGenderName = GenderName.split(";");
                String[] dSmsizin = Smsizin.split(";");
                String[] dAramaizin = Aramaizin.split(";");
                String[] dChatizin = Chatizin.split(";");
                String[] dLatitude = Latitude.split(";");
                String[] dLongitude = Longitude.split(";");
                String[] dUzaklik = Uzaklik.split(";");
                String[] dImgUrl=ImgUrl.split(";");


                list_UserId=new ArrayList<>();
                list_Username=new ArrayList<>();
                list_UserPhone=new ArrayList<>();
                list_BloodName=new ArrayList<>();
                list_GenderName=new ArrayList<>();
                list_Smsizin=new ArrayList<>();
                list_Aramaizin=new ArrayList<>();
                list_Chatizin=new ArrayList<>();
                list_Latitude=new ArrayList<>();
                list_Longitude=new ArrayList<>();
                list_Uzaklik=new ArrayList<>();
                list_ImgUrl=new ArrayList<>();

                for(int i=0;i<dUserId.length;i++)
                {
                    list_UserId.add(dUserId[i]);
                    list_Username.add(dUserName[i]);
                    list_UserPhone.add(dUserPhone[i]);
                    list_BloodName.add(dBloodName[i]);
                    list_GenderName.add(dGenderName[i]);
                    list_Smsizin.add(dSmsizin[i]);
                    list_Aramaizin.add(dAramaizin[i]);
                    list_Chatizin.add(dChatizin[i]);
                    list_Latitude.add(dLatitude[i]);
                    list_Longitude.add(dLongitude[i]);
                    list_Uzaklik.add(dUzaklik[i]);
                    list_ImgUrl.add(dImgUrl[i]);

                }

                ArrayList<BagisciList> list = new ArrayList<>();
                for(int i=0;i<list_Username.size();i++)
                {
                    double uzaklik = Double.parseDouble(list_Uzaklik.get(i));
                    String yuzaklik = new DecimalFormat("##.##").format(uzaklik);

                    BagisciList b = new BagisciList(list_UserId.get(i),list_Username.get(i),list_BloodName.get(i),list_Chatizin.get(i),list_Aramaizin.get(i),list_Smsizin.get(i)
                    ,list_Longitude.get(i),list_Latitude.get(i),list_UserPhone.get(i),list_GenderName.get(i),yuzaklik,list_ImgUrl.get(i));
                   // KanIlanList k = new KanIlanList(list_Username.get(i),list_BloodName.get(i),list_UserPhone.get(i),yuzaklik+" KM");
                    list.add(b);

                }

                BagisciListAdapter adapter1 = new BagisciListAdapter (context,R.layout.adapter_bagisci_layout,list);
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
