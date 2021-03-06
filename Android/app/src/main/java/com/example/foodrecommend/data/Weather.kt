package com.example.foodrecommend.data

data class Weather(
    val count: Int,
    val `data`: List<Data>
)

data class Data(
    val app_temp: Double,
    val aqi: Int,
    val city_name: String,
    val clouds: Int,
    val country_code: String,
    val datetime: String,
    val dewpt: Double,
    val dhi: Double,
    val dni: Double,
    val elev_angle: Double,
    val ghi: Double,
    val h_angle: Double,
    val lat: Double,
    val lon: Double,
    val ob_time: String,
    val pod: String,
    val precip: Int,
    val pres: Double,
    val rh: Int,
    val slp: Int,
    val snow: Int,
    val solar_rad: Double,
    val state_code: String,
    val station: String,
    val sunrise: String,
    val sunset: String,
    val temp: Int,
    val timezone: String,
    val ts: Int,
    val uv: Double,
    val vis: Int,
    val weather: WeatherX,
    val wind_cdir: String,
    val wind_cdir_full: String,
    val wind_dir: Int,
    val wind_spd: Int
)

data class WeatherX(
    val code: Int,
    val description: String,
    val icon: String
)