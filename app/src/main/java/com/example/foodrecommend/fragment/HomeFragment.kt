package com.example.foodrecommend.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.location.Location
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommend.R
import com.example.foodrecommend.activity.RecipeActivity
import com.example.foodrecommend.adapter.DanhSachApdater
import com.example.foodrecommend.adapter.RecommendAdapter
import com.example.foodrecommend.api.API
import com.example.foodrecommend.api.WeatherViewModel
import com.example.foodrecommend.data.CongThuc
import com.example.foodrecommend.data.Rate
import com.example.foodrecommend.data.Weather
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment(), DanhSachApdater.OnItemClickListener,
    RecommendAdapter.OnItemClickListener {

    private lateinit var listdata: ArrayList<CongThuc>
    private lateinit var listdatanew: ArrayList<CongThuc>
    private lateinit var listRate: ArrayList<Rate>
    private lateinit var recipeAdapter: DanhSachApdater
    private lateinit var recommendAdapter: RecommendAdapter

    private lateinit var mAuth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLastLocation: Location? = null
    private lateinit var myWeatherViewModel: WeatherViewModel
    private lateinit var pref: SharedPreferences
    private lateinit var cout : CountDownTimer
    private var job : Job? = null

    companion object {
        val key = "6a0dc7a1149d4ec68f1a4f3a67e2d318"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        pref = context?.getSharedPreferences("PREF", AppCompatActivity.MODE_PRIVATE)!!
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        databaseReference = database?.reference
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        var arraydata: List<Int>?

        val bundle = arguments
        val realid = bundle?.getString("realid")
        var dataget = ""

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val time = view.findViewById<TextView>(R.id.tv_tg)
        val session = view.findViewById<TextView>(R.id.txt_thoigian)
        val imgsession = view.findViewById<ImageView>(R.id.imgv_tg)
        val temperature = view?.findViewById<TextView>(R.id.tv_tt)
        val textweather = view?.findViewById<TextView>(R.id.txt_thoitiet)
        val imgweather = view?.findViewById<ImageView>(R.id.imgv_tt)

        val listgoiy = view.findViewById<RecyclerView>(R.id.listgoiy)
        val listmonmoi = view.findViewById<RecyclerView>(R.id.listmonmoi)
        cout = object : CountDownTimer(18000000,1000){
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                checkPermission(temperature!!,textweather!!, imgweather!!)
                Log.v("cout", "true")
                cout.start()
            }
        }

        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val formatted = current.format(formatter)
                val c: java.util.Calendar = java.util.Calendar.getInstance()
                val timeofday = c.get(Calendar.HOUR_OF_DAY)
                if (timeofday in 5..11) {
                    session.text = "Sáng"
                    Picasso.get().load(R.drawable.morning).into(imgsession)
                } else if (timeofday in 12..17) {
                    session.text = "Chiều"
                    Picasso.get().load(R.drawable.sun).into(imgsession)
                } else if (timeofday in 18..21) {
                    session.text = "Tối"
                    Picasso.get().load(R.drawable.moon).into(imgsession)
                } else if (timeofday >= 22 || timeofday < 5) {
                    session.text = "Đêm"
                    Picasso.get().load(R.drawable.latenight).into(imgsession)
                }
                time.text = formatted
                handler.postDelayed(this, 1000)
            }
        })

        databaseReference?.child("Rate")
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        val a = "" + data.child("userId").value.toString()
                        val b = "" + data.child("itemId").value.toString()
                        val c = "" + data.child("rate").value.toString()
                        if (c != "0") {
                            dataget += "$a $b $c\n"
                        }
                    }
                    val okHttpClient = OkHttpClient()
                    val formBody =
                        FormBody.Builder().add("uid", realid!!).add("data", dataget).build()
                    val request =
                        Request.Builder().url("http://192.168.0.101:3000/").post(formBody).build()
                    okHttpClient.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            activity?.runOnUiThread {
                                Log.v("okhttp error", "Network not found")
                                e.printStackTrace()
                            }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val okdata = response.body?.string()
                            arraydata = if (okdata == "a" || okdata == "[]") {
                                arrayListOf(0, 0)
                            } else {
                                okdata?.removeSurrounding("[", "]")?.replace(" ", "")?.split(",")
                                    ?.map { it.toInt() }
                            }

                            activity?.runOnUiThread {

                                recommendAdapter = RecommendAdapter(
                                    this@HomeFragment,
                                    listdatanew,
                                    listRate,
                                    requireContext(),
                                    arraydata!!
                                )
                                listgoiy.setHasFixedSize(true)
                                listgoiy.isNestedScrollingEnabled = false
                                listgoiy.adapter = recommendAdapter
                                listgoiy.layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )

                                getData(arraydata!!)
                                getRate()
                                for (i in arraydata!!.indices) {
                                    Log.v("arraydata $i", arraydata!![i].toString())
                                }
                            }

                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.v("error", error.message)
                }
            })

        listdata = ArrayList()
        listdatanew = ArrayList()
        listRate = ArrayList()
        recipeAdapter = DanhSachApdater(this@HomeFragment, listdata, listRate, requireContext())
        listmonmoi.setHasFixedSize(true)
        listmonmoi.isNestedScrollingEnabled = false
        listmonmoi.adapter = recipeAdapter
        listmonmoi.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        getDataOrder()

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        val temperature = view?.findViewById<TextView>(R.id.tv_tt)
        val textweather = view?.findViewById<TextView>(R.id.txt_thoitiet)
        val imgweather = view?.findViewById<ImageView>(R.id.imgv_tt)

        val temp = pref.getInt("temp" , 0)
        val code = pref.getString("code", "")
        val des = pref.getString("des", "")
        if (temp == 0 && code == "" && des == ""){
            checkPermission(temperature!!,textweather!!, imgweather!!)
        }else{
            temperature?.text = "$temp°C"
            textweather?.text = des
            Picasso.get().load("https://www.weatherbit.io/static/img/icons/$code.png").resize(60,60).into(imgweather)
        }
        cout.start()
    }

    private suspend fun fetchWeather (latitude : Double, longitude : Double) : Weather{
        return withContext(Dispatchers.IO) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.weatherbit.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(API::class.java)
            retrofit.getWeather2(latitude, longitude, key)
        }
    }

    suspend fun fetchAndShowWeather(latitude : Double, longitude : Double){
        val weather = fetchWeather(latitude,longitude)
        showWeather(weather)
    }

    private fun showWeather(weather: Weather) {
        Log.v("show temp", weather.data[0].temp.toString())
    }

    override fun onStop() {
        super.onStop()
        cout.cancel()
        //job?.cancel()

    }

    private fun checkPermission(texweather : TextView, textDescriptionWeather : TextView, img : ImageView){
        val withListener = Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                @SuppressLint("MissingPermission", "SetTextI18n")
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()) {
                        myWeatherViewModel = ViewModelProviders.of(requireActivity()).get(WeatherViewModel::class.java)
                        mFusedLocationProviderClient!!.lastLocation
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful && task.result != null) {
                                    mLastLocation = task.result
                                    myWeatherViewModel.getApiWeather(mLastLocation?.latitude!!, mLastLocation?.longitude!!, key)
                                    myWeatherViewModel.requestWeather.observe(requireActivity(),{
                                        it.run {
                                            val temp = pref.getInt("temp" , 0)
                                            val codeold = pref.getString("code", "")
                                            val code = this.data[0].weather.icon
                                            job = GlobalScope.launch {
                                                Log.v("corroo", "true")
                                                fetchAndShowWeather(mLastLocation?.latitude!!, mLastLocation?.longitude!!)
                                            }
                                            Log.v("texweather", this.data[0].temp.toString())
                                            if (temp != this.data[0].temp || codeold != code){
                                                texweather.text = this.data[0].temp.toString() + "°C"
                                                weatherDes(this.data[0].weather.code, textDescriptionWeather)
                                                Picasso.get().load("https://www.weatherbit.io/static/img/icons/$code.png").resize(60,60).into(img)
                                                pref.edit().putInt("temp",this.data[0].temp ).apply()
                                                pref.edit().putString("code", code).apply()
                                            }
                                        }
                                    })
                                }
                            }
                    } else
                        Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT)
                            .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1!!.continuePermissionRequest()
                }

            }).check()
    }

    private fun getData(arraydata: List<Int>) {
        var userId: String
        var ten: String
        var nguoidang: String
        var ngaydang: String
        var anhbia: String
        var gioithieu: String
        var itemId: String
        var congThuc: CongThuc

        databaseReference!!.child("Công Thức").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listdatanew.clear()
                for (data in snapshot.children) {
                    ten = "" + data.child("Tên Món Ăn").value.toString()
                    nguoidang = "" + data.child("Người đăng").value.toString()
                    ngaydang = "" + data.child("Ngày đăng").value.toString()
                    anhbia = "" + data.child("Ảnh bìa").value.toString()
                    gioithieu = "" + data.child("Giới thiệu món ăn").value.toString()
                    itemId = "" + data.child("ItemId").value.toString()
                    userId = "" + data.child("UserId").value.toString()

                    for (i in arraydata.indices) {
                        if (arraydata[i].toString() == itemId) {
                            congThuc = CongThuc(
                                anhbia,
                                ten,
                                gioithieu,
                                ngaydang,
                                nguoidang,
                                itemId,
                                userId
                            )
                            listdatanew.add(congThuc)
                        }
                    }
                }
                recommendAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.v("cancel", error.toString())
            }

        })
    }

    private fun getDataOrder() {
        var userId: String
        var ten: String
        var nguoidang: String
        var ngaydang: String
        var anhbia: String
        var gioithieu: String
        var itemId: String
        var congThuc: CongThuc
        databaseReference!!.child("Công Thức").orderByChild("TLM")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listdata.clear()
                    for (data in snapshot.children) {
                        ten = "" + data.child("Tên Món Ăn").value.toString()
                        nguoidang = "" + data.child("Người đăng").value.toString()
                        ngaydang = "" + data.child("Ngày đăng").value.toString()
                        anhbia = "" + data.child("Ảnh bìa").value.toString()
                        gioithieu = "" + data.child("Giới thiệu món ăn").value.toString()
                        itemId = "" + data.child("ItemId").value.toString()
                        userId = "" + data.child("UserId").value.toString()

                        congThuc =
                            CongThuc(anhbia, ten, gioithieu, ngaydang, nguoidang, itemId, userId)
                        listdata.add(0, congThuc)
                    }
                    recipeAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.v("cancel", error.toString())
                }

            })
    }

    private fun getRate() {
        var userId: String
        var itemId: String
        var rate: String
        databaseReference?.child("Rate")?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot1: DataSnapshot) {
                for (data in snapshot1.children) {
                    userId = "" + data.child("userId").value.toString()
                    itemId = "" + data.child("itemId").value.toString()
                    rate = "" + data.child("rate").value.toString()

                    listRate.add(Rate(userId, itemId, rate))
                    Log.v("list rate home ", listRate.toString())
                }
                recommendAdapter.notifyDataSetChanged()
                recipeAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.v("cancel", error.toString())
            }
        })
    }

    override fun OnItemClick(position: Int) {
        val item: CongThuc = listdata[position]
        val intent = Intent(context, RecipeActivity::class.java)
        intent.putExtra("mon an", item)
        intent.putExtra("new", false)
        startActivity(intent)
    }

    override fun OnItemClickNew(position: Int) {
        val itemnew: CongThuc = listdatanew[position]
        val intent = Intent(context, RecipeActivity::class.java)
        intent.putExtra("mon an new", itemnew)
        intent.putExtra("new", true)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun weatherDes(code : Int, text : TextView){
        when (code){
            200 -> {
                val a = "Có giông kèm mưa nhẹ"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            201 -> {
                val a = "Có giông kèm mưa"
                text.text = a
                pref.edit().putString("des", a).apply()

            }
            202 -> {
                val a = "Có giông kèm mưa lớn"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            230 -> {
                val a ="Bão có mưa phùn nhẹ"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            231 -> {
                val a = "Bão có mưa phùn"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            232 -> {
                val a = "Bão có mưa phùn lớn"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            233 -> {
                val a = "Có sấm sét kèm theo mưa đá"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            300 -> {
                val a = "Mưa phùn nhẹ"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            301 -> {
                val a = "Mưa phùn"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            302 -> {
                val a = "Mưa phùn nặng hạt"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            500 -> {
                val a ="Mưa nhỏ"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            501-> {
                val a = "Mưa vừa phải"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            502 -> {
                val a = "Mưa nặng hạt"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            511 -> {
                val a = "Mưa đóng băng"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            520 -> {
                val a ="Mưa rào nhẹ"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            521 -> {
                val a = "Mưa rào nhẹ"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            522 -> {
                val a = "Mưa rào"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            600 -> {
                val a = "Tuyết nhẹ"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            601 -> {
                val a = "Tuyết"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            602 -> {
                val a = "Tuyết rơi nhiều"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            610 -> {
                val a = "Kết hợp tuyết / mưa"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            611 -> {
                val a = "Mưa tuyết"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            612 -> {
                val a = "Mưa tuyết dày đặc"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            621 -> {
                val a = "Mưa tuyết"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            622 -> {
                val a = "Mưa tuyết dày đặc"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            623 -> {
                val a = "Gió lớn"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            700 -> {
                val a = "Sương mù"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            711 -> {
                val a = "Khói"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            721 -> {
                val a = "Sương mù"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            731 -> {
                val a = "Cát / bụi"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            741 -> {
                val a = "Sương mù"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            751 -> {
                val a = "Sương mù đóng băng"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            800 -> {
                val a =  "Bầu trời quang đãng"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            801 -> {
                val a = "Vài đám mây"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            802 -> {
                val a = "Mây rải rác"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            803 -> {

                val a = "Mây tan vỡ"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            804 -> {
                val a = "Mây u ám"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
            900 -> {
                val a = "Lượng mưa không xác định"
                text.text = a
                pref.edit().putString("des", a).apply()
            }
        }
    }

}