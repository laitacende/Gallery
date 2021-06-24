package com.example.zad2.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.zad2.fragments.MainGalleryViewFragment
import com.example.zad2.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            var frag = supportFragmentManager.findFragmentById(R.id.mainfragment) as MainGalleryViewFragment
            frag.onActivityResult(requestCode, resultCode, data)
        }
    }
}