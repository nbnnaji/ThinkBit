package com.nkechinnaji.thinkbit.network

import com.nkechinnaji.thinkbit.news.model.ArticlesResponse
import com.nkechinnaji.thinkbit.BuildConfig

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiNewsInterface {
    @GET("everything")
    suspend fun getEveryNews (
        @Query("q") weather: String = "weather",
        @Query("apiKey") apiKey: String = BuildConfig.KEY_URL
    ): Response<ArticlesResponse>
}