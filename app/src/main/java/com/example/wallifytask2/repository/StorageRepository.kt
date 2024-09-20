package com.example.wallifytask2.repository

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.wallifytask2.model.ModelImages
import com.example.wallifytask2.utils.SAVEDDIRECTORY

class StorageRepository {

    fun getAllStorageImages(context: Context): ArrayList<ModelImages> {
        val allImagesList: ArrayList<ModelImages> = ArrayList()
        val imageProjection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media._ID
        )
        val imageSortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            null,
            null,
            imageSortOrder
        )
        cursor.use {
            it?.let {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val name = it.getString(nameColumn)
                    val size = it.getString(sizeColumn)
                    val date = it.getString(dateColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    if (name != null && size != null && date != null) {
                        allImagesList.add(ModelImages(id, name, size, date, contentUri.toString(),false))
                    }

                }
            } ?: kotlin.run {
                Log.e("TAG", "Cursor is null!")
            }
        }
        return allImagesList
    }


    fun getSavedImages(context: Context): ArrayList<ModelImages> {
        val saveImagesList: ArrayList<ModelImages> = ArrayList()

        val imageSortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        val imageProjection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media._ID
        )
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.getContentUri("external")
        }


        val cursor = context.contentResolver.query(
            collection,
            imageProjection,
            MediaStore.Images.Media.DATA + " like ? ",
            arrayOf("%$SAVEDDIRECTORY%"),
            imageSortOrder
        )
        cursor.use {
            it?.let {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val name = it.getString(nameColumn)
                    val size = it.getString(sizeColumn)
                    val date = it.getString(dateColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    if (name != null && size != null) {
                        saveImagesList.add(
                            ModelImages(
                                id, name, size, date,
                                contentUri.toString(), false
                            )
                        )
                    }

                }
            } ?: kotlin.run {
                Log.e("TAG", "Cursor is null!")
            }
        }
        return saveImagesList
    }

}