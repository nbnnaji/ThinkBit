package com.nkechinnaji.thinkbit.baseservice

import com.nkechinnaji.thinkbit.news.model.ArticlesResponse
import com.nkechinnaji.thinkbit.BuildConfig

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsServiceInterface {
    @GET("everything")
    suspend fun getEveryNews (
        @Query("q") weather: String = "weather",
        @Query("apiKey") apiKey: String = "84a14bd367b6466894466ebcb9da7612"
    ): Response<ArticlesResponse>
}