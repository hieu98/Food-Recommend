package com.example.foodrecommend.api

import com.example.foodrecommend.data.Weather
import io.reactivex.rxjava3.core.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class APIcontroller {

    companion object {
        private var apiBuilder : API? = null
        fun getApiBuilder() : API{
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.weatherbit.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
            apiBuilder = retrofit.create(API::class.java)
            return apiBuilder as API
        }

        fun callWeather(lat : Double, lon : Double, key : String) : Observable<Weather>{
            return getApiBuilder().getWeather(lat, lon, key)
        }
    }
}