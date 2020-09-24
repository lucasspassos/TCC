package com.example.monitoramento;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class Veiculo extends AppCompatActivity {

    private TextView txtRpm;
    private NumberPicker np;
    private Button btnFinalizar;
    private ImageView img_car;
    private static  final int gallery_request_code = 222;

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

        img_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "pick an image"),gallery_request_code );
                    startActivity(intent);
                }catch (Exception e){

                    Toast.makeText(Veiculo.this, "Ocorreu um erro ao escolher a imagem", Toast.LENGTH_LONG).show();
                }
            }
        });



        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                }catch (Exception e){

                    Toast.makeText(Veiculo.this, "Ocorreu um erro ao cadastrar o veículo", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gallery_request_code && resultCode == resultCode && data != null) {
            Uri imageData = data.getData();

            img_car.setImageURI(imageData);
        }
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
}