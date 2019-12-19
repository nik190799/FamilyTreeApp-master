package com.hexanovate.familytree.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hexanovate.familytree.R
import com.hexanovate.familytree.ui.subadmin.home.MainActivity

class AboutusActivity : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutus)
    }
}
