package com.example.wallifytask2.network

import com.example.wallifytask2.model.PixelsResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WallpapersApi {

    @GET("search")
    suspend fun searchWallpapers(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("per_page") perPage: Int
    ): PixelsResponse
}