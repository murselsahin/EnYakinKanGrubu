package com.example.sahin.enyakinkangrubu;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.sahin.enyakinkangrubu.ButtonEffect.Effect;
import com.example.sahin.enyakinkangrubu.Info.UserInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BagisBilgilerim extends AppCompatActivity {

    CheckBox cbagiscimi,carama,csms,cchat;
    Button btn_kaydet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bagis_bilgilerim);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ChatInfo.chat_sayfasi_acikmi=false;
        menu_butonlari();
        cbagiscimi=(CheckBox)findViewById(R.id.checkBox2);
        carama=(CheckBox)findViewById(R.id.checkBox3);
        csms=(CheckBox)findViewById(R.id.checkBox4);
        cchat=(CheckBox)findViewById(R.id.checkBox5);
        btn_kaydet=(Button)findViewById(R.id.button4);

        carama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kontrol();
            }
        });

        cchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kontrol();
            }
        });

        csms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kontrol();
            }
        });

        btn_kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UserInfo.Coordinates.equals("a"))
                {
                    Toast.makeText(getApplicationContext(),"Sisteminizde Konum Bilgisi Yok",Toast.LENGTH_LONG).show();
                    return;
                }

                String bagiscimi="";
                String smsizin = "";
                String aramaizin = "";
                String chatizin="";

                if(cbagiscimi.isChecked())
                    bagiscimi="1";
                else
                    bagiscimi="0";

                if(csms.isChecked())
                    smsizin="1";
                else
                    smsizin="0";

                if(carama.isChecked())
                    aramaizin="1";
                else
                    aramaizin="0";

                if(cchat.isChecked())
                    chatizin="1";
                else
                    chatizin="0";

                String[] coordinates = UserInfo.Coordinates.split(" ");
                String Longitude = coordinates[0];
                String Latitude = coordinates[1];

                new ilani_guncelle().execute("http://acilkan.iskenderunteknik.com/uygulama/izinguncelle.php?UserId="+UserInfo.Id+"&Bagiscimi="+bagiscimi+"&Smsizin="+smsizin+"&Aramaizin="+aramaizin+"&Chatizin="+chatizin+"&Latitude="+Latitude+"&Longitude="+Longitude+"");

            }
        });

        cbagiscimi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cbagiscimi.isChecked()==false)
                {
                    carama.setChecked(false);
                    csms.setChecked(false);
                    cchat.setChecked(false);
                }
                if(cbagiscimi.isChecked())
                {
                    if(carama.isChecked()==false && csms.isChecked()==false && cchat.isChecked()==false)
                        carama.setChecked(true);
                }
            }
        });

        new checkboxlari_doldur().execute("http://acilkan.iskenderunteknik.com/uygulama/izinler.php?UserId="+UserInfo.Id+"");

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

    private void kontrol()
    {
        if(carama.isChecked() || cchat.isChecked() || csms.isChecked())
            cbagiscimi.setChecked(true);
        if(carama.isChecked()==false && csms.isChecked()==false && cchat.isChecked()==false)
            cbagiscimi.setChecked(false);


    }

    class checkboxlari_doldur extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            //ilk elemani sunucu adresi URL
            HttpURLConnection connection = null;
            BufferedReader br = null;
            try{
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.connect();


                InputStream is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                String satir;
                String dosya="";
                while ((satir=br.readLine())!=null)
                {
                    Log.d("satir",satir);
                    dosya += satir;
                }
                return dosya;
            }catch (Exception e){
                e.printStackTrace();
                return  "hata";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {

                String sonuc = new JSONObject(s).getString("bilgi");
                if(sonuc.equals("olumsuz"))
                    return;

                String bagiscimi = new JSONObject(sonuc).getString("bagiscimi");
                String smsizin = new JSONObject(sonuc).getString("smsizin");
                String aramaizin =  new JSONObject(sonuc).getString("Aramaizin");
                String chatizin = new JSONObject(sonuc).getString("Chatizin");

                if(bagiscimi.equals("0"))
                    cbagiscimi.setChecked(false);
                else
                    cbagiscimi.setChecked(true);

                if(smsizin.equals("0"))
                    csms.setChecked(false);
                else
                    csms.setChecked(true);

                if(aramaizin.equals("0"))
                    carama.setChecked(false);
                else
                    carama.setChecked(true);

                if(chatizin.equals("0"))
                    cchat.setChecked(false);
                else
                    cchat.setChecked(true);


            }catch (Exception e)
            {
                e.printStackTrace();
            }



        }
    }


    class ilani_guncelle extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            //ilk elemani sunucu adresi URL
            HttpURLConnection connection = null;
            BufferedReader br = null;
            try{
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.connect();


                InputStream is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                String satir;
                String dosya="";
                while ((satir=br.readLine())!=null)
                {
                    Log.d("satir",satir);
                    dosya += satir;
                }
                return dosya;
            }catch (Exception e){
                e.printStackTrace();
                return  "hata";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {

                String sonuc = new JSONObject(s).getString("bilgi");
               if(sonuc.equals("guncellendi"))
               {
                   Toast.makeText(getApplicationContext(),"Bilgileriniz Güncellendi",Toast.LENGTH_SHORT).show();
               }else
                   Toast.makeText(getApplicationContext(),"Hata Oluştu",Toast.LENGTH_SHORT).show();


            }catch (Exception e)
            {
                e.printStackTrace();
            }



        }
    }

}
