package com.example.monitoramento.services;

import com.example.monitoramento.data.model.VeiculoModelo;
import com.example.monitoramento.response.VeiculoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface VeiculoService {

    @POST("Veiculo")
    Call<VeiculoResponse> salvarVeiculo(@Body VeiculoModelo veiculoModelo);
}
