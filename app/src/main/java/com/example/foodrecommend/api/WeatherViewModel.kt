package com.example.foodrecommend.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foodrecommend.data.Weather
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.security.Key

class WeatherViewModel : ViewModel() {

    val requestWeather : MutableLiveData<Weather> = MutableLiveData()

    fun getApiWeather(lat : Double, lon : Double, key : String){
        val callWeather = APIcontroller.callWeather(lat, lon, key)
        callWeather
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                it.run {
                    requestWeather.postValue(it)
                }
            },{
                it.run {
                    Log.v("throw", it.message.toString())
                }
            })
    }
}