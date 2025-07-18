package com.nkechinnaji.thinkbit.di.module

import com.nkechinnaji.thinkbit.BuildConfig
import com.nkechinnaji.thinkbit.network.ApiNewsInterface
import com.nkechinnaji.thinkbit.news.repository.NewsRepository
import com.nkechinnaji.thinkbit.news.repository.NewsRepositoryImpl
import com.nkechinnaji.thinkbit.news.service.NewsService
import com.nkechinnaji.thinkbit.news.service.NewsServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.NEWS_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun getNewsApiService(retrofit: Retrofit): ApiNewsInterface {
        return retrofit
            .create(ApiNewsInterface::class.java)
    }


    @Singleton
    @Provides
    fun buildRepo(
        apiService: ApiNewsInterface
    ): NewsRepository {
        return NewsRepositoryImpl(apiService)
    }

    @Singleton
    @Provides
    fun buildService(
        newsRepository: NewsRepository
    ): NewsService{
        return NewsServiceImpl(newsRepository)
    }

}