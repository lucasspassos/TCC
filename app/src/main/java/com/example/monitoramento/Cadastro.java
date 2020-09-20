package com.example.monitoramento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);

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
}