package com.example.monitoramento.services;

import com.example.monitoramento.Veiculo;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit getRetrofit(){

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://apimonitoramento-env.eba-2jhzevmf.us-east-1.elasticbeanstalk.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();


        return  retrofit;
    }

    public static UsuarioService getUsuarioService(){
        UsuarioService usuarioService = getRetrofit().create(UsuarioService.class);
        return usuarioService;
    }

    public static VeiculoService getVeiculoService(){
        VeiculoService veiculoService = getRetrofit().create(VeiculoService.class);
        return veiculoService;
    }

}
