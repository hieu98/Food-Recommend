package com.example.foodrecommend.data

import java.io.Serializable
import java.util.*

data class CongThuc  (val image :String, var ten :String, var gioithieu :String, var ngaydang : String,
                      var nguoidang :String, val itemId :String, var rate :Float?): Serializable
