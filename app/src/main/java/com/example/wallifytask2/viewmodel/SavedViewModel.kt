package com.example.wallifytask2.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.wallifytask2.repository.SavedRepository

class SavedViewModel : ViewModel() {
    private var repository: SavedRepository = SavedRepository()
    fun getSavedPhotos(context: Context) = repository.getSavedPhotos(context)
}