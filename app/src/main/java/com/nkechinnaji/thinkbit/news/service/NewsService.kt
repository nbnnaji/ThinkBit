package com.nkechinnaji.thinkbit.news.service

import com.nkechinnaji.thinkbit.news.model.ArticlesResponse

interface NewsService {

    suspend fun getEveryNewsService(): ArticlesResponse?
}