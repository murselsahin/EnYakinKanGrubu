package com.example.sahin.enyakinkangrubu;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericTranscodeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.sahin.enyakinkangrubu.ButtonEffect.Effect;
import com.example.sahin.enyakinkangrubu.Info.BloodInfo;
import com.example.sahin.enyakinkangrubu.Info.GoogleInfo;
import com.example.sahin.enyakinkangrubu.Info.UserInfo;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;


public class KayitFormu extends AppCompatActivity {
    Button tarihButton;
    TextView tarihTextView;
    EditText eAd, eSoyad, eTelefon;
    String googleId;

    private GoogleApiClient mGoogleApiClient ;
    public ArrayList<String> bloodNameList;
    public ArrayList<Integer> bloodIdList,cinsiyetIdList;
    public Spinner spinnerKanGrubu;

    ImageView  imageView;
    private Spinner spinnerCinsiyet;

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
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {


            }
        });
        super.onBackPressed();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_formu);
        ChatInfo.chat_sayfasi_acikmi=false;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();



        //region Değişken eşitlemeleri
        
        eAd = (EditText) findViewById(R.id.Name);
        eSoyad = (EditText) findViewById(R.id.Surname);
        eTelefon = (EditText) findViewById(R.id.Phone);
        tarihButton = (Button) findViewById(R.id.btnDate);
        imageView = (ImageView)findViewById(R.id.imageView5);
        tarihTextView = (TextView) findViewById(R.id.txtDate);

        spinnerKanGrubu = (Spinner) findViewById(R.id.spinnerKanGrubu);
        spinnerCinsiyet = (Spinner) findViewById(R.id.spinnerCinsiyet);
        //endregion

        Glide.with(this).load(GoogleInfo.Url).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
        eAd.setText(GoogleInfo.ad);
        eSoyad.setText(GoogleInfo.soyad);

        kan_gruplari_doldur();

        new cinsiyet_doldur().execute("http://acilkan.iskenderunteknik.com/uygulama/cinsiyet.php");

        Button btn_signup = (Button) findViewById(R.id.btn_signup);
        Effect.buttonEffect(btn_signup);
        Effect.buttonEffect(tarihButton);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kayityap();

            }
        });




        //region Tarihbutonu click listener
        tarihButton.setOnClickListener(new View.OnClickListener() {//tarihButona Click Listener ekliyoruz

                        @Override
                        public void onClick(View v) {
                            try {

                                // TODO Auto-generated method stub
                                Calendar mcurrentTime = Calendar.getInstance();
                                int year = mcurrentTime.get(Calendar.YEAR);//Güncel Yılı alıyoruz
                                int month = mcurrentTime.get(Calendar.MONTH);//Güncel Ayı alıyoruz
                                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);//Güncel Günü alıyoruz

                                DatePickerDialog datePicker;//Datepicker objemiz
                                datePicker = new DatePickerDialog(KayitFormu.this, new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                          int dayOfMonth) {

                                        monthOfYear++;

                                        // TODO Auto-generated method stub
                                        String gun = dayOfMonth + "";
                                        if (gun.length() == 1)
                                            gun = "0" + gun;
                                        String ay = monthOfYear + "";
                                        if (ay.length() == 1)
                                            ay = "0" + ay;


                                        tarihTextView.setText(gun + "/" + ay + "/" + year);//Ayarla butonu tıklandığında textview'a yazdırıyoruz

                                    }
                                }, 1996, 3, 16);//başlarken set edilcek değerlerimizi atıyoruz
                                datePicker.setTitle("Tarih Seçiniz");
                                datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", datePicker);
                                datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", datePicker);

                                datePicker.show();
                            } catch (Exception ex) {
                                Toast.makeText(getApplicationContext(), "" + ex, Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        //endregion
        }


    private void kan_gruplari_doldur()
    {
        bloodNameList=BloodInfo.listBloodGroupName;
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




    String telefon, tarih, ad, soyad;
    int cinsiyetId,cinsiyetSecilen, kangrubuId;


    private void kayityap() {
        // Telefon Kontrolü Başlangıç
        telefon = eTelefon.getText().toString().trim();
        tarih = tarihTextView.getText().toString();
        ad = eAd.getText().toString().replace(';',' ');
        soyad = eSoyad.getText().toString().replace(';',' ');
        int kangrubuSecilen = spinnerKanGrubu.getSelectedItemPosition();
        int cinsiyetSecilen = spinnerCinsiyet.getSelectedItemPosition();
        cinsiyetId = cinsiyetIdList.get(cinsiyetSecilen);
        char[] ch = telefon.toCharArray();

        if (telefon.length() != 10 || ch[0]=='0') {
            Toast.makeText(getApplicationContext(), "Telefon Numarası 10 Haneli Giriniz", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Double.parseDouble(telefon);

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Telefon Numarasına Harf Girmeyiniz", Toast.LENGTH_SHORT).show();
            return;
        }
        // Telefon Kontrolü Bitiş

        if (tarih.length() == 0) {
            Toast.makeText(getApplicationContext(), "Doğum Tarihinizi Belirtiniz", Toast.LENGTH_SHORT).show();
            return;
        }
        //////////////////////////////////////////
        if (ad.length() == 0 || soyad.length() == 0) {
            Toast.makeText(getApplicationContext(), "Ad veya Soyad Boş Bırakılamaz", Toast.LENGTH_SHORT).show();
            return;
        }

        if (kangrubuSecilen == -1 || cinsiyetSecilen == -1)
            return;
        ///////////////////////////////////////////


        kangrubuId=bloodIdList.get(kangrubuSecilen);





        new  kullaniciEkle().execute("http://acilkan.iskenderunteknik.com/uygulama/kullaniciekle.php");


    }

    class kullaniciEkle extends AsyncTask<String, String, String> {

        private String tarih_formati() {
            String tarih = tarihTextView.getText().toString();
            if (tarih.length() == 9)
                tarih = "0" + tarih;

            String gun = "";
            String ay = "";
            String yil = "";

            char[] ch = tarih.toCharArray();

            for (int i = 0; i < ch.length; i++) {
                if (i == 0 || i == 1)
                    gun += ch[i];

                if (i == 3 || i == 4)
                    ay += ch[i];

                if (i == 6 || i == 7 || i == 8 || i == 9)
                    yil += ch[i];


            }

            return yil+"-"+ay+"-"+gun;
        }

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
                        .appendQueryParameter("ad", ad)
                        .appendQueryParameter("soyad", soyad)
                        .appendQueryParameter("telefon", telefon)
                        .appendQueryParameter("tarih",tarih_formati())
                        .appendQueryParameter("kangrubuId",kangrubuId+"")
                        .appendQueryParameter("googleId",GoogleInfo.googleId)
                        .appendQueryParameter("cinsiyetId",cinsiyetId+"")
                        .appendQueryParameter("Mail",GoogleInfo.mail)
                        .appendQueryParameter("ImgUrl",GoogleInfo.Url);
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


                    new rememberEkleveAnaSayfaGec().execute("http://acilkan.iskenderunteknik.com/uygulama/googleIdvarmi.php?googleId="+GoogleInfo.googleId+"");


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

    class rememberEkleveAnaSayfaGec extends AsyncTask<String,String,String> {
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

                String sonuc = new JSONObject(s).getString("varmi");


                if(sonuc.equals("yok"))
                {
                    //daha once kayit yoksa
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);

                }else
                {
                    //daha once kayit yapildiysa
                    int UserId = Integer.parseInt(sonuc);
                    new Database(getApplicationContext(),"",null,1).rememberEkle(UserId);
                    new userinfo_doldur_ve_AnaSayfaya_Gec().execute("http://acilkan.iskenderunteknik.com/uygulama/UserInfoGetir.php?UserId="+UserId+"");
                    /*
                    Intent intent = new Intent(getApplicationContext(),AnaSayfa.class);
                    startActivity(intent);
                    */
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }



        }
    }
    class userinfo_doldur_ve_AnaSayfaya_Gec extends AsyncTask<String,String,String> {
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


                String sonuc = new JSONObject(s).getString("info");
                if(sonuc.equals("yok"))
                    return;

                UserInfo.Id=Integer.parseInt(new JSONObject(sonuc).getString("Id"));
                UserInfo.GoogleId=new JSONObject(sonuc).getString("GoogleId");
                UserInfo.Name=new JSONObject(sonuc).getString("Name");
                UserInfo.Surname=new JSONObject(sonuc).getString("Surname");
                UserInfo.Date=new JSONObject(sonuc).getString("Date");
                UserInfo.Phone=new JSONObject(sonuc).getString("Phone");
                UserInfo.BloodName=new JSONObject(sonuc).getString("BloodName");
                UserInfo.GenderName=new JSONObject(sonuc).getString("GenderName");
                UserInfo.Mail=new JSONObject(sonuc).getString("Mail");
                UserInfo.ImgUrl=new JSONObject(sonuc).getString("ImgUrl");
                UserInfo.IlanHaber=Integer.parseInt(new JSONObject(sonuc).getString("IlanHaber"));
                new Database(getApplicationContext(),"",null,1).izinEkle(UserInfo.Id,UserInfo.IlanHaber);
                new Database(getApplicationContext(),"",null,1).UserInfoDoldur(UserInfo.Id, UserInfo.GoogleId,UserInfo.Name,UserInfo.Surname,UserInfo.Date,UserInfo.Phone,UserInfo.BloodName,UserInfo.GenderName,UserInfo.Mail,UserInfo.ImgUrl,UserInfo.IlanHaber);



                Intent intent = new Intent(getApplicationContext(),AnaSayfa.class);
                startActivity(intent);


            }catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }



        }
    }




    class cinsiyet_doldur extends AsyncTask<String, String, String> {

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


                ArrayList<String> cinsiyetNameList= new ArrayList<>();
                cinsiyetIdList = new ArrayList<>();
                String[] satirlar = sonuc.split(";");
                for(int i=0;i<satirlar.length;i++)
                {
                    String[] satir = satirlar[i].split(":");
                    cinsiyetIdList.add(Integer.parseInt(satir[0]));
                    cinsiyetNameList.add(satir[1]);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, cinsiyetNameList);
                adapter.setDropDownViewResource(R.layout.spinner_item);

                spinnerCinsiyet.setAdapter(adapter);

                spinnerCinsiyet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });




            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
            }

        }
    }



}
