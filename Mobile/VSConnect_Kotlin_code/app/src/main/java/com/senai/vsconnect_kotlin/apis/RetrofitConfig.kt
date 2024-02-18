package com.senai.vsconnect_kotlin.apis

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConfig {
    companion object{
//        fun obterInstanciaRetrofit(url: String = "http://apisecurepasscenter.azurewebsites.net/") : Retrofit{
        fun obterInstanciaRetrofit(url: String = "http://192.168.18.16:8081/") : Retrofit{
            return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}