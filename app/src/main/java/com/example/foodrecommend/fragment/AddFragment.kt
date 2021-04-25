package com.example.foodrecommend.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.example.foodrecommend.R
import com.example.foodrecommend.data.CachLam
import com.example.foodrecommend.data.NguyenLieu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso


class AddFragment : Fragment() {

    private lateinit var mAuth : FirebaseAuth
    var databaseReference : DatabaseReference?= null
    var database : FirebaseDatabase?= null

    lateinit var img1 :ImageView
    lateinit var img2 :ImageView
    lateinit var imgage :ImageView
    var a = 1

    val cachlamList :ArrayList<CachLam> = ArrayList()
    val nguyenlieuList : ArrayList<NguyenLieu> = ArrayList()
    var themnguyenlieu :Boolean = false
    var themcachlam :Boolean = false
    private lateinit var tenmonan :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")

        val scrollview = view.findViewById<ScrollView>(R.id.scroll)
        scrollview.isVerticalScrollBarEnabled = false

        val parentLayout = view.findViewById<LinearLayout>(R.id.linearlayout)
        val parentLayout2 = view.findViewById<LinearLayout>(R.id.linearlayout2)
        val edt_tenmonan = view.findViewById<EditText>(R.id.edt_tenmonan)
        val edt_nguyenlieu =  view.findViewById<EditText>(R.id.edt_nguyenlieu)
        val edt_soluong =  view.findViewById<EditText>(R.id.edt_soluong)
        val edt_cachlam =  view.findViewById<EditText>(R.id.edt_cachlam)
        val btnanh = view.findViewById<Button>(R.id.btn_themanh)
        val btnAddNguyenlieu = view.findViewById<Button>(R.id.btn_add_nguyenlieu)
        val btnadd = view.findViewById<Button>(R.id.btnadd)
        val btnhoantat = view.findViewById<Button>(R.id.btn_hoantat)
        img1 = view.findViewById(R.id.img_add_cachlam)
        imgage = view.findViewById(R.id.imgv_monan)

        btnanh.setOnClickListener {
            openImageGallery(1000)
            btnanh.isVisible = false
        }

        imgage.setOnClickListener {
            btnanh.isVisible = true
        }

        btnAddNguyenlieu.setOnClickListener {
            add1(parentLayout)
            themnguyenlieu = true
        }

        btnadd.setOnClickListener {
            add2(parentLayout2)
            themcachlam = true
        }

        img1.setOnClickListener {
            openImageGallery(0)
        }

        btnhoantat.setOnClickListener {
            nguyenlieuList.clear()
            cachlamList.clear()
            if (edt_tenmonan.text.toString().equals("")){
                edt_tenmonan.error = "Thêm Tên món ăn"
            }else{
                tenmonan = edt_tenmonan.text.toString()
            }
            if (edt_nguyenlieu.text.toString().equals("")){
                Toast.makeText(context,"Thêm Nguyên Liệu",Toast.LENGTH_LONG).show()
                edt_nguyenlieu.error = "Thêm Nguyên Liệu"
            }else if (edt_soluong.text.toString().equals("")){
                Toast.makeText(context,"Thêm Số Lượng của Nguyên Liệu",Toast.LENGTH_LONG).show()
                edt_soluong.error = "Thêm Số Lượng của Nguyên Liệu"
            }else{
                val nguyenLieualway = NguyenLieu(edt_nguyenlieu.text.toString(),edt_soluong.text.toString())
                nguyenlieuList.add(0,nguyenLieualway)
            }

            if (edt_cachlam.text.toString().equals("")){
                Toast.makeText(context,"Thêm Cách Làm",Toast.LENGTH_LONG).show()
                edt_cachlam.error = "Thêm Cách Làm"
            }else{
                val cachLamalway = CachLam("1",edt_cachlam.text.toString(),null)
                cachlamList.add(0,cachLamalway)
            }
            if(themnguyenlieu){
                themNguyenLieu(parentLayout)
            }
            if (themcachlam){
                themCachLam(parentLayout2)
            }
            Log.v("tenmonan", tenmonan)
            Log.v("nguyenlieu",nguyenlieuList.toString())
            Log.v("cachlam",cachlamList.toString())

            

        }

