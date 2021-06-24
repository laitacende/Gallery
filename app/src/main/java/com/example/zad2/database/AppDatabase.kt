package com.example.zad2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.zad2.GalleryCell

@Database(entities = arrayOf(GalleryCell::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun galleryCellDao(): GalleryCellDao
    companion object {
        private var instance: AppDatabase? = null
        // create singleton of class
        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(context,
                    AppDatabase::class.java,
                    "cell").fallbackToDestructiveMigration().build()
            }
            return instance
        }

        fun deleteInstanceOfDatabase() {
            instance = null
        }
    }
}