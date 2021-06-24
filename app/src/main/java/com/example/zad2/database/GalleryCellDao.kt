package com.example.zad2.database

import androidx.room.*
import com.example.zad2.GalleryCell

@Dao
interface GalleryCellDao {
    @Query("SELECT * FROM cell")
    fun getAll(): List<GalleryCell>

    @Query("SELECT * FROM cell WHERE pathImg = :id")
    fun getPath(id: String) : List<GalleryCell>

    @Query("UPDATE cell SET rating = :rate WHERE pathImg = :id")
    fun setRatingById(rate: Int, id: String)

    @Query("SELECT rating FROM cell WHERE pathImg = :id")
    fun getRatingById(id: String) : Int

    @Query("UPDATE cell SET des = :des WHERE pathImg = :id")
    fun setDesById(des: String, id: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAll(cells: List<GalleryCell>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun add(cell: GalleryCell) : Long

    @Delete
    fun delete(cell: GalleryCell)

    @Update
    fun update(cell: GalleryCell)

    @Query("DELETE FROM cell")
    fun deleteAll()


    @Query("DELETE FROM cell WHERE pathImg NOT IN (:ids)")
    fun deleteNotPresent(ids: List<String>)

}