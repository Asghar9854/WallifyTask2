package com.example.wallifytask2.dataBase

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "saved_images")
data class savePhoto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imgId: Int,
    val name: String,
    val imagePath: String
) : Serializable
