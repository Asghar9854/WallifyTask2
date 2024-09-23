package com.example.wallifytask2.data.repository

import com.example.wallifytask2.domain.model.PixelsResponse
import com.example.wallifytask2.data.network.RetrofitInstance

class ApiRepository {
    suspend fun getWallpapers(apiKey: String, query: String, perPage: Int): PixelsResponse {
        return RetrofitInstance.api.searchWallpapers(apiKey, query, perPage)
    }
}
