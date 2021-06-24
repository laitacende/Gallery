package com.example.zad2.recycleritem

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.zad2.GalleryCell
import com.example.zad2.R
import com.example.zad2.activities.DetailActivity
import com.example.zad2.database.GalleryCellDao
import com.example.zad2.fragments.MainGalleryViewFragment


class GalleryItem(private val data: MutableList<GalleryCell>, private val context: Context?,
                  private val galleryCellDao: GalleryCellDao,
                  mainGalleryViewFragment: MainGalleryViewFragment
) : RecyclerView.Adapter<GalleryItem.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView

        init {
            image = view.findViewById(R.id.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryItem.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
        val viewHolder = ViewHolder(view)
        view.setOnClickListener {
            var myintent = Intent(context, DetailActivity::class.java)
            myintent.putExtra("uri", data[viewHolder.absoluteAdapterPosition].uri)
            myintent.putExtra("path", data[viewHolder.absoluteAdapterPosition].pathImg)
            (context as Activity).startActivityForResult(myintent, 100)
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: GalleryItem.ViewHolder, position: Int) {
        val uri = Uri.parse(data[position].uri)
//        var thumbBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            context?.contentResolver?.loadThumbnail(uri, Size(200, 200), null)
//        } else {
////            MediaStore.Images.Thumbnails.getThumbnail(context?.contentResolver,
////                    uri, MediaStore.Images.Thumbnails.MINI_KIND, null)
//        }
//      holder.image.setImageBitmap(thumbBitmap as Bitmap?)
        Glide.with(context!!)
                .load(uri)
                .override(150, 150)
                .thumbnail(0.5f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.image)

    }
}