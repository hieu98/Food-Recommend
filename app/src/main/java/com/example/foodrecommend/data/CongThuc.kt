package com.example.foodrecommend.data

import java.io.Serializable

data class CongThuc  (val image :String, var ten :String, var gioithieu :String, var ngaydang : String,
                      var nguoidang :String, val itemId :String, var userId :String): Serializable
