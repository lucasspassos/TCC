package com.example.monitoramento.services;

import com.example.monitoramento.data.model.UsuarioModelo;
import com.example.monitoramento.response.UsuarioResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuarioService {

    @POST("Usuario")
   Call<UsuarioResponse> salvarUsuario(@Body UsuarioModelo usuarioModelo);

}
