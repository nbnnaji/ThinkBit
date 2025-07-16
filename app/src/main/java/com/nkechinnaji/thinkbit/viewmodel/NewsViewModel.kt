package com.nkechinnaji.thinkbit.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nkechinnaji.thinkbit.news.model.Articles
import com.nkechinnaji.thinkbit.news.service.NewsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val service: NewsService
) : ViewModel() {

    var everyNewsObserver = MutableLiveData<ArrayList<Articles>>()
    var emptyStateObserver = MutableLiveData<Boolean>()
    var errorObserver = MutableLiveData<String?>()

    fun getEveryNews() {
        viewModelScope.launch {
            try {
                // Clear any previous error before making a new call
                errorObserver.postValue(null) // Or use a specific "clear error" event if preferred
                emptyStateObserver.postValue(false) // Assume not empty until proven otherwise

                val response = service.getEveryNewsService()

                if (response != null) {
                    if (response.totalResults != 0) {
                        everyNewsObserver.postValue(response.articles)
                        emptyStateObserver.postValue(false) // Assuming data means not empty
                    } else {
                        everyNewsObserver.postValue(ArrayList()) // Post empty list
                        emptyStateObserver.postValue(true)
                    }
                } else {
                    // Response object itself is null - likely a service layer issue before an HTTP error
                    emptyStateObserver.postValue(true)
                    everyNewsObserver.postValue(ArrayList())
                    errorObserver.postValue("Failed to retrieve news. Please try again later.")
                }
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Error fetching news", e)
                emptyStateObserver.postValue(true)
                everyNewsObserver.postValue(ArrayList()) // Ensure data observer also gets an empty list

                val errorMessage = when (e) {
                    is java.net.UnknownHostException -> "No internet connection."
                    is java.net.SocketTimeoutException -> "Connection timed out. Please check your network."
                    else -> "An unexpected error occurred: ${e.message}" // Or a generic message
                }
                errorObserver.postValue(errorMessage)
            }
        }
    }
}