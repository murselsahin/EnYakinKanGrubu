package com.example.sahin.enyakinkangrubu;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.sahin.enyakinkangrubu.Info.BloodInfo;
import com.example.sahin.enyakinkangrubu.Info.GoogleInfo;
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
import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    static private ChatArrayAdapter chatArrayAdapter;
    static private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false; //false sol taraf
    static String UserId;
    ListView list_chat;
    public static Context context;
    List<String> gonderenUserId,Message,MesajId,aliciUserId;
    public static List<String> Date;
    String currentTime,sgonderenUserId,saliciUserId,sMessage;
    int basim_sayisi=2;
    String Name,Surname,ImgUrl;
    String bilgi="";
    @Override
    public void onBackPressed() {
        if(bilgi.equals("bagisayrinti"))
        {
            ChatInfo.chat_sayfasi_acikmi=false;
            super.onBackPressed();
        }else {
            ChatInfo.chat_sayfasi_acikmi=false;
            Intent i = new Intent(getApplicationContext(), Mesajlarim.class);
            startActivity(i);
        }
    }
    public TextView textName;
    public ImageView imagepic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        context=this;
            bilgi=getIntent().getExtras().getString("bilgi");



        textName=(TextView)findViewById(R.id.chatname);
        imagepic=(ImageView) findViewById(R.id.chatimage);

        buttonSend=(Button)findViewById(R.id.send);
        chatText=(EditText)findViewById(R.id.msg);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chatText.getText().toString().length()>0) {

                    java.util.Date dt = new java.util.Date();

                    java.text.SimpleDateFormat sdf =
                            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    if(chatText.getText().toString().length()==0)
                        return;

                    currentTime = sdf.format(dt);
                    sMessage=chatText.getText().toString().replace(';',' ');
                    sgonderenUserId=UserInfo.Id+"";
                    saliciUserId=UserId;
                    new mesajekle().execute("http://acilkan.iskenderunteknik.com/uygulama/mesajekle2.php");
                    chatText.setText("");
                }




            }
        });

        ChatInfo.chat_sayfasi_acikmi=true;

        gonderenUserId=new ArrayList<>();
        Message = new ArrayList<>();
        Date = new ArrayList<>();
        MesajId = new ArrayList<>();
        aliciUserId = new ArrayList<>();

        //ChatServisDurumu.durum=false;

        context=this;
        UserId = getIntent().getExtras().getString("UserId");

        listView = (ListView) findViewById(R.id.msgview);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);


        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        listView.setAdapter(chatArrayAdapter);

        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
        final String[] aylar={"Ocak","Şubat","Mart","Nisan","Mayıs","Haziran","Temmuz","Ağustos","Eylül","Ekim","Kasım","Aralık"};
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String hepsi = Date.get(i);
                String[] dizi = hepsi.split(" ");
                String saat = dizi[1];
                String[] tarih = dizi[0].split("-");
                String yil=tarih[0];
                String ay = tarih[1];
                String gun = tarih[2];


               Toast.makeText(getApplicationContext(),gun+" "+aylar[Integer.parseInt(ay)-1]+" "+yil+" "+saat,Toast.LENGTH_LONG).show();
            }
        });



        new userinfo_doldur().execute("http://acilkan.iskenderunteknik.com/uygulama/UserInfoGetir.php?UserId="+UserId+"");


        MesajGuncelle();
    }
    public static void MesajGuncelle()
    {
        SQLiteDatabase db =  new Database(context,"",null,1).getReadableDatabase();
        String sql="SELECT gonderenUserId,aliciUserId,Message,Date,MesajId from MESAJLAR where (aliciUserId="+UserId+" or gonderenUserId="+UserId+") AND UserId="+UserInfo.Id+" order by Date ASC";
        Cursor cursor = db.rawQuery(sql,null);

        Date = new ArrayList<>();
        chatArrayAdapter = new ChatArrayAdapter(context, R.layout.right);

        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
        while (cursor.moveToNext())
        {


            if(cursor.getString(0).equals(UserInfo.Id+""))
            {
                chatArrayAdapter.add(new ChatMessage(true,cursor.getString(2)));
            }else
            {
                chatArrayAdapter.add(new ChatMessage(false,cursor.getString(2)));
            }

            Date.add(cursor.getString(3));

         }

        listView.setAdapter(chatArrayAdapter);



    }
    class mesajekle extends AsyncTask<String, String, String> {



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
                        .appendQueryParameter("gonderenUserId",sgonderenUserId)
                        .appendQueryParameter("aliciUserId", saliciUserId)
                        .appendQueryParameter("Message",sMessage);
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
              //  Toast.makeText(Chat.this, s, Toast.LENGTH_LONG).show();
                String sonuc = new JSONObject(s).getString("bilgi");
                if(sonuc.equals("eklenmedi"))
                    return;




                Database db=new Database(getApplicationContext(),"",null,1);

                int UserId=db.userid_getir();
                String MesajId=new JSONObject(sonuc).getString("MesajId");
                String gonderenUserId=new JSONObject(sonuc).getString("gonderenUserId");
                String aliciUserId=new JSONObject(sonuc).getString("aliciUserId");
                String Date=new JSONObject(sonuc).getString("Date");
                String Message=new JSONObject(sonuc).getString("Message");




                //Toast.makeText(getApplicationContext(),"MesajId : "+MesajId +"gonderenUserId "+gonderenUserId + "aliciUserId :"+aliciUserId + " Date : "+gercekDate[0] +" Message :"+Message,Toast.LENGTH_LONG).show();
                db.MesajlaraEkle(UserId+"",gonderenUserId,Message,Date,MesajId,aliciUserId);

                MesajGuncelle();



            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
            }

        }
    }

    class userinfo_doldur extends AsyncTask<String,String,String> {
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


                Name=new JSONObject(sonuc).getString("Name");
                Surname=new JSONObject(sonuc).getString("Surname");
                ImgUrl=new JSONObject(sonuc).getString("ImgUrl");

                textName.setText(Name+" "+Surname);
                Glide.with(context).load(ImgUrl).asBitmap().centerCrop().into(new BitmapImageViewTarget(imagepic) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imagepic.setImageDrawable(circularBitmapDrawable);
                    }
                });


            }catch (Exception e)
            {
                e.printStackTrace();
            }



        }
    }


}
