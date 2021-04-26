package com.example.foodrecommend.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodrecommend.R
import kotlinx.android.synthetic.main.activity_cong_thuc.*

class CongThucActivity : AppCompatActivity() {

    var rate : Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cong_thuc)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val a = intent.getStringExtra("ten mon")
        rate = intent.getFloatExtra("rating",0.0f)
        supportActionBar?.title = a
        ratingbar.rating = rate
        ratingbar.stepSize = .5f
        ratingbar.setOnRatingBarChangeListener{ratingbar,rating,fromUser ->
            rate = rating
            Toast.makeText(this,"Rating: $rating",Toast.LENGTH_LONG).show()
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (rate!=0.0f){
                    onBackPressed()
                }else{
                    showDialog()
                }

                return true
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Rate")
        builder.setMessage("Do you want stay and rate this recipe ?")
        builder.setPositiveButton("No") { dialogInterface: DialogInterface, i: Int ->
            onBackPressed()
        }
        builder.setNegativeButton("Yes",{dialogInterface: DialogInterface,i:Int ->   })
        builder.show()
    }
}