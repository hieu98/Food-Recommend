package com.example.foodrecommend.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.foodrecommend.R
import com.example.foodrecommend.activity.MainActivity
import com.example.foodrecommend.data.CachLam
import com.example.foodrecommend.data.NguyenLieu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class AddFragment : Fragment() {

    private lateinit var mAuth : FirebaseAuth
    private var databaseReference : DatabaseReference?= null
    private var database : FirebaseDatabase?= null
    private var storage : FirebaseStorage ?= null
    private var storageReference : StorageReference ?= null

    private lateinit var img1 :ImageView
    private lateinit var img2 :ImageView
    private lateinit var imgage :ImageView
    private var a = 1
    private var uri : Uri ?= null
    private var anhbia : Uri ?= null

    private val imageList :ArrayList<Uri>  = ArrayList()
    private val cachlamList :ArrayList<CachLam> = ArrayList()
    private val nguyenlieuList : ArrayList<NguyenLieu> = ArrayList()
    private var themnguyenlieu :Boolean = false
    private var themcachlam :Boolean = false
    private var tenmonan :String = ""
    private var gioithieumonan :String = ""
    private var nguoidangmonan :String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_new, container, false)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        databaseReference = database?.reference

        val scrollview = view.findViewById<ScrollView>(R.id.scroll)
        scrollview.isVerticalScrollBarEnabled = false

        val parentLayout = view.findViewById<LinearLayout>(R.id.linearlayout)
        val parentLayout2 = view.findViewById<LinearLayout>(R.id.linearlayout2)
        val edt_tenmonan = view.findViewById<EditText>(R.id.edt_tenmonan)
        val edt_gioithieumonan = view.findViewById<EditText>(R.id.edt_gioithieu)
        val edt_nguyenlieu =  view.findViewById<EditText>(R.id.edt_nguyenlieu)
        val edt_soluong =  view.findViewById<EditText>(R.id.edt_soluong)
        val edt_cachlam =  view.findViewById<EditText>(R.id.edt_cachlam)
        val btnanh = view.findViewById<Button>(R.id.btn_themanh)
        val btnAddNguyenlieu = view.findViewById<Button>(R.id.btn_add_nguyenlieu)
        val btnadd = view.findViewById<Button>(R.id.btnadd)
        val btnhoantat = view.findViewById<Button>(R.id.btn_hoantat)
        img1 = view.findViewById(R.id.img_add_cachlam)
        imgage = view.findViewById(R.id.imgv_monan)

        val user = mAuth.currentUser
        val userref = databaseReference?.child("profile")?.child(user?.uid!!)
        userref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nguoidangmonan = snapshot.child("name").value.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

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
            // thêm tên món
            if (edt_tenmonan.text.toString().equals("")){
                edt_tenmonan.error = "Thêm Tên món ăn"
            }else{
                tenmonan = edt_tenmonan.text.toString()
            }
            // thêm giới thiệu món ăn
            if (edt_gioithieumonan.text.toString().equals("")){
                edt_gioithieumonan.error = "Thêm Tên món ăn"
            }else{
                gioithieumonan = edt_gioithieumonan.text.toString()
            }
            // thêm nguyên liệu default
            if (edt_nguyenlieu.text.toString().equals("")){
                edt_nguyenlieu.error = "Thêm Nguyên Liệu"
            }else if (edt_soluong.text.toString().equals("")){
                edt_soluong.error = "Thêm Số Lượng của Nguyên Liệu"
            }else{
                val nguyenLieualway = NguyenLieu(edt_nguyenlieu.text.toString(),edt_soluong.text.toString())
                nguyenlieuList.add(0,nguyenLieualway)
            }
            // thêm nguyên liệu new
            if(themnguyenlieu){
                themNguyenLieu(parentLayout)
            }
            // thêm cách làm default
            if (edt_cachlam.text.toString().equals("")){
                edt_cachlam.error = "Thêm Cách Làm"
            }else{
                val cachLamalway = CachLam("1",edt_cachlam.text.toString(),null)
                cachlamList.add(0,cachLamalway)
            }
            // thêm cách làm new
            if (themcachlam){
                themCachLam(parentLayout2)
            }
            if (cachlamList.size > 2){
                cachlamList.removeAt(1)
            }
            uploadCongThuc(cachlamList,nguyenlieuList,tenmonan,gioithieumonan,nguoidangmonan)

            val intent = Intent(context,MainActivity::class.java)
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

        }

        return view
    }

    private fun add1(parentLayout: LinearLayout) {
        val circleView= requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowview : View= circleView.inflate(R.layout.item_add_nguyenlieu, null, false)
        parentLayout.addView(rowview, parentLayout.childCount - 1)
        val btnxoa = rowview.findViewById<Button>(R.id.xoa_nguyenlieu1)
        btnxoa.setOnClickListener {
            remove(rowview, parentLayout)
        }
    }

    private fun add2(parentLayout2: LinearLayout){
        val circleView2= requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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
            val edtextNguyenLieu = rowview?.findViewById<EditText>(R.id.edtext_nguyenlieu)
            val edittextSoLuong = rowview?.findViewById<EditText>(R.id.edtext_soluong)

            if (edtextNguyenLieu?.text.toString().equals("")){
                edtextNguyenLieu?.error = "Thêm Nguyên Liệu"
                break
            }else if (edittextSoLuong?.text.toString().equals("")){
                edittextSoLuong?.error = "Thêm Số Lượng của Nguyên Liệu"
                break
            }else{
                val nguyenLieu = NguyenLieu(edtextNguyenLieu?.text.toString(),edittextSoLuong?.text.toString())
                nguyenlieuList.add(i-1 ,nguyenLieu)
            }
        }
    }

    private fun themCachLam(parentLayout2: LinearLayout){
        for (i in 2 until parentLayout2.childCount-1){
            val rowview2 = parentLayout2.getChildAt(i)
            val edtextCachLam = rowview2?.findViewById<EditText>(R.id.edtext_cachlam)
            val txtSL = rowview2?.findViewById<TextView>(R.id.txtSTT)

            if (edtextCachLam?.text.toString().equals("")){
                edtextCachLam?.error = "Thêm Cách Làm bước "+ (i-1)
                break
            }else{
                val cachlam = CachLam(txtSL?.text.toString(),edtextCachLam?.text.toString(),null)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadCongThuc(cachlamList : ArrayList<CachLam>, nguyenlieuList : ArrayList<NguyenLieu>, ten :String?, gioithieu:String?,nguoidang :String?){
        val userId = mAuth.currentUser?.uid
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val formatted = current.format(formatter)
        val cur = databaseReference?.child("Công Thức")?.child(ten!!)
        cur?.child("Giới thiệu món ăn")?.setValue(gioithieu!!)
        cur?.child("Ngày đăng")?.setValue(formatted)
        cur?.child("Người đăng")?.setValue(nguoidang)
        cur?.child("Tên Món Ăn")?.setValue(ten)

        databaseReference?.child("profile")?.child(userId!!)?.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                    val iduser = snapshot.child("useridReal")
                    cur?.child("UserId")?.setValue(iduser)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.v("error","Lỗi")
            }

        })

        val fileRef = storageReference?.child("Ảnh Bìa/")
        fileRef?.listAll()?.addOnSuccessListener { listResult ->
            for (item in listResult.items) {
                val countofimages = listResult.items.size
                cur?.child("ItemId")?.setValue(countofimages + 1)
            }
        }

        var a :String
        if (uri != null) {
            for (i in 0 until imageList.size) {
                val ref = storageReference?.child("Ảnh các bước")?.child(ten!!)?.child("Bước " + (i+1).toString())
                ref?.putFile(imageList[i])
                        ?.addOnFailureListener {

                        }
                        ?.addOnSuccessListener {
//                            saveUrlToUser(po.storage.downloadUrl.toString())
                            ref.downloadUrl.addOnSuccessListener {
                                a = it.toString()
                                cachlamList[i].imageBuoc = a
                                cur?.child("Cách Làm")?.child(i.toString())?.setValue(cachlamList[i])
                                Log.v("listcachlam",cachlamList[i].toString())
                            }
                        }
                        ?.addOnProgressListener {

                        }
            }
            val reff = storageReference?.child("Ảnh bìa")?.child(ten!!)
            if (anhbia != null){
                reff?.putFile(anhbia!!)
                        ?.addOnFailureListener {

                        }
                        ?.addOnSuccessListener {
                            reff.downloadUrl.addOnSuccessListener {
                                a = it.toString()
                                cur?.child("Ảnh bìa")?.setValue(a)
                            }
                        }
                        ?.addOnProgressListener {
                        }
            }else {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Image")
                builder.setMessage("Thêm ảnh đại diện cho món ăn")
                builder.setNegativeButton("Ok",{ dialogInterface: DialogInterface, i:Int ->   })
                builder.show()
            }
        }
        for (i in 0 until nguyenlieuList.size){
            cur?.child("Nguyên Liệu")?.child(i.toString())?.setValue(nguyenlieuList[i])
        }
    }


//    private fun saveUrlToUser(uri :String){
//        val userId = mAuth.currentUser!!.uid
//        val cur = databaseReference?.child(userId)?.child("image")
//        val calendar = Calendar.getInstance()
//        val name = calendar.timeInMillis.toString()
//        cur?.child(name)?.setValue(Image(name,uri))
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val dis = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dis)

        uri = data?.data
        if(resultCode == RESULT_OK && requestCode == 0 ) {
            Picasso.get().load(uri).resize(150, 150).into(img1)
            imageList.add(uri!!)
        }else if (resultCode == RESULT_OK && requestCode == 1000 ){
            Picasso.get().load(uri).resize(dis.widthPixels,600).into(imgage)
            anhbia = uri
        }else if (resultCode == RESULT_OK && requestCode == 20 ){
            Picasso.get().load(uri).resize(150, 150).into(img2)
            imageList.add(uri!!)
        }
    }

}