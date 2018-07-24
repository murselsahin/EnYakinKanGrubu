package com.example.sahin.enyakinkangrubu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.sahin.enyakinkangrubu.ButtonEffect.Effect;
import com.example.sahin.enyakinkangrubu.Info.BloodInfo;
import com.example.sahin.enyakinkangrubu.Info.GoogleInfo;
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

public class Mesajlarim extends AppCompatActivity {
    public static Context context;
    List<String> list_NameSurname,list_ImgUrl;
    List<Integer> list_UserId;
    ListView listViewOdalar;
    ImageView imageView;

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), AnaSayfa.class);
        startActivity(i);
    }

    private void menu_butonlari()
    {
        context=this;
        imageView = (ImageView)findViewById(R.id.denemeimage);
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
        setContentView(R.layout.activity_mesajlarim);
        context=this;
        ChatInfo.chat_sayfasi_acikmi=false;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        menu_butonlari();
        listViewOdalar=(ListView)findViewById(R.id.listodalar);

        List<Integer> list_userId = new Database(getApplicationContext(),"",null,1).odalari_getir(UserInfo.Id+"");
        String UserIds="";
        for(int i=0;i<list_userId.size();i++)
            UserIds+=list_userId.get(i)+";";

        new odalari_doldur().execute("http://acilkan.iskenderunteknik.com/uygulama/topluUserGetir.php?UserId="+UserIds+"");

    }

    class odalari_doldur extends AsyncTask<String, String, String> {

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

                list_NameSurname= new ArrayList<>();
                list_UserId = new ArrayList<>();
                list_ImgUrl = new ArrayList<>();

                String sNameSurname = new JSONObject(sonuc).getString("NameSurname");
                String sUserId = new JSONObject(sonuc).getString("UserId");
                String sImgUrl = new JSONObject(sonuc).getString("ImgUrl");

                String[] dNameSurname = sNameSurname.split(";");
                String[] dUserId = sUserId.split(";");
                String[] dImgUrl = sImgUrl.split(";");


                for(int i=0;i<dNameSurname.length;i++)
                {
                    list_NameSurname.add(dNameSurname[i]);
                    list_UserId.add(Integer.parseInt(dUserId[i]));
                    list_ImgUrl.add(dImgUrl[i]);
                }
            /* silinecek
                ArrayAdapter<String> adapter=new ArrayAdapter<String>
                        (getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, list_NameSurname);

                listViewOdalar.setAdapter(adapter);

                listViewOdalar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(getApplicationContext(),Chat.class);
                        intent.putExtra("UserId",list_UserId.get(i).toString());
                        startActivity(intent);
                    }
                });
                */
                ArrayList<OdaList> list = new ArrayList<>();
                for(int i=0;i<list_NameSurname.size();i++)
                {
                    OdaList o = new OdaList(list_NameSurname.get(i),list_ImgUrl.get(i));
                    list.add(o);

                }
                OdaListAdapter adapter = new OdaListAdapter(context,R.layout.adapter_mesajlarim_layout,list);
                listViewOdalar.setAdapter(adapter);

                listViewOdalar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(getApplicationContext(),Chat.class);
                        intent.putExtra("UserId",list_UserId.get(i).toString());
                        intent.putExtra("bilgi","qweqwe");
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
