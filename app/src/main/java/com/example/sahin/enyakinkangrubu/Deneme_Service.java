package com.example.sahin.enyakinkangrubu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.sahin.enyakinkangrubu.Info.BloodInfo;
import com.example.sahin.enyakinkangrubu.Info.UserInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sahin on 11.12.2017.
 */

public class Deneme_Service extends Service {
    Runnable runnable;
    Handler handler = new Handler();
    @Override
    public void onCreate() {
        super.onCreate();
        runnable = new Runnable() {
            @Override
            public void run() {
                if(ChatServisDurumu.durum==true) {
                    String engellenenMesajIdler = new Database(getApplicationContext(), "", null, 1).engellenenMesajIdleriGetir(UserInfo.Id + "");
                    if (engellenenMesajIdler.length() == 0) {
                        new Deneme().execute("http://acilkan.iskenderunteknik.com/uygulama/tummesajlarigetir.php?gelmeyecekMesajIdler=-1&UserId=" + UserInfo.Id + "");
                    } else {
                        new Deneme().execute("http://acilkan.iskenderunteknik.com/uygulama/tummesajlarigetir.php?gelmeyecekMesajIdler=" + engellenenMesajIdler + "&UserId=" + UserInfo.Id + "");
                    }
                }
                if(ChatInfo.chat_service==true) {

                    handler.postDelayed(this, 5000);
                }else
                {

                    handler.removeCallbacks(runnable);
                    Intent i = new Intent(getApplicationContext(),Deneme_Service.class);
                    stopService(i);
                }
            }
        };
            handler.post(runnable);






    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
    String sonMesaj="";
    @Nullable
    @Override
    public IBinder onBind(Intent ıntent) {
        return null;
    }
    class Deneme extends AsyncTask<String, String, String> {

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
        NotificationCompat.Builder notification;
        private static final int uniqueId=45612;

        @Override
        protected void onPostExecute(String s) {
            try {
                String sonuc = new JSONObject(s).getString("bilgi");
                if(sonuc.equals("yok"))
                {

                }else
                {
                    String Id = new JSONObject(sonuc).getString("Id");
                    String gonderenUserId = new JSONObject(sonuc).getString("gonderenUserId");
                    String aliciUserId = new JSONObject(sonuc).getString("aliciUserId");
                    String Date = new JSONObject(sonuc).getString("Date");
                    String Message = new JSONObject(sonuc).getString("Message");

                    String[] dId = Id.split(";");
                    String[] dgonderenUserId = gonderenUserId.split(";");
                    String[] daliciUserId = aliciUserId.split(";");
                    String[] dDate = Date.split(";");
                    String[] dMessage = Message.split(";");
                    sonMesaj=dMessage[0];
                    for(int i=0;i<dId.length;i++)
                    {
                        new Database(getApplicationContext(),"",null,1).MesajlaraEkle(UserInfo.Id+"",dgonderenUserId[i],dMessage[i],dDate[i],dId[i],daliciUserId[i]);
                    }

                    if(ChatInfo.chat_sayfasi_acikmi==true)
                        Chat.MesajGuncelle();
                    else {
                        new userinfo().execute("http://acilkan.iskenderunteknik.com/uygulama/UserInfoGetir.php?UserId="+dgonderenUserId[0]+"");

                    }

                }
            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
            }

        }
    }
    class userinfo extends AsyncTask<String,String,String> {
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

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                notificationBuilder.setContentTitle(new JSONObject(sonuc).getString("Name")+" "+new JSONObject(sonuc).getString("Surname"));
                notificationBuilder.setContentText(sonMesaj);
                notificationBuilder.setTicker("");

                Intent resultIntent = new Intent(getApplicationContext(), Mesajlarim.class);
                TaskStackBuilder TSB = TaskStackBuilder.create(getApplicationContext());
                TSB.addParentStack(Mesajlarim.class);
                TSB.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = TSB.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationBuilder.setAutoCancel(true);
                NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(11221, notificationBuilder.build());



            }catch (Exception e)
            {
                e.printStackTrace();
            }



        }
    }
}
