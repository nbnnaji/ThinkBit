package com.nkechinnaji.thinkbit.news.repository

import com.nkechinnaji.thinkbit.news.model.ArticlesResponse

interface NewsRepository {
    suspend fun getAllNews(): ArticlesResponse?
}