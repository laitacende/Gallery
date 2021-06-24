package com.example.zad2.activities

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.zad2.R
import com.example.zad2.StateAdapter
import com.example.zad2.fragments.RateFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initViewPager()
    }

    private fun initViewPager() {
        var viewPager = findViewById<ViewPager2>(R.id.pager)
        var adapter = StateAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        var tab = findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tab, viewPager) { tab, position ->

        }.attach()
    }
}