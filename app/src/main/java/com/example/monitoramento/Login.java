package com.example.monitoramento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pires.obd.commands.protocol.EchoOffCommand;

public class Login extends AppCompatActivity {

    private EditText txtUsuario;
    private EditText txtSenha;
    private Button btnlogin;
    private TextView cadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        txtUsuario = (EditText) findViewById(R.id.txt_usuario);
        txtSenha = (EditText) findViewById(R.id.txt_senha);
        btnlogin = (Button) findViewById(R.id.btn_login);
        cadastrar = (TextView) findViewById(R.id.txt_criar_conta);


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = txtUsuario.getText().toString();
                String senha = txtSenha.getText().toString();

                if(!senha.equals("") && !usuario.equals("")){
                    try {
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);

                    }catch (Exception e){

                        Toast.makeText(Login.this, "Ocorreu um erro ao iniciar o app", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(Login.this, "Deve Digitar o usuário e a Senha!", Toast.LENGTH_LONG).show();
                }
            }
        });

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(getApplicationContext(), Cadastro.class);
                    startActivity(intent);

                }catch (Exception e){

                    Toast.makeText(Login.this, "Ocorreu um erro ao iniciar o app", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}