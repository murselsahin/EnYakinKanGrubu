package com.example.sahin.enyakinkangrubu;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahin on 18.12.2017.
 */

public class IlanServis extends Service {
    Runnable runnable;
    Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        runnable = new Runnable() {
            @Override
            public void run() {
                Database db=new Database(getApplicationContext(),"",null,1);
                if(db.remember_Varmi()) {
                    int UserId=db.userid_getir();
                    if(db.izinGetir(UserId)==1)
                    {
                        //bildirim();
                        //Toast.makeText(MainActivity.context,"İlan Haber Servisi Çalışıyor",Toast.LENGTH_LONG).show();
                        if(!db.coordinat_bosmu()) {
                            ilangetir();
                        }
                        handler.postDelayed(this, 30000);
                    }else
                    {
                        handler.removeCallbacks(runnable);
                        Intent i = new Intent(getApplicationContext(),IlanServis.class);
                        stopService(i);
                    }
                }else
                {
                    handler.removeCallbacks(runnable);
                    Intent i = new Intent(getApplicationContext(),IlanServis.class);
                    stopService(i);
                }


            }
        };
        handler.post(runnable);
    }
    private void ilangetir()
    {

        Database db=new Database(getApplicationContext(),"",null,1);
        String UserId=db.userid_getir()+"";
        /////
        String koordinat = db.coordinat_getir();
        String[] coordinates =koordinat.split(" ");
        String Longitude = coordinates[0];
        String Latitude = coordinates[1];
        //////
       int BloodGroupId=-1;
        String BloodName="";
        SQLiteDatabase db_oku1= db.getReadableDatabase();
        String sql1="select BloodName from USERINFO where UserId="+Integer.parseInt(UserId)+"";
        Cursor cursor1=db_oku1.rawQuery(sql1,null);
        while (cursor1.moveToNext())
            BloodName=cursor1.getString(0);

        /////////////////////////////////
         SQLiteDatabase db_oku2= db.getReadableDatabase();
        List<String> listBloodGroupName = new ArrayList<>();
        List<Integer> listBloodGroupId =  new ArrayList<>();
        String sql2="select Id,Name from BLOODGROUPS";
        Cursor cursor2=db_oku2.rawQuery(sql2,null);
        while (cursor2.moveToNext())
        {
            listBloodGroupName.add(cursor2.getString(1));
            listBloodGroupId.add(cursor2.getInt(0));
        }



        for(int i=0;i< listBloodGroupName.size();i++)
        {
            if(listBloodGroupName.get(i).equals(BloodName))
            {
                BloodGroupId=listBloodGroupId.get(i);
                break;
            }
        }
        ////////////
        String engellenenIlanId="";
        SQLiteDatabase db_oku = db.getReadableDatabase();
        String sql="select IlanId from ILANLAR";
        Cursor cursor = db_oku.rawQuery(sql,null);
        if(cursor.getCount()==0)
            engellenenIlanId="-1";
        else
        {
            while (cursor.moveToNext())
            {
                engellenenIlanId+=cursor.getString(0)+";";
            }
        }
        ///////////////////////////////



        String url="http://acilkan.iskenderunteknik.com/uygulama/yakinlarda_ilan_varmi.php?UserId="+UserId+"&Longitude="+Longitude+"&Latitude="+Latitude+"&BloodGroupId="+BloodGroupId+"&EngellenenIlanId="+engellenenIlanId+"";

        new yakinlardaki_ilanlari_getir().execute(url);

    }
    class yakinlardaki_ilanlari_getir extends AsyncTask<String, String, String> {

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
                    return;

                String IlanId=new JSONObject(sonuc).getString("IlanId");
                String IlanUserId=new JSONObject(sonuc).getString("IlanUserId");
                String UserName=new JSONObject(sonuc).getString("UserName");
                String UserPhone=new JSONObject(sonuc).getString("UserPhone");
                String HospitalName=new JSONObject(sonuc).getString("HospitalName");
                String BloodName=new JSONObject(sonuc).getString("BloodName");
                String Declaration=new JSONObject(sonuc).getString("Declaration");
                String Date=new JSONObject(sonuc).getString("Date");
                String Longitude=new JSONObject(sonuc).getString("Longitude");
                String Latitude=new JSONObject(sonuc).getString("Latitude");
                String Uzaklik=new JSONObject(sonuc).getString("Uzaklik");

                String[] dIlanId=IlanId.split(";");
                String[] dIlanUserId=IlanUserId.split(";");
                String[] dUserName=UserName.split(";");
                String[] dUserPhone=UserPhone.split(";");
                String[] dHospitalName=HospitalName.split(";");
                String[] dBloodName=BloodName.split(";");
                String[] dDeclaration=Declaration.split(";");
                String[] dDate=Date.split(";");
                String[] dLongitude=Longitude.split(";");
                String[] dLatitude=Latitude.split(";");
                String[] dUzaklik=Uzaklik.split(";");

                Database db = new Database(getApplicationContext(),"",null,1);
                int userId=db.userid_getir();

                for(int i=0;i<dIlanId.length;i++)
                {
                    SQLiteDatabase db_yaz = db.getWritableDatabase();
                    double uzaklik = Double.parseDouble(dUzaklik[i]);
                    String yuzaklik = new DecimalFormat("##.##").format(uzaklik);
                    String sql="insert into ILANLAR(IlanId,KendiUserId,IlanUserId,UserName,UserPhone,HospitalName,BloodName,Declaration,Date,Longitude,Latitude,Uzaklik) values("+dIlanId[i]+","+userId+","+dIlanUserId[i]+",'"+dUserName[i]+"','"+dUserPhone[i]+"','"+dHospitalName[i]+"','"+dBloodName[i]+"','"+dDeclaration[i]+"','"+dDate[i]+"','"+dLongitude[i]+"','"+dLatitude+"','"+yuzaklik+"');";
                    db_yaz.execSQL(sql);
                }
                double uzaklik = Double.parseDouble(dUzaklik[dUzaklik.length-1]);
                String yuzaklik = new DecimalFormat("##.##").format(uzaklik);

                String[] tumtarih =  dDate[dDate.length-1].split(" ");
                String saat = tumtarih[1];
                String[] tarih = tumtarih[0].split("-");
                String yil=tarih[0];
                String ay = tarih[1];
                String gun = tarih[2];


                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                notificationBuilder.setContentTitle(yuzaklik+" Km Yakınınızda İlan Var");
                notificationBuilder.setContentText(gun+"."+ay+"."+yil+" "+saat+" tarihinde "+dBloodName[dBloodName.length-1]+" ilanı verildi. Bilgiler için Tıklayınız");
                notificationBuilder.setTicker("");

                Intent resultIntent = new Intent(getApplicationContext(), KanIlaniAyrinti.class);
                resultIntent.putExtra("Id",dIlanId[dIlanId.length-1]);
                resultIntent.putExtra("UserId",dIlanUserId[dIlanId.length-1]);
                resultIntent.putExtra("HospitalName",dHospitalName[dIlanId.length-1]);
                resultIntent.putExtra("Declaration",dDeclaration[dIlanId.length-1]);
                resultIntent.putExtra("Date",dDate[dIlanId.length-1]);
                resultIntent.putExtra("Longitude",dLongitude[dIlanId.length-1]);
                resultIntent.putExtra("Latitude",dLatitude[dIlanId.length-1]);
                resultIntent.putExtra("UserName",dUserName[dIlanId.length-1]);
                resultIntent.putExtra("UserDate","qweqwe");
                resultIntent.putExtra("UserPhone",dUserPhone[dIlanId.length-1]);
                resultIntent.putExtra("GenderName","wqeqwe");
                resultIntent.putExtra("Uzaklik",dUzaklik[dIlanId.length-1]);
                resultIntent.putExtra("BloodName",dBloodName[dIlanId.length-1]);
                TaskStackBuilder TSB = TaskStackBuilder.create(getApplicationContext());
                TSB.addParentStack(KanIlaniAyrinti.class);
                TSB.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = TSB.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationBuilder.setAutoCancel(true);
                NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(11221, notificationBuilder.build());





            }catch (Exception e)
            {

            }

        }
    }
    private void bildirim()
    {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentTitle("Denemee");
        notificationBuilder.setContentText("Denemeeee");
        notificationBuilder.setTicker("");

        Intent resultIntent = new Intent(getApplicationContext(), AnaSayfa.class);
        TaskStackBuilder TSB = TaskStackBuilder.create(getApplicationContext());
        TSB.addParentStack(AnaSayfa.class);
        TSB.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = TSB.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(11221, notificationBuilder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent ıntent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}
