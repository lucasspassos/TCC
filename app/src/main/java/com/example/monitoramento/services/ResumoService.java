package com.example.monitoramento.services;

import com.example.monitoramento.data.model.ResumoModelo;
import com.example.monitoramento.data.model.VeiculoModelo;
import com.example.monitoramento.response.VeiculoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ResumoService {

    @POST("ResumoUltimaViagem")
    Call<ResumoModelo> salvarResumo(@Body ResumoModelo resumoModelo);

    @GET("ResumoUltimaViagem/{id}")
    Call<ResumoModelo> ultimoResumo(@Path("id") String idUsuario);


}
