package com.nkechinnaji.thinkbit.news.service

import com.nkechinnaji.thinkbit.news.model.ArticlesResponse
import com.nkechinnaji.thinkbit.news.repository.NewsRepository
import javax.inject.Inject

class NewsServiceImpl  @Inject constructor  (private val repository: NewsRepository): NewsService {
    override suspend fun getEveryNewsService(): ArticlesResponse? {
       return repository.getAllNews()
    }
}