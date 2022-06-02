package com.client.appM;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.client.appM.Service.AppSignatureHelper;

public class MainActivity extends AppCompatActivity {
    private ImageButton imgP;
    private int cont;
    private int cambio=1;
    private TextView contad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cont = 0;
        imgP = (ImageButton) findViewById(R.id.imgPist);
        contad = (TextView) findViewById(R.id.numPist);

        //Esconder la barra superior de la APP
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //AppSignatureHelper appSignatureHashHelper = new AppSignatureHelper(MainActivity.this);
        //Log.d("MAIN", "HashKey: " + appSignatureHashHelper.getAppSignatures().get(0));
        //codigo de la app : F6oWIQlQ+AE

        if (comprobarPermisoDado()==0) {
            Toast.makeText(MainActivity.this, "Permiso denegado", Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(MainActivity.this, "Permiso concedido", Toast.LENGTH_SHORT).show();


    }


    // Metodo para comprobar si se ha concedido el permiso de accesibilidad.
    public int comprobarPermisoDado() {
        int valorPerm = 0;
        try {
            valorPerm = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            String err= e.toString();
            Log.d("MAIN", "Valor del error:"+err);
        }
        if (valorPerm==0) { //Si no se ha concedido el permiso
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);  //comenzamos a pedir la actividad para el permiso de Accessib
            return 0;
        } else return 1;
    }

    public void onCambioImg(View view) {
        if(cambio==3){
            imgP.setImageResource(R.drawable.pistachocerrado);
            cambio=1;
            cont++;
        }else if (cambio==2){
            imgP.setImageResource(R.drawable.pistachovacio);
            cambio=3;
            cont++;
        }else if (cambio==1){
            imgP.setImageResource(R.drawable.pistachomedio);
            cambio=2;
            cont++;
        }
        contad.setText(String.valueOf((cont/3)));
    }
}