package com.example.monitoramento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class Home extends AppCompatActivity {

    private Button btnIniciar;
    private ImageView imagemVeiculo;

    /**
     * Diretótio onde vamos salvar
     */
    private static final String DIRETORIO = "/appTCC/imagens/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);
        btnIniciar = (Button) findViewById(R.id.btnIniciar);
        imagemVeiculo = (ImageView) findViewById(R.id.imgVeiculo);

        SharedPreferences preferences = getSharedPreferences("IMGPATH", 0);

        if(preferences.contains("NOMEIMG")){

            String nomeIMG = preferences.getString("NOMEIMG", "");

            File imgFile = new  File(Environment.getExternalStorageDirectory() + DIRETORIO + nomeIMG);

            Log.e("PHOTO","CAMINHO: " + imgFile.exists());

            // Se existir imagem, mostra no ImageView
            if(imgFile.exists()){

                imagemVeiculo.setImageURI(Uri.fromFile(imgFile));

            }

        }

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            try {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }catch (Exception e){

                Toast.makeText(Home.this, "Ocorreu um erro ao iniciar a viagem", Toast.LENGTH_LONG).show();
            }

            }
        });



    }
}