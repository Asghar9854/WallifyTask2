package com.example.wallifytask2.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallifytask2.model.PixelsResponse
import com.example.wallifytask2.repository.MainRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository: MainRepository = MainRepository()

    private val _response = MutableLiveData<PixelsResponse>()
    val apiResponse: LiveData<PixelsResponse> get() = _response

    val loading = mutableStateOf(false)
    fun wallpapers(apiKey: String, query: String, perPage: Int) {
        viewModelScope.launch {
            loading.value = true
            delay(2000)
            _response.value = repository.getWallpapers(apiKey, query, perPage)

            loading.value = false
        }
    }

}