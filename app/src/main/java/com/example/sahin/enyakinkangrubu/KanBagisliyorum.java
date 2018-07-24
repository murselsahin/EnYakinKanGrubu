package com.example.sahin.enyakinkangrubu;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sahin.enyakinkangrubu.ButtonEffect.Effect;

public class KanBagisliyorum extends AppCompatActivity {
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
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), AnaSayfa.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kan_bagisliyorum);
        ChatInfo.chat_sayfasi_acikmi=false;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        menu_butonlari();

        Button btn_bagis_bilgilerim=(Button)findViewById(R.id.bbagisbilgilerim);
        Button btn_kanilanlarina_gozat = (Button)findViewById(R.id.bkanilanlarinagozat);

        Effect.buttonEffect( btn_bagis_bilgilerim);
        Effect.buttonEffect( btn_kanilanlarina_gozat);


        btn_kanilanlarina_gozat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),KanIlanlarinaGozat.class);
                startActivity(intent);
            }
        });

        btn_bagis_bilgilerim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),BagisBilgilerim.class);
                startActivity(i);
            }
        });


    }
}
