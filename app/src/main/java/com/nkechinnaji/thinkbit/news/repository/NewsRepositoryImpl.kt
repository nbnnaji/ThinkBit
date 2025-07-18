package com.nkechinnaji.thinkbit.news.repository

import android.util.Log
import com.nkechinnaji.thinkbit.network.ApiNewsInterface
import com.nkechinnaji.thinkbit.news.model.Articles
import com.nkechinnaji.thinkbit.news.model.ArticlesResponse
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor (private val apiNewsInterface: ApiNewsInterface): NewsRepository {
    override suspend fun getAllNews(): ArticlesResponse? {
        Log.d("TAG", "getAllNews: ${apiNewsInterface.getEveryNews().body()}")
        try {
            return apiNewsInterface.getEveryNews().body()
        }catch (e: Exception){
            Log.d("TAG", "getAllNews: ${e.message}")
            return null
        }
    }
}