package com.example.sahin.enyakinkangrubu;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.sahin.enyakinkangrubu.Info.UserInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sahin on 19.12.2017.
 */

public class ChatService extends Service {

    Runnable runnable;
    Handler handler = new Handler();

    String sonTarih="",UserId="",databaseBosmu="";
    @Override
    public void onCreate() {
        super.onCreate();
        runnable = new Runnable() {
            @Override
            public void run() {
                Database db=new Database(getApplicationContext(),"",null,1);
                if(db.remember_Varmi())
                {
                    UserId=db.userid_getir()+"";
                    databaseBosmu=db.mesajlarBosMu(db.userid_getir())+"";
                    sonTarih=db.sonTarihGetir(db.userid_getir());
                    /*
                    String engellenenMesajIdler = db.engellenenMesajIdleriGetir(UserId+"");
                    if(engellenenMesajIdler.length() == 0)
                        engellenenMesajIdler="-1";*/

                    String url="http://acilkan.iskenderunteknik.com/uygulama/tummesajlarigetir2.php";
                    //Toast.makeText(getApplicationContext(),url,Toast.LENGTH_SHORT).show();
                    new MesajlariGetir().execute(url);
                    handler.postDelayed(this, 8000);


                }else
                {
                    handler.removeCallbacks(runnable);
                    Intent i = new Intent(getApplicationContext(),ChatService.class);
                    stopService(i);
                }

            }
        };
        handler.post(runnable);

    }
    String gonderenUserId="";
    String sonMesaj="";

    class MesajlariGetir extends AsyncTask<String, String, String> {

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
                        .appendQueryParameter("sonTarih",sonTarih)
                        .appendQueryParameter("UserId", UserId)
                        .appendQueryParameter("databaseBosmu", databaseBosmu);

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

                    Database db=new Database(getApplicationContext(),"",null,1);
                    int uid=db.userid_getir();
                    int index=-1;
                    for(int i=dgonderenUserId.length-1;i>=0;i--)
                    {
                        if(Integer.parseInt(dgonderenUserId[i])!=uid)
                        {
                            index=i;
                            break;
                        }

                    }


                    for(int i=0;i<dId.length;i++)
                    {
                        new Database(getApplicationContext(),"",null,1).MesajlaraEkle(UserInfo.Id+"",dgonderenUserId[i],dMessage[i],dDate[i],dId[i],daliciUserId[i]);
                    }

                    if(ChatInfo.chat_sayfasi_acikmi==true)
                        Chat.MesajGuncelle();
                    else {
                        if(index!=-1) {
                            gonderenUserId = dgonderenUserId[index];
                            sonMesaj = dMessage[index];
                            new userinfo().execute("http://acilkan.iskenderunteknik.com/uygulama/UserInfoGetir.php?UserId=" + gonderenUserId + "");
                        }

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

                Intent resultIntent = new Intent(getApplicationContext(), Chat.class);
                resultIntent.putExtra("bilgi","qweqwe");
                resultIntent.putExtra("UserId",new JSONObject(sonuc).getString("Id"));
                TaskStackBuilder TSB = TaskStackBuilder.create(getApplicationContext());
                TSB.addParentStack(Chat.class);
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

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
    public IBinder onBind(Intent ıntent) {
        return null;
    }
}
