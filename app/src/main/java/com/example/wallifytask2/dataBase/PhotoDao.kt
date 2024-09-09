package com.example.wallifytask2.dataBase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(savePhoto: savePhoto)

    @Query("SELECT * FROM saved_images")
    fun getAllPhotos(): LiveData<List<savePhoto>>
}