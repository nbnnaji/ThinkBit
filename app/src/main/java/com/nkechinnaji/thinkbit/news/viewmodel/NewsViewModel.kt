package com.nkechinnaji.thinkbit.news.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nkechinnaji.thinkbit.news.model.Articles
import com.nkechinnaji.thinkbit.news.service.NewsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val service: NewsService
) : ViewModel() {

    private var everyNewsObserver = MutableLiveData<List<Articles>>(arrayListOf())
    val everyNewsLd : LiveData<List<Articles>> = everyNewsObserver

    private var errorObserver = MutableLiveData<String?>()
    val errorLd : LiveData<String?> = errorObserver // down casting to live data to avoid mutation outside

    fun getEveryNews() {
        viewModelScope.launch {
            try {
                // Clear any previous error before making a new call
                errorObserver.postValue(null) // Or use a specific "clear error" event if preferred

                val response = service.getEveryNewsService()

                if (response != null) {
                    if (response.totalResults != 0) {
                        everyNewsObserver.postValue(response.articles)
                    } else {
                        everyNewsObserver.postValue(ArrayList()) // Post empty list
                    }
                } else {
                    // Response object itself is null - likely a service layer issue before an HTTP error
                    everyNewsObserver.postValue(ArrayList())
                    errorObserver.postValue("Failed to retrieve news. Please try again later.")
                }
            } catch (e: Exception) {
                everyNewsObserver.postValue(ArrayList()) // Ensure data observer also gets an empty list

                val errorMessage = when (e) {
                    is UnknownHostException -> "No internet connection."
                    is SocketTimeoutException -> "Connection timed out. Please check your network."
                    else -> "An unexpected error occurred: ${e.message}" // Or a generic message
                }
                errorObserver.postValue(errorMessage)
            }
        }
    }
}