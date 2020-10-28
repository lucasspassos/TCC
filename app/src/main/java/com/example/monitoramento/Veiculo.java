package com.example.monitoramento;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monitoramento.data.model.UsuarioModelo;
import com.example.monitoramento.data.model.VeiculoModelo;
import com.example.monitoramento.response.UsuarioResponse;
import com.example.monitoramento.response.VeiculoResponse;
import com.example.monitoramento.services.ApiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Veiculo extends AppCompatActivity {

    private TextView marca;
    private TextView ano;
    private TextView modelo;
    private NumberPicker np;
    private Button btnFinalizar;
    private ImageView img_car;
    private static  final int gallery_request_code = 222;

    /**
     * Diretótio onde vamos salvar
     */
    private static final String DIRETORIO = "/appTCC/imagens/";

    /**
     * Código de retrono da permissão
     */
    private static final int CODE_PERMISSION = 12;

    /**
     * Código de retono da galeria
     */
    private static final int IMAGEM_INTERNA = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veiculo);
        getSupportActionBar().hide();
        btnFinalizar = (Button) findViewById(R.id.btn_finalizar);
        img_car = (ImageView) findViewById(R.id.img_car);
        np = (NumberPicker) findViewById(R.id.txt_ano);
        np.setMaxValue(2050);
        np.setMinValue(1950);
        np.setValue(2020);
        changeDividerColor(np, Color.parseColor("#00ffffff"));
        marca = (EditText) findViewById(R.id.txt_marca);
        modelo = (EditText) findViewById(R.id.txt_modelo);


        ImageView.class.cast(findViewById(R.id.img_car)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGEM_INTERNA);
            }
        });

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String marcaText = marca.getText().toString();
                    String modeloText = modelo.getText().toString();
                    int ano = np.getValue();

                    VeiculoModelo veiculoModelo = new VeiculoModelo();
                    veiculoModelo.setCod_usuario(Integer.parseInt(codigoUsuario()));
                    veiculoModelo.setMarca(marcaText);
                    veiculoModelo.setModelo(modeloText);
                    veiculoModelo.setAnoFabricacao(Integer.toString(ano));

                    salvarVeiculo(veiculoModelo);



                }catch (Exception e){

                    Toast.makeText(Veiculo.this, "Ocorreu um erro ao cadastrar o veículo", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //cehaca a permissão!
        checkPermission();
    }

    /**
     * Método responsável por verificar se o app possui a permissão de escrita e leitura
     */
    private void checkPermission() {
        // Verifica necessidade de verificacao de permissao
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Verifica necessidade de explicar necessidade da permissao
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this,"É necessário a  de leitura e escrita!", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE },
                        CODE_PERMISSION);
            } else {
                // Solicita permissao
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,  android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        CODE_PERMISSION);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == IMAGEM_INTERNA) {
            Uri imageData = data.getData();

            // criamos um File com o diretório selecionado!
            final File selecionada = new File(getRealPathFromURI(imageData));

            //Caso não exista o doretório, vamos criar!
            final File rootPath = new File(Environment.getExternalStorageDirectory() + DIRETORIO);
            if (!rootPath.exists()) {
                rootPath.mkdirs();
            }

            //Criamos um file, com o no DIRETORIO, com o mesmo nome da anterior
            final File novaImagem = new File(rootPath, selecionada.getName());

            Log.e("IMG", "IMG: "+ novaImagem.getName());

            SharedPreferences preferences = getSharedPreferences("IMGPATH", 0);
            SharedPreferences.Editor editor = preferences.edit();
            String nome = novaImagem.getName();
            editor.putString("NOMEIMG", nome);
            editor.commit();

            //Movemos o arquivo!
            try {
                moveFile(selecionada, novaImagem);

                // De acordo com o caminho da nova imagem, insere no TextView
                img_car.setImageURI(Uri.fromFile(novaImagem.getAbsoluteFile()));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Copia a imagem e remove o destino
     */
    private void moveFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }
        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
        //Alertamos, caso não consiga remover
        if(!sourceFile.delete()){
            Toast.makeText(getApplicationContext(), "Não foi possível remover a imagem!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Transforma o Uri em um diretório válido, para carregarmos em um arquivo
     * @param contentUri
     * @return
     */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void changeDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private String codigoUsuario(){

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.CodUsuario), Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences(getString(R.string.CodUsuario), Context.MODE_PRIVATE);
        String result = sharedPreferences.getString(getString(R.string.CodUsuario), "");
        return result;
    }

    public void salvarVeiculo(VeiculoModelo veiculoModelo){

        Call<VeiculoResponse> veiculoResponseCall = ApiClient.getVeiculoService().salvarVeiculo(veiculoModelo);
        veiculoResponseCall.enqueue(new Callback<VeiculoResponse>() {
            @Override
            public void onResponse(Call<VeiculoResponse> call, Response<VeiculoResponse> response) {
                if(response.code() == 200)
                    if(response.isSuccessful()){
                        Toast.makeText(Veiculo.this,"Cadastro Concluído", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(Veiculo.this,"Não foi cadastrado o veiculo", Toast.LENGTH_LONG).show();
                    }
            }

            @Override
            public void onFailure(Call<VeiculoResponse> call, Throwable t) {
                Toast.makeText(Veiculo.this,"Não foi cadastrado o veiculo", Toast.LENGTH_LONG).show();
            }
        });
    }
}