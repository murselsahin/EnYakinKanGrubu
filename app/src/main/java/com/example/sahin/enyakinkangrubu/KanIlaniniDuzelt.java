package com.example.sahin.enyakinkangrubu;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sahin.enyakinkangrubu.ButtonEffect.Effect;
import com.example.sahin.enyakinkangrubu.Info.IlaniDuzelt;
import com.example.sahin.enyakinkangrubu.Info.UserInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class KanIlaniniDuzelt extends AppCompatActivity {

    public Context context=this;
    public ArrayList<String> list_Id,list_UserId,list_HospitalName,list_Declaration,list_Date,list_Longitude,list_Latitude,list_UserName,list_UserDate,list_UserPhone,list_GenderName,list_Uzaklik,list_BloodName;
    public ListView listView_kanilan;
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
        setContentView(R.layout.activity_kan_ilanini_duzelt);
        ChatInfo.chat_sayfasi_acikmi=false;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        menu_butonlari();
        listView_kanilan = (ListView)findViewById(R.id.userkanilanlari);

        new user_kan_ilanlarini_getir().execute("http://acilkan.iskenderunteknik.com/uygulama/userkanilanigetir.php?UserId="+ UserInfo.Id+"");
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), KanAriyorum.class);
        startActivity(i);

    }

    class user_kan_ilanlarini_getir extends AsyncTask<String, String, String> {

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
                String BloodName = new JSONObject(sonuc).getString("BloodName");

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
                String[] dBloodName = BloodName.split(";");

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
                    list_BloodName.add(dBloodName[i]);

                }
                ArrayList<KendiIlaninList> list = new ArrayList<>();
                for(int i=0;i<list_UserName.size();i++)
                {




                    KendiIlaninList k = new  KendiIlaninList(list_Id.get(i),list_UserId.get(i),list_HospitalName.get(i),list_Declaration.get(i),list_Date.get(i),list_Longitude.get(i)
                    ,list_Latitude.get(i),list_UserName.get(i),list_UserDate.get(i),list_UserPhone.get(i),list_GenderName.get(i),list_BloodName.get(i));
                    list.add(k);

                }
                KendiIlaninListAdapter adapter1 = new KendiIlaninListAdapter(context,R.layout.adapter_kendi_ilanin,list);
                listView_kanilan.setAdapter(adapter1);



                listView_kanilan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {



                        Intent intent = new Intent(getApplicationContext(),IlaniDuzelt.class);
                        intent.putExtra("Id",list_Id.get(i));
                        intent.putExtra("UserId",list_UserId.get(i));
                        intent.putExtra("HospitalName",list_HospitalName.get(i));
                        intent.putExtra("Declaration",list_Declaration.get(i));
                        intent.putExtra("Date",list_Date.get(i));
                        intent.putExtra("Longitude",list_Longitude.get(i));
                        intent.putExtra("Latitude",list_Latitude.get(i));
                        intent.putExtra("UserName",list_UserName.get(i));
                        intent.putExtra("UserDate",list_UserDate.get(i));
                        intent.putExtra("UserPhone",list_UserPhone.get(i));
                        intent.putExtra("GenderName",list_GenderName.get(i));
                        intent.putExtra("BloodName",list_BloodName.get(i));

                        startActivity(intent);


                    }
                });







      




            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
            }

        }
    }
}
