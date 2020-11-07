package com.example.monitoramento.data.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.monitoramento.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class ResumoModelo {

    private int codigoUsuario;
    private double consumoMedio;
    private double notaConducao;
    private int distancia;
    private double nivelTanque;
    private double temperaturaMaxima;
    private double velocidadeMaxima;
    private int avarias;
    private String dataInicio;
    private String dataTermino;

    public int getAvarias() {
        return avarias;
    }

    public void setAvarias(int avarias) {
        this.avarias = avarias;
    }

    public int getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(int codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public double getConsumoMedio() {
        return consumoMedio;
    }

    public void setConsumoMedio(double consumoMedio) {
        this.consumoMedio = consumoMedio;
    }

    public double getNotaConducao() {
        return notaConducao;
    }

    public void setNotaConducao(double notaConducao) {
        this.notaConducao = notaConducao;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    public double getNivelTanque() {
        return nivelTanque;
    }

    public void setNivelTanque(double nivelTanque) {
        this.nivelTanque = nivelTanque;
    }

    public double getTemperaturaMaxima() {
        return temperaturaMaxima;
    }

    public void setTemperaturaMaxima(double temperaturaMaxima) {
        this.temperaturaMaxima = temperaturaMaxima;
    }

    public double getVelocidadeMaxima() {
        return velocidadeMaxima;
    }

    public void setVelocidadeMaxima(double velocidadeMaxima) {
        this.velocidadeMaxima = velocidadeMaxima;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(String dataTermino) {
        this.dataTermino = dataTermino;
    }

    public ResumoModelo (){
        this.dataInicio = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        this.dataInicio = this.dataInicio.replace(' ', 'T');
    }



}
