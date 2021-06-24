package com.example.zad2

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cell")
data class GalleryCell (
    @PrimaryKey var pathImg: String,
    @ColumnInfo(name = "des") var des: String,
    @ColumnInfo(name = "rating") var rating: Int,
    @ColumnInfo(name = "uri") var uri: String) {
}