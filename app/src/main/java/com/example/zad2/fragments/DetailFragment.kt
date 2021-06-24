package com.example.zad2.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.zad2.R
import com.example.zad2.activities.RateActivity
import com.example.zad2.database.AppDatabase
import com.example.zad2.database.GalleryCellDao
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DetailFragment : Fragment() {

    private var galleryCellDao : GalleryCellDao? = null
    private var path : String? = " "

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity?.intent != null) {
            path = activity!!.intent.getStringExtra("path")
            val uri = Uri.parse(activity!!.intent.getStringExtra("uri"))
            Glide.with(context!!)
                .load(uri)
                .thumbnail(0.8f)
                .into(view.findViewById(R.id.imageDetail))
            val intent = Intent()
            intent.putExtra("name", path)
            requireActivity().setResult(RESULT_OK, intent)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBase = AppDatabase.getInstance(context!!)
        galleryCellDao = dataBase?.galleryCellDao()

        return inflater.inflate(R.layout.detail_fragment, container, false)
    }
}