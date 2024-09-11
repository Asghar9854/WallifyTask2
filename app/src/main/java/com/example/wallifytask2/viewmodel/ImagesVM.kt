package com.example.wallifytask2.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallifytask2.model.ModelImages
import com.example.wallifytask2.repository.ImagesRepository
import kotlinx.coroutines.launch

class ImagesVM : ViewModel() {

    private val repository: ImagesRepository = ImagesRepository()

    private val mutableCropImagesFiles = MutableLiveData<ArrayList<ModelImages>>()
    val cropDirectory: LiveData<ArrayList<ModelImages>> get() = mutableCropImagesFiles

    fun getSaveImages(context: Context) {
        viewModelScope.launch {
            mutableCropImagesFiles.value = repository.getSavedImages(context)
        }
    }

}