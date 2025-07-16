package com.nkechinnaji.thinkbit.news.repository

import android.util.Log
import com.nkechinnaji.thinkbit.baseservice.ApiNewsInterface
import com.nkechinnaji.thinkbit.news.model.ArticlesResponse
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor (private val apiNewsInterface: ApiNewsInterface): NewsRepository {
    override suspend fun getAllNews(): ArticlesResponse? {
        Log.d("TAG", "getAllNews: ${apiNewsInterface.getEveryNews().body()}")
        return apiNewsInterface.getEveryNews().body()

    }
}