package com.nkechinnaji.thinkbit.news.repository

import android.util.Log
import com.nkechinnaji.thinkbit.baseservice.NewsServiceInterface
import com.nkechinnaji.thinkbit.news.model.ArticlesResponse
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor (val service: NewsServiceInterface): NewsRepository {
    override suspend fun getAllNews(): ArticlesResponse? {
        Log.d("TAG", "getAllNews: ${service.getEveryNews().body()}")
        return service.getEveryNews().body()

    }
}