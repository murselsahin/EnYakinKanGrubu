package com.example.sahin.enyakinkangrubu;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sahin.enyakinkangrubu.ButtonEffect.Effect;

public class KanAriyorum extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), AnaSayfa.class);
        startActivity(i);

    }
    private void menu_butonlari()
    {
        Button bkanariyorum=(Button) findViewById(R.id.bmenuarama);
        Button bkanbagis=(Button)findViewById(R.id.bmenubagis);
        Button bchat = (Button)findViewById(R.id.bmenuchat);
        Button bayarlar = (Button)findViewById(R.id.bmenuayarlar);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kan_ariyorum);
        ChatInfo.chat_sayfasi_acikmi=false;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        menu_butonlari();
        Button btn_kanilaniver = (Button)findViewById(R.id.bkanilaniver);
        Button btn_ilaniniduzenle = (Button)findViewById(R.id.bilanlarinagozat);
        Button btn_bagiscilarabak = (Button)findViewById(R.id.bkanbagiscilarinagozat);

        Effect.buttonEffect(btn_kanilaniver);
        Effect.buttonEffect(btn_ilaniniduzenle);
        Effect.buttonEffect(btn_bagiscilarabak);
        btn_kanilaniver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Kanilanver.class);
                startActivity(intent);
            }
        });

        btn_ilaniniduzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),KanIlaniniDuzelt.class);
                startActivity(intent);
            }
        });

        btn_bagiscilarabak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  intent = new Intent(getApplicationContext(),BagiscilaraBak.class);
                startActivity(intent);
            }
        });

    }
}