        return view
    }

    private fun add1(parentLayout: LinearLayout) {
        val circleView= activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowview : View= circleView.inflate(R.layout.item_add_nguyenlieu, null, false)
        parentLayout.addView(rowview, parentLayout.childCount - 1)
        val btnxoa = rowview.findViewById<Button>(R.id.xoa_nguyenlieu1)
        btnxoa.setOnClickListener {
            remove(rowview, parentLayout)
        }
    }

    private fun add2(parentLayout2: LinearLayout){
        val circleView2= activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowview2 : View= circleView2.inflate(R.layout.item_add_cachlam, null, false)
        img2 = rowview2.findViewById(R.id.img_add)
        val btnxoa = rowview2.findViewById<Button>(R.id.xoa_cachlam)
        val txt = rowview2.findViewById<TextView>(R.id.txtSTT)
        a++
        txt.text = a.toString()
        Log.v("a", a.toString())
        parentLayout2.addView(rowview2, parentLayout2.childCount - 1)
        img2.setOnClickListener {
            openImageGallery(20)
        }
        btnxoa.setOnClickListener {
            remove(rowview2, parentLayout2)
            a--
        }
    }

    private fun remove(view: View, parentLayout: LinearLayout){
        parentLayout.removeView(view)
    }

    private fun themNguyenLieu(parentLayout: LinearLayout){
        for (i in 2 until parentLayout.childCount-1){
            val rowview = parentLayout.getChildAt(i)
            val edtextNguyenLieu = rowview.findViewById<EditText>(R.id.edtext_nguyenlieu)
            val edittextSoLuong = rowview.findViewById<EditText>(R.id.edtext_soluong)

            if (edtextNguyenLieu.text.toString().equals("")){
                edtextNguyenLieu.error = "Thêm Nguyên Liệu"
                break
            }else if (edittextSoLuong.text.toString().equals("")){
                edittextSoLuong.error = "Thêm Số Lượng của Nguyên Liệu"
                break
            }else{
                val nguyenLieu = NguyenLieu(edtextNguyenLieu.text.toString(),edittextSoLuong.text.toString())
                nguyenlieuList.add(i-1 ,nguyenLieu)
            }
        }
    }

    private fun themCachLam(parentLayout2: LinearLayout){
        for (i in 2 until parentLayout2.childCount-1){
            val rowview2 = parentLayout2.getChildAt(i)
            val edtextCachLam = rowview2.findViewById<EditText>(R.id.edtext_cachlam)
            val txtSL = rowview2.findViewById<TextView>(R.id.txtSTT)

            if (edtextCachLam.text.toString().equals("")){
                edtextCachLam.error = "Thêm Cách Làm bước "+ (i-1)
                break
            }else{
                val cachlam = CachLam(txtSL.text.toString(),edtextCachLam.text.toString(),null)
                cachlamList.add(i-1 ,cachlam)
            }
        }
    }

    private fun openImageGallery(requestCode: Int){
        val withListener = Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                        if (p0!!.areAllPermissionsGranted()) {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            startActivityForResult(intent, requestCode)
                        } else
                            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT)
                                    .show()
                    }

                    override fun onPermissionRationaleShouldBeShown(p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?, p1: PermissionToken?) {
                        p1!!.continuePermissionRequest()
                    }

                }).check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val dis = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dis)

        val uri = data?.data
        if(resultCode == RESULT_OK && requestCode == 0 ) {
            Picasso.get().load(uri).resize(100, 100).into(img1)
        }else if (resultCode == RESULT_OK && requestCode == 1000 ){
            Picasso.get().load(uri).resize(dis.widthPixels,600).into(imgage)
        }else if (resultCode == RESULT_OK && requestCode == 20 ){
            Picasso.get().load(uri).resize(100, 100).into(img2)
        }
    }

}