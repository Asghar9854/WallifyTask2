package com.example.wallifytask2.repository

import com.example.wallifytask2.model.PixelsResponse
import com.example.wallifytask2.network.RetrofitInstance

class ApiRepository {
    suspend fun getWallpapers(apiKey: String, query: String, perPage: Int): PixelsResponse {
        return RetrofitInstance.api.searchWallpapers(apiKey, query, perPage)
    }
}
