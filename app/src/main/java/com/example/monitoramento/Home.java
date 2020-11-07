package com.example.monitoramento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

import com.example.monitoramento.data.model.ResumoModelo;
import com.example.monitoramento.data.model.UsuarioModelo;
import com.example.monitoramento.response.UsuarioResponse;
import com.example.monitoramento.services.ApiClient;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {

    private Button btnIniciar;
    private Button btnInicio;
    private Button btnTermino;
    private Button btnConsumo;
    private Button btnNota;
    private Button btnDistancia;
    private Button btnNivel;
    private Button btnAvarias;
    private Button btnVelocidade;
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
        btnInicio = (Button) findViewById(R.id.btn_data_inicio);
        btnTermino = (Button) findViewById(R.id.btn_data_termino);
        btnConsumo = (Button) findViewById(R.id.btn_consumo_medio);
        btnNota = (Button) findViewById(R.id.btn_nt_conducao);
        btnDistancia = (Button) findViewById(R.id.btn_distancia);
        btnNivel = (Button) findViewById(R.id.btn_nivel_tanque);
        btnAvarias = (Button) findViewById(R.id.btn_avarias);
        btnVelocidade = (Button) findViewById(R.id.btn_velocidade_maxima);


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

        buscarUltimoResumo();

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

    public void buscarUltimoResumo(){

        Call<ResumoModelo> resumoModeloCall = ApiClient.getResumoService().ultimoResumo(codigoUsuario());
        resumoModeloCall.enqueue(new Callback<ResumoModelo>() {
            @Override
            public void onResponse(Call<ResumoModelo> call, Response<ResumoModelo> response) {
                if(response.code() == 200)
                    if(response.isSuccessful()){
                        AtualizaDisplay(response.body());
                    }else{
                        Toast.makeText(Home.this,"Não existe resumo para o usuário", Toast.LENGTH_LONG).show();
                    }
            }

            @Override
            public void onFailure(Call<ResumoModelo> call, Throwable t) {
                Toast.makeText(Home.this,"Não foi cadastrado o usuario", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String codigoUsuario(){

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.CodUsuario), Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences(getString(R.string.CodUsuario), Context.MODE_PRIVATE);
        String result = sharedPreferences.getString(getString(R.string.CodUsuario), "");
        return result;
    }
    public void AtualizaDisplay(ResumoModelo resumoModelo){

        try{
            btnInicio.setText(resumoModelo.getDataInicio().replace('T',' '));
            btnTermino.setText(resumoModelo.getDataTermino().replace('T',' '));
            btnConsumo.setText(Double.toString(resumoModelo.getConsumoMedio()) + '%');
            btnNota.setText(Double.toString(resumoModelo.getNotaConducao()));
            btnDistancia.setText(Double.toString(resumoModelo.getDistancia()) + "km");
            btnNivel.setText(Double.toString(resumoModelo.getNivelTanque()) + '%');
            btnAvarias.setText(Integer.toString(resumoModelo.getAvarias()));
            btnVelocidade.setText(Double.toString(resumoModelo.getVelocidadeMaxima()) + "km/h");
        }catch (Exception e){
            Toast.makeText(Home.this,"Não foi possivel buscar o resumo", Toast.LENGTH_LONG).show();
        }


    }
}