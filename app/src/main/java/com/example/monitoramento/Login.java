package com.example.monitoramento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monitoramento.data.model.UsuarioModelo;
import com.example.monitoramento.response.UsuarioResponse;
import com.example.monitoramento.services.ApiClient;
import com.github.pires.obd.commands.protocol.EchoOffCommand;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                        UsuarioModelo usuarioModelo = new UsuarioModelo();
                        usuarioModelo.setEmail(usuario);
                        usuarioModelo.setSenha(senha);
                        logar(usuarioModelo);

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
    public void logar(UsuarioModelo usuarioModelo){

        Call<UsuarioResponse> usuarioResponseCall = ApiClient.getUsuarioService().logar(usuarioModelo);
        usuarioResponseCall.enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {
                if(response.code() == 200){
                    if(response.isSuccessful()){
                            salvarDadosLocal(response.body());
                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            startActivity(intent);
                    }
                    else{
                        Toast.makeText(Login.this,"Usuário ou senha incorreta", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(Login.this,"Usuário ou senha incorreta", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                Toast.makeText(Login.this,"Usuário ou senha incorreta", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void salvarDadosLocal(UsuarioResponse usuarioResponse){

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.CodUsuario), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.CodUsuario), usuarioResponse.getCodigo());
        editor.apply();


    }
}