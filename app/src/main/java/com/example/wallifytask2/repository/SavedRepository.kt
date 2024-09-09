package com.example.wallifytask2.repository

import android.content.Context
import com.example.wallifytask2.dataBase.AppDatabase

class SavedRepository {
    fun getSavedPhotos(context: Context) =
        AppDatabase.getDataBase(context).photoDao().getAllPhotos()
}