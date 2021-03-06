package com.example.foodrecommend.api

import com.example.foodrecommend.data.Weather
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface API {

    @GET("v2.0/current")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") key: String
    ): Observable<Weather>

    @GET("v2.0/current")
    suspend fun getWeather2(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") key: String
    ): Weather
}