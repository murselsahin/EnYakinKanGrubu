package com.example.sahin.enyakinkangrubu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sahin.enyakinkangrubu.Info.BloodInfo;
import com.example.sahin.enyakinkangrubu.Info.GoogleInfo;
import com.example.sahin.enyakinkangrubu.Info.UserInfo;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{

    private SignInButton SingIn;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;

    public static Context context;
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        context=this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ChatInfo.chat_sayfasi_acikmi=false;
        ChatInfo.chat_service=false;

        new Database(getApplicationContext(),"",null,1).getWritableDatabase();

        boolean remembervarmi = new Database(getApplicationContext(),"",null,1).remember_Varmi();


        if(BloodInfo.listBloodGroupId==null)
            new kangruplari_doldur().execute("http://acilkan.iskenderunteknik.com/uygulama/bloodgroup.php");


        SingIn=(SignInButton)findViewById(R.id.btn_login);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient  = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

        SingIn.setOnClickListener(this);

        if(remembervarmi) {
            int UserId = new Database(getApplicationContext(),"",null,1).userid_getir();
            new userinfo_doldur_ve_AnaSayfaya_Gec().execute("http://acilkan.iskenderunteknik.com/uygulama/UserInfoGetir.php?UserId="+UserId+"");
           // Intent intent = new Intent(getApplicationContext(), AnaSayfa.class);
            //startActivity(intent);
        }

    }


    @Override
    public void onClick(View view) {
        signIn();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn()
    {
        Intent intent  = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);

    }

    private void handleResult(GoogleSignInResult result)
    {
        if(result.isSuccess())
        {
            GoogleSignInAccount account = result.getSignInAccount();
            GoogleInfo.ad = account.getGivenName();
            GoogleInfo.soyad=account.getFamilyName();
            GoogleInfo.googleId=account.getId();
            GoogleInfo.mail= account.getEmail();
            if(account.getPhotoUrl()!=null)
                GoogleInfo.Url=account.getPhotoUrl().toString();
            else
                GoogleInfo.Url="https://pbs.twimg.com/profile_images/716487122224439296/HWPluyjs.jpg";



           new ArkaPlan().execute("http://acilkan.iskenderunteknik.com/uygulama/googleIdvarmi.php?googleId="+account.getId()+"");





        }
        else
        {

        }

    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQ_CODE)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }





    class ArkaPlan extends AsyncTask<String,String,String> {
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
                    Intent intent = new Intent(getApplicationContext(),KayitFormu.class);
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
            }



        }
    }

    class kangruplari_doldur extends AsyncTask<String, String, String> {

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


                ArrayList<String> bloodNameList= new ArrayList<>();
                ArrayList<Integer> bloodIdList = new ArrayList<>();
                String[] satirlar = sonuc.split(";");
                for(int i=0;i<satirlar.length;i++)
                {
                    String[] satir = satirlar[i].split(":");
                    bloodIdList.add(Integer.parseInt(satir[0]));
                    bloodNameList.add(satir[1]);
                }


                BloodInfo.listBloodGroupId=bloodIdList;
                BloodInfo.listBloodGroupName=bloodNameList;

                new Database(getApplicationContext(),"",null,1).BloodGroupDoldur(bloodIdList,bloodNameList);




            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
            }

        }
    }

}
