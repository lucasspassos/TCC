package com.example.monitoramento.services;

import com.example.monitoramento.data.model.ResumoModelo;
import com.example.monitoramento.data.model.VeiculoModelo;
import com.example.monitoramento.response.VeiculoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ResumoService {

    @POST("ResumoUltimaViagem")
    Call<ResumoModelo> salvarResumo(@Body ResumoModelo resumoModelo);

    @GET("ResumoUltimaViagem")
    Call<ResumoModelo> ultimoResumo(@Body ResumoModelo resumoModelo);

}
