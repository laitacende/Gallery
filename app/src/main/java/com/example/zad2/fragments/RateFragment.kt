package com.example.zad2.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.zad2.DialogEdit
import com.example.zad2.GalleryCell
import com.example.zad2.R
import com.example.zad2.database.AppDatabase
import com.example.zad2.database.GalleryCellDao
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class RateFragment : Fragment() {

    var galleryCellDao : GalleryCellDao? = null
    private var key : String? = " "
    private var cell: GalleryCell? = null
    private var save : Button? = null
    private var edit : Button? = null
    private var descri: TextView? = null
    private var rating: RatingBar? = null
    var rate: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        save = view.findViewById<Button>(R.id.save)
        edit = view.findViewById<Button>(R.id.edit)
        descri = view.findViewById<TextView>(R.id.desc)
        rating = view.findViewById<RatingBar>(R.id.rate)
        if (activity?.intent != null) {
            key = activity!!.intent.getStringExtra("path")
            if (key != null) {
                setUp(key!!)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBase = AppDatabase.getInstance(context!!)
        galleryCellDao = dataBase?.galleryCellDao()
        return inflater.inflate(R.layout.rate_fragment, container, false)
    }

    fun setUp(key: String) {
        doAsync {
            cell =  galleryCellDao?.getPath(key)!![0]
            uiThread {
                if (rate == null) {
                    rating?.rating = cell!!.rating.toFloat()
                } else {
                    rating?.rating = rate!!.toFloat()
                }
                descri?.text = cell!!.des
            }
        }
        save?.setOnClickListener {
            // save changed rating to data base
            doAsync {
                galleryCellDao?.setRatingById(rating!!.rating.toInt(), key)
                uiThread {
                    Toast.makeText(activity, "Rating saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
        edit?.setOnClickListener {
           DialogEdit(descri!!, key, galleryCellDao).show(childFragmentManager, DialogEdit.TAG)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("rating", rating!!.rating.toInt())
        rate = null
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            rate = savedInstanceState.getInt("rating")
        }
    }
}