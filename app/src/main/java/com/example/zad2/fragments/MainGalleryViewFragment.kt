package com.example.zad2.fragments

import android.Manifest.permission.*
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zad2.GalleryCell
import com.example.zad2.activities.CameraActivity
import com.example.zad2.activities.DetailActivity
import com.example.zad2.database.AppDatabase
import com.example.zad2.database.GalleryCellDao
import com.example.zad2.databinding.MainFragmentBinding
import com.example.zad2.recycleritem.GalleryItem
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.*


class MainGalleryViewFragment : Fragment() {
    private lateinit var binding: MainFragmentBinding

    private var cells : MutableList<GalleryCell> = ArrayList()
    private var adapter : GalleryItem? = null
    private var galleryCellDao : GalleryCellDao? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = MainFragmentBinding.inflate(layoutInflater)
        val view = binding.root
        val dataBase = AppDatabase.getInstance(context!!)
        galleryCellDao = dataBase?.galleryCellDao()
        updateDataBase()
        adapter = GalleryItem(cells, context, galleryCellDao!!, this)
        binding.images.adapter = adapter
        if (context!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.images.layoutManager = GridLayoutManager(context, 3)
        } else {
            binding.images.layoutManager = GridLayoutManager(context, 6)
        }

        adapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.floating.setOnClickListener {
            // take photo
            var myintent = Intent(context, CameraActivity::class.java)
            startActivityForResult(myintent, 200)
        }
        binding.images.addOnScrollListener( object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.floating.hide()
                } else if (dy <= 0) {
                    binding.floating.show()
                }
            }
        })
        return view
    }

    // before createview
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
   }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val name = data?.getStringExtra("name")
            updateRating(name!!)
        } else if (requestCode == 200) {
            updateDataBase()
        }
    }

    private fun updateRating(path: String) {
        val pos = findPos(path)
        if (pos != -1) {
            doAsync {
                var new = galleryCellDao?.getRatingById(path)
                cells[pos].rating = new!!
                cells.sortByDescending { it.rating }
                uiThread {
                    adapter?.notifyDataSetChanged()
                }

            }
        }
    }

    private fun findPos(path: String)  : Int {
        for (i in cells.indices) {
            if(cells[i].pathImg == path) {
                return i
            }
        }
        return -1
    }

    private fun checkPermissions() {
        val read = ContextCompat.checkSelfPermission(context!!, READ_EXTERNAL_STORAGE)
        val write = ContextCompat.checkSelfPermission(context!!, WRITE_EXTERNAL_STORAGE)
        if (read != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), 100)
        }
        if (write != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE), 200)
        }
    }

    private fun updateDataBase() { // if user added new images to folder, add them; if user deleted some, delete them
        val directory = "MyGalleryImages"
        var all = ArrayList<String>()
        var list = ArrayList<GalleryCell>()
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )


        val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
        else "${MediaStore.Images.Media.DATA } LIKE ? "

        val selectionArgs = arrayOf("%$directory%")
        context?.contentResolver?.query(
            collection,
            projection,
            null,
            null,
            null)?.use { cursor ->
            while (cursor.moveToNext()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val newCell = GalleryCell(name, " ", 0, contentUri.toString())
                all.add(newCell.pathImg)
                //Log.println(Log.DEBUG, "kk", name)
                list.add(newCell)
                doAsync {
                    galleryCellDao?.add(newCell)
                }
            }
        }

        doAsync {
            galleryCellDao?.deleteNotPresent(all)
            cells.clear()
            cells.addAll(galleryCellDao?.getAll() as Collection<GalleryCell>)
            cells.sortByDescending { it.rating }
            uiThread {
                adapter?.notifyDataSetChanged()
            }
        }
    }

    fun saveImage(name: String, name1 : String) {
        // save image  // for below Q environemt.get blah blah
        val relativeLocation = Environment.DIRECTORY_PICTURES + "/" + "MyGalleryImages"
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "description")
        val contentResolver = context!!.contentResolver
        val out = context!!.assets.open(name1)
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        var stream: OutputStream? = null
        var uri: Uri? = null

        try {
            uri = contentResolver.insert(contentUri, contentValues)
            if (uri == null)
            {
                throw IOException("Failed to create new MediaStore record.")
            }

            stream = contentResolver.openOutputStream(uri)
            val bitmap = BitmapFactory.decodeStream(out)
            if (stream == null)
            {
                throw IOException("Failed to get output stream.")
            }

            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream))
            {
                throw IOException("Failed to save bitmap.")
            }


        } catch(e: IOException) {
            if (uri != null)
            {
                contentResolver.delete(uri, null, null)
            }

            throw IOException(e)

        }
        finally {
            stream?.close()
        }
    }
}