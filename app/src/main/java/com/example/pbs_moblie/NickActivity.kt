package com.example.pbs_moblie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_nick.*

class NickActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nick)



        var intent = Intent(this,MainActivity::class.java)
        nic_btn.setOnClickListener {
            intent.putExtra("nickname",editText.text.toString())
           startActivity(intent)
        }
    }
}
