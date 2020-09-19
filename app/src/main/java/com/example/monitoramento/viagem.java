package com.example.monitoramento;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class viagem extends AppCompatActivity {

    private TextView txtRpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viagem);

        txtRpm = (TextView)findViewById(R.id.rpm);

        Bundle dados = getIntent().getExtras();
        String rpm  = dados.getString("RPM");

        txtRpm.setText(rpm);
    }
}