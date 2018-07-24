package com.example.sahin.enyakinkangrubu;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sahin.enyakinkangrubu.ButtonEffect.Effect;
import com.example.sahin.enyakinkangrubu.Info.BloodInfo;
import com.example.sahin.enyakinkangrubu.Info.GoogleInfo;
import com.example.sahin.enyakinkangrubu.Info.UserInfo;
import com.google.android.gms.auth.api.Auth;
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
import java.util.Calendar;
import java.util.List;

public class Ayarlar extends AppCompatActivity {
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

            }
        });
        Effect.buttonEffectiki(bkanariyorum);
        Effect.buttonEffectiki(bkanbagis);
        Effect.buttonEffectiki(bchat);
        Effect.buttonEffectiki(bayarlar);
    }
    private BroadcastReceiver broadcastReceiver;
    String ad,soyad,mail,telno,ilanhaber;
     EditText editMail,editSoyad,editTelno,editAd;
    CheckBox checkHaber;
    public List<String> bloodNameList;
    public List<Integer> bloodIdList;
    public Spinner sBloodGroup;
    TextView textTarih;
    String yil;
    String ay ;
    String gun;
    Button btn_konumuguncelle;
    int kangrubuSecilen;
    Context context;
    String[] aylar={"Ocak","Şubat","Mart","Nisan","Mayıs","Haziran","Temmuz","Ağustos","Eylül","Ekim","Kasım","Aralık"};
    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), AnaSayfa.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar);
        ChatInfo.chat_sayfasi_acikmi=false;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        menu_butonlari();
        context=this;
        sBloodGroup=(Spinner)findViewById(R.id.spinner2);
        kan_gruplari_doldur();
        btn_konumuguncelle=(Button)findViewById(R.id.butonkonumuguncelle);

        Button btnTarih = (Button)findViewById(R.id.button6);

        editAd = (EditText)findViewById(R.id.editText);
        editSoyad = (EditText)findViewById(R.id.editText3);
         editMail = (EditText)findViewById(R.id.editText4);
        textTarih =  (TextView)findViewById(R.id.textView21);
        editTelno = (EditText)findViewById(R.id.editText5);
        checkHaber = (CheckBox) findViewById(R.id.checkBox);

        editAd.setText(UserInfo.Name);
        editSoyad.setText(UserInfo.Surname);
        editMail.setText(UserInfo.Mail);
        editTelno.setText(UserInfo.Phone);
        if(UserInfo.IlanHaber==1)
            checkHaber.setChecked(true);
        else
            checkHaber.setChecked(false);

        String[] tarih = UserInfo.Date.split("-");
        yil=tarih[0];
        ay = tarih[1];
        gun = tarih[2];

        textTarih.setText(gun+" "+aylar[Integer.parseInt(ay)-1]+" "+yil);


        btnTarih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    // TODO Auto-generated method stub


                    DatePickerDialog datePicker;//Datepicker objemiz
                    datePicker = new DatePickerDialog(Ayarlar.this, new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {

                           monthOfYear++;

                           yil=year+"";
                           ay=monthOfYear+"";
                           gun=dayOfMonth+"";


                            textTarih.setText(dayOfMonth + " " + aylar[monthOfYear-1] + " " + year);//Ayarla butonu tıklandığında textview'a yazdırıyoruz

                        }
                    }, Integer.parseInt(yil), Integer.parseInt(ay), Integer.parseInt(gun));//başlarken set edilcek değerlerimizi atıyoruz
                    datePicker.setTitle("Tarih Seçiniz");
                    datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", datePicker);
                    datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", datePicker);

                    datePicker.show();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "" + ex, Toast.LENGTH_LONG).show();
                }
            }
        });

        Button btnBilgilerimiGuncelle = (Button)findViewById(R.id.butonbilgilerimiguncelle);
        btnBilgilerimiGuncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad = editAd.getText().toString().replace(';',' ');
                soyad=editSoyad.getText().toString().replace(';',' ');
                mail = editMail.getText().toString().replace(';',' ');
                telno=editTelno.getText().toString().replace(';',' ');
                 kangrubuSecilen = sBloodGroup.getSelectedItemPosition();

                 char[] ch = telno.toCharArray();
                 if(telno.length()!=10 || ch[0]=='0')
                 {
                     Toast.makeText(getApplicationContext(),"Telno 10 Haneli ve Başında 0 Olmadan Doldurunuz ",Toast.LENGTH_LONG).show();
                     return;
                 }

                if(Integer.parseInt(yil)>=2008)
                {
                    Toast.makeText(getApplicationContext(),"Geçerli Tarih Doldurunuz ",Toast.LENGTH_LONG).show();
                    return;
                }

                if(ad.length()==0 || soyad.length()==0 || telno.length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Bilgileri Doldurunuz ",Toast.LENGTH_LONG).show();
                    return;
                }
                if(kangrubuSecilen==-1)
                    return;


                if(isValidEmail(mail)) {

                }
                else {
                    Toast.makeText(getApplicationContext(), "Mail Yanlış", Toast.LENGTH_LONG).show();
                    return;
                }

                if(checkHaber.isChecked())
                    ilanhaber="1";
                else
                    ilanhaber="0";

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Uyarı");
                builder.setMessage("Bilgilerinizi Güncellensin Mi?");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setCancelable(false);

                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        new kullaniciguncelle().execute("http://acilkan.iskenderunteknik.com/uygulama/userUpdate.php");

             }
                      });

                builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();



            }
        });


        if(!runtime_permissions()) {
            enable_buttons();

        }
    }
    private void kan_gruplari_doldur()
    {
        bloodNameList= BloodInfo.listBloodGroupName;
        bloodIdList=BloodInfo.listBloodGroupId;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, bloodNameList);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        sBloodGroup.setAdapter(adapter);

        sBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

            int secilen=0;
            for(int i=0;i<bloodNameList.size();i++)
            {
                if(bloodNameList.get(i).equals(UserInfo.BloodName))
                {
                    secilen=i;
                    break;
                }
            }
            sBloodGroup.setSelection(secilen);

    }

    private boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    class kullaniciguncelle extends AsyncTask<String, String, String> {

        private String tarih_formati() {

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
                        .appendQueryParameter("Id", UserInfo.Id+"")
                        .appendQueryParameter("Name", ad)
                        .appendQueryParameter("Surname", soyad)
                        .appendQueryParameter("Date",tarih_formati())
                        .appendQueryParameter("Phone",telno)
                        .appendQueryParameter("BloodGroupId", bloodIdList.get(kangrubuSecilen)+"")
                        .appendQueryParameter("Mail",mail)
                        .appendQueryParameter("IlanHaber",ilanhaber);
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
                if(sonuc.equals("guncellendi"))
                {
                    UserInfo.Name=ad;
                    UserInfo.Surname=soyad;
                    UserInfo.Date=yil+"-"+ay+"-"+gun;
                    UserInfo.Phone=telno;
                    UserInfo.BloodName=bloodNameList.get(kangrubuSecilen);
                    UserInfo.Mail=mail;
                    UserInfo.IlanHaber=Integer.parseInt(ilanhaber);

                    new Database(getApplicationContext(),"",null,1).izinEkle(UserInfo.Id,UserInfo.IlanHaber);
                    new Database(getApplicationContext(),"",null,1).UserInfoDoldur(UserInfo.Id,UserInfo.GoogleId,UserInfo.Name,UserInfo.Surname,UserInfo.Date,UserInfo.BloodName,UserInfo.BloodName,UserInfo.GenderName,UserInfo.Mail,UserInfo.ImgUrl,UserInfo.IlanHaber);
                    Toast.makeText(getApplicationContext(),"Guncellendi",Toast.LENGTH_LONG).show();

                    if(UserInfo.IlanHaber==1) {
                        Intent iiservice = new Intent(Ayarlar.this,IlanServis.class);
                    startService(iiservice);}

                }else
                {
                    Toast.makeText(getApplicationContext(),"Guncellenemedi",Toast.LENGTH_LONG).show();

                }
            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {


                    String s = intent.getExtras().get("coordinates")+"";
                    new Database(getApplicationContext(),"",null,1).coordinatekle(s);
                    UserInfo.Coordinates=s;
                    Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                    stopService(i);

                    Toast.makeText(getApplicationContext(),"Konum Güncellendi", Toast.LENGTH_LONG).show();

                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }else {
                runtime_permissions();
            }
        }
    }

    private void enable_buttons() {

        btn_konumuguncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getApplicationContext(),GPS_Service.class);
                startService(i);

            }
        });


    }

}
