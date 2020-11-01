package com.example.monitoramento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monitoramento.data.model.UsuarioModelo;
import com.example.monitoramento.response.UsuarioResponse;
import com.example.monitoramento.services.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cadastro extends AppCompatActivity {

    private EditText txtUsuario;
    private EditText txtSenha;
    private EditText txtConfirmaSenha;
    private EditText txtNome;
    private Button btnCadastrar;
    private CheckBox checkBoxTermos;
    private TextView termos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_cadastro);
        txtUsuario = (EditText) findViewById(R.id.txt_email);
        txtSenha = (EditText) findViewById(R.id.txt_senha);
        txtConfirmaSenha = (EditText) findViewById(R.id.txt_confirmar_senha);
        txtNome = (EditText) findViewById(R.id.txt_nome);
        btnCadastrar = (Button) findViewById(R.id.btn_cadastrar);
        checkBoxTermos = (CheckBox) findViewById(R.id.checkbox_termos);
        termos = (TextView) findViewById(R.id.lbl_termos);

        termos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(getApplicationContext(), Termos.class);
                    startActivity(intent);

                }catch (Exception e){

                    Toast.makeText(Cadastro.this, "Ocorreu um erro ao abrir os termos", Toast.LENGTH_LONG).show();
                }

            }
        });

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = txtUsuario.getText().toString();
                String senha = txtSenha.getText().toString();
                String confirmaSenha = txtConfirmaSenha.getText().toString();
                String nome = txtNome.getText().toString();
                boolean termos = checkBoxTermos.isChecked();

                if(!senha.equals("") && !usuario.equals("") && !confirmaSenha.equals("") && !nome.equals("")){
                    if(senha.equals(confirmaSenha))
                    {
                        if(termos){
                            try {
                                UsuarioModelo usuarioModelo = new UsuarioModelo();
                                usuarioModelo.setNome(nome);
                                usuarioModelo.setEmail(usuario);
                                usuarioModelo.setSenha(senha);
                                salvarUsuario(usuarioModelo);


                            }catch (Exception e){

                                Toast.makeText(Cadastro.this, "Ocorreu um erro ao iniciar o app", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(Cadastro.this, "É preciso aceitar os termos para continuar", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(Cadastro.this, "As senhas não conferem!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(Cadastro.this, "Todos os campos são obrigatório!", Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    public void salvarUsuario(UsuarioModelo usuarioModelo){

        Call<UsuarioResponse> usuarioResponseCall = ApiClient.getUsuarioService().salvarUsuario(usuarioModelo);
        usuarioResponseCall.enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {
                if(response.code() == 200)
                if(response.isSuccessful()){
                    salvarDadosLocal(response.body());
                    Intent intent = new Intent(getApplicationContext(), Veiculo.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(Cadastro.this,"Não foi cadastrado o usuario", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                Toast.makeText(Cadastro.this,"Não foi cadastrado o usuario", Toast.LENGTH_LONG).show();
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