package com.example.foodrecommend.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodrecommend.R
import com.example.foodrecommend.adapter.FixCachLamAdapter
import com.example.foodrecommend.adapter.FixNguyenLieuAdapter
import com.example.foodrecommend.data.CachLam
import com.example.foodrecommend.data.CongThuc
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
import kotlinx.android.synthetic.main.activity_fix_recipe.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class FixRecipeActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private var themnguyenlieu: Boolean = false
    private var themcachlam: Boolean = false
    private lateinit var imgage: ImageView
    private lateinit var img2: ImageView
    private var a = 1
    private val imageList: ArrayList<Uri> = ArrayList()
    private var cachlamList: ArrayList<CachLam> = ArrayList()
    private var nguyenlieuList: ArrayList<NguyenLieu> = ArrayList()
    private var tenmonan: String = ""
    private var gioithieumonan: String = ""
    private var nguoidangmonan: String = ""
    private lateinit var nguyenLieuAdapter: FixNguyenLieuAdapter
    private lateinit var cachLamAdapter: FixCachLamAdapter

    private var uri: Uri? = null
    private var anhbia: Uri? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fix_recipe)

        val dis = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(dis)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        databaseReference = database?.reference

        val scrollview = findViewById<ScrollView>(R.id.fix_scroll)
        scrollview.isVerticalScrollBarEnabled = false

        val congThucOld = intent.getSerializableExtra("congthucold") as CongThuc

        Picasso.get().load(congThucOld.image).resize(dis.widthPixels, 600).into(imgage)
        edt_fix_gioithieu.setText(congThucOld.gioithieu)
        edt_fix_tenmonan.setText(congThucOld.ten)

        btn_suaanh.setOnClickListener {
            openImageGallery(1000)
            btn_suaanh.isVisible = false
        }
        getListCachLam(congThucOld)
        getListNguyenLieu(congThucOld)

        cachLamAdapter = FixCachLamAdapter(cachlamList, this)
        nguyenLieuAdapter = FixNguyenLieuAdapter(nguyenlieuList, this)

        nguyenLieuAdapter.callback = {
            nguyenlieuList.clear()
            nguyenlieuList = it as ArrayList<NguyenLieu>
            nguyenLieuAdapter.notifyDataSetChanged()
        }

        cachLamAdapter.callback = {
            cachlamList.clear()
            cachlamList = it
            cachLamAdapter.notifyDataSetChanged()
        }
        nguoidangmonan = congThucOld.nguoidang

        rv_fix_nguyenlieu.setHasFixedSize(true)
        rv_fix_cachlam.setHasFixedSize(true)
        rv_fix_cachlam.adapter = cachLamAdapter
        rv_fix_nguyenlieu.adapter = nguyenLieuAdapter
        rv_fix_cachlam.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_fix_nguyenlieu.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        imgage.setOnClickListener {
            btn_suaanh.isVisible = true
        }

        btn_fix_add_nguyenlieu.setOnClickListener {
            add1(fix_linearlayout)
            themnguyenlieu = true
        }

        btn_fix_add_cach_lam.setOnClickListener {
            add2(fix_linearlayout2)
            themcachlam = true
        }

        btn_fix_hoantat.setOnClickListener {
            nguyenlieuList.clear()
            cachlamList.clear()

            // thêm tên món
            if (edt_fix_tenmonan.text.toString() == "") {
                edt_fix_tenmonan.error = "Thêm Tên món ăn"
            } else {
                tenmonan = edt_fix_tenmonan.text.toString()
            }

            // thêm giới thiệu món ăn
            if (edt_fix_gioithieu.text.toString() == "") {
                edt_fix_gioithieu.error = "Thêm Tên món ăn"
            } else {
                gioithieumonan = edt_fix_gioithieu.text.toString()
            }

            // thêm nguyên liệu new
            if (themnguyenlieu) {
                themNguyenLieu(fix_linearlayout)
            }

            // thêm cách làm new
            if (themcachlam) {
                themCachLam(fix_linearlayout2)
            }

            if (cachlamList.size > 2) {
                cachlamList.removeAt(1)
            }
            uploadCongThuc(cachlamList, nguyenlieuList, tenmonan, gioithieumonan, nguoidangmonan)

            val intent = Intent(this, LoadingActivity::class.java)
            intent.putExtra("add data", true)
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

        }

    }

    private fun getListNguyenLieu(congThuc: CongThuc) {
        databaseReference?.child("Công Thức")?.child(congThuc.ten)?.child("Nguyên Liệu")
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        nguyenlieuList.add(
                            NguyenLieu(
                                data.child("tenNguyenLieu").value.toString(),
                                data.child("soLuong").value.toString()
                            )
                        )
                        nguyenLieuAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun getListCachLam(congThuc: CongThuc) {
        databaseReference?.child("Công Thức")?.child(congThuc.ten)?.child("Cách Làm")
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        cachlamList.add(
                            CachLam(
                                data.child("stt").value.toString(),
                                data.child("buoc").value.toString(),
                                data.child("imageBuoc").value.toString()
                            )
                        )
                        cachLamAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadCongThuc(
        cachlamList: ArrayList<CachLam>,
        nguyenlieuList: ArrayList<NguyenLieu>,
        ten: String?,
        gioithieu: String?,
        nguoidang: String?
    ) {
        val userId = mAuth.currentUser?.uid
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val formatted = current.format(formatter)
        val cal = Calendar.getInstance()
        val timelimit = cal.timeInMillis.toString()
        val cur = databaseReference?.child("Công Thức")?.child(ten!!)
        cur?.child("Giới thiệu món ăn")?.setValue(gioithieu!!)
        cur?.child("Ngày đăng")?.setValue(formatted)
        cur?.child("Người đăng")?.setValue(nguoidang)
        cur?.child("Tên Món Ăn")?.setValue(ten)
        cur?.child("TLM")?.setValue(timelimit)

        databaseReference?.child("profile")?.child(userId!!)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val iduser = snapshot.child("useridReal").value
                    cur?.child("UserId")?.setValue(iduser)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.v("error", "Lỗi")
                }

            })

        val fileRef = storageReference?.child("Ảnh bìa/")
        fileRef?.listAll()?.addOnSuccessListener { listResult ->
            for (item in listResult.items) {
                val countofimages = listResult.items.size
                cur?.child("ItemId")?.setValue(countofimages + 1)
            }
        }

        var a: String
        if (uri != null) {
            for (i in 0 until imageList.size) {
                val ref = storageReference?.child("Ảnh các bước")?.child(ten!!)
                    ?.child("Bước " + (i + 1).toString())
                ref?.putFile(imageList[i])
                    ?.addOnFailureListener {

                    }
                    ?.addOnSuccessListener {
//                            saveUrlToUser(po.storage.downloadUrl.toString())
                        ref.downloadUrl.addOnSuccessListener {
                            a = it.toString()
                            cachlamList[i].imageBuoc = a
                            cur?.child("Cách Làm")?.child(i.toString())?.setValue(cachlamList[i])
                            Log.v("listcachlam", cachlamList[i].toString())
                        }
                    }
                    ?.addOnProgressListener {

                    }
            }
            val reff = storageReference?.child("Ảnh bìa")?.child(ten!!)
            if (anhbia != null) {
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
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Image")
                builder.setMessage("Thêm ảnh đại diện cho món ăn")
                builder.setNegativeButton("Ok") { dialogInterface: DialogInterface, i: Int -> }
                builder.show()
            }
        }
        for (i in 0 until nguyenlieuList.size) {
            cur?.child("Nguyên Liệu")?.child(i.toString())?.setValue(nguyenlieuList[i])
        }
    }

    private fun themCachLam(parentLayout2: LinearLayout) {
        for (i in 2 until parentLayout2.childCount - 1) {
            val rowview2 = parentLayout2.getChildAt(i)
            val edtextCachLam = rowview2?.findViewById<EditText>(R.id.edtext_cachlam)
            val txtSL = rowview2?.findViewById<TextView>(R.id.txtSTT)

            if (edtextCachLam?.text.toString() == "") {
                edtextCachLam?.error = "Thêm Cách Làm bước " + (i - 1)
                break
            } else {
                val cachlam = CachLam(txtSL?.text.toString(), edtextCachLam?.text.toString(), null)
                cachlamList.add(i - 1, cachlam)
            }
        }
    }

    private fun themNguyenLieu(parentLayout: LinearLayout) {
        for (i in 2 until parentLayout.childCount - 1) {
            val rowview = parentLayout.getChildAt(i)
            val edtextNguyenLieu = rowview?.findViewById<EditText>(R.id.edtext_nguyenlieu)
            val edittextSoLuong = rowview?.findViewById<EditText>(R.id.edtext_soluong)

            if (edtextNguyenLieu?.text.toString() == "") {
                edtextNguyenLieu?.error = "Thêm Nguyên Liệu"
                break
            } else if (edittextSoLuong?.text.toString() == "") {
                edittextSoLuong?.error = "Thêm Số Lượng của Nguyên Liệu"
                break
            } else {
                val nguyenLieu =
                    NguyenLieu(edtextNguyenLieu?.text.toString(), edittextSoLuong?.text.toString())
                nguyenlieuList.add(i - 1, nguyenLieu)
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun add1(parentLayout: LinearLayout) {
        val circleView =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowview: View = circleView.inflate(R.layout.item_add_nguyenlieu, null, false)
        parentLayout.addView(rowview, parentLayout.childCount - 1)
        val btnxoa = rowview.findViewById<Button>(R.id.xoa_nguyenlieu1)

        btnxoa.setOnClickListener {
            remove(rowview, parentLayout)
        }
    }

    @SuppressLint("InflateParams")
    private fun add2(parentLayout2: LinearLayout) {
        val circleView2 =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowview2: View = circleView2.inflate(R.layout.item_add_cachlam, null, false)
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

    private fun remove(view: View, parentLayout: LinearLayout) {
        parentLayout.removeView(view)
    }

    private fun openImageGallery(requestCode: Int) {
        val withListener = Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()) {
                        shhowDialog(requestCode)
                    } else
                        Toast.makeText(
                            this@FixRecipeActivity,
                            "Permission denied",
                            Toast.LENGTH_SHORT
                        )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val dis = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(dis)

        uri = data?.data
        if (resultCode == RESULT_OK && requestCode == 1000) {
            Picasso.get().load(uri).resize(dis.widthPixels, 600).into(imgage)
            anhbia = uri
        } else if (resultCode == RESULT_OK && requestCode == 20) {
            Picasso.get().load(uri).resize(150, 150).into(img2)
            imageList.add(uri!!)
        }
    }

    private fun shhowDialog(requestCode: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ảnh")
        builder.setMessage("Bạn muốn chụp ảnh mới hay chọn ảnh trong máy ?")
        builder.setPositiveButton("Chụp ảnh") { dialogInterface: DialogInterface, i: Int ->
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, requestCode)
        }
        builder.setNegativeButton("Chọn ảnh") { dialogInterface: DialogInterface, i: Int ->
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, requestCode)
        }
        builder.show()

    }
}







