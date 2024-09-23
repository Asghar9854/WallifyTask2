package com.example.wallifytask2.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallifytask2.domain.model.ModelImages
import com.example.wallifytask2.data.repository.StorageRepository
import kotlinx.coroutines.launch

class StorageViewModel : ViewModel() {
    private val repository: StorageRepository = StorageRepository()

    private val mutableSavedImagesList = MutableLiveData<ArrayList<ModelImages>>()
    val saveImages: LiveData<ArrayList<ModelImages>> get() = mutableSavedImagesList

    fun getSaveImages(context: Context) {
        viewModelScope.launch {
            mutableSavedImagesList.value = repository.getSavedImages(context)
        }
    }

    private val mutableAllImagesList = MutableLiveData<ArrayList<ModelImages>>()
    val allImagesList: LiveData<ArrayList<ModelImages>> get() = mutableAllImagesList

    fun getAllStorageImages(context: Context) {
        viewModelScope.launch {
            mutableAllImagesList.value = repository.getAllStorageImages(context)
        }
    }

}