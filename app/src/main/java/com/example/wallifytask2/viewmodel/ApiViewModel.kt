package com.example.wallifytask2.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallifytask2.domain.model.PixelsResponse
import com.example.wallifytask2.data.repository.ApiRepository
import kotlinx.coroutines.launch

class ApiViewModel(val repository: ApiRepository) : ViewModel() {
    private val _response = MutableLiveData<PixelsResponse>()
    val apiResponse: LiveData<PixelsResponse> get() = _response

    val loading = mutableStateOf(false)

    // Error State
    val error = mutableStateOf<String?>(null)


    fun wallpapers(apiKey: String, query: String, perPage: Int) {
        loading.value = true
        error.value = null
        viewModelScope.launch {
            try {
                _response.value = repository.getWallpapers(apiKey, query, perPage)
            } catch (e: Exception) {
                error.value = "Error loading data"
            } finally {
                loading.value = false
            }
        }
    }

}