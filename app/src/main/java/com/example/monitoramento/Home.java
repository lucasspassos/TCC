package com.example.monitoramento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    private Button btnIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);
        btnIniciar = (Button) findViewById(R.id.btnIniciar);


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