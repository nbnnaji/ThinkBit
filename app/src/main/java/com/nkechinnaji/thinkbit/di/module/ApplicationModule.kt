package com.nkechinnaji.thinkbit.di.module

import com.nkechinnaji.thinkbit.BuildConfig
import com.nkechinnaji.thinkbit.baseservice.NewsServiceInterface
import com.nkechinnaji.thinkbit.baseservice.WeatherServiceInterface
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
import javax.inject.Qualifier
import javax.inject.Singleton
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitOne

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitTwo


@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    @RetrofitOne
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.NEWS_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    @RetrofitTwo
    fun getRetrofit2(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("BuildConfig.WEATHER_URL")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Singleton
    @Provides
    fun getNewsApiService(@RetrofitOne retrofit: Retrofit): NewsServiceInterface {
        return retrofit
            .create(NewsServiceInterface::class.java)
    }

    @Singleton
    @Provides
    fun getWeatherApiService(@RetrofitTwo retrofit: Retrofit): WeatherServiceInterface {
        return retrofit
            .create(WeatherServiceInterface::class.java)
    }

    @Singleton
    @Provides
    fun buildRepo(
        apiService: NewsServiceInterface
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

//    @Singleton
//    @Provides
//    fun getNewsEndpointInterface(retrofit: Retrofit): NewsServiceInterface {
//        return retrofit
//            .create(NewsServiceInterface::class.java)
//    }

    // service with different base url
//    @Singleton
//    @Provides
//    fun getNewsEndpointInterface(retrofit: Retrofit): NewsServiceInterface {
//        return Retrofit.Builder()
//            .baseUrl(BuildConfig.NEWS_URL_2)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(NewsServiceInterface::class.java)
//    }
}