package com.nkechinnaji.thinkbit.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nkechinnaji.thinkbit.base.viewmodel.BaseViewModel
import com.nkechinnaji.thinkbit.news.model.Articles
import com.nkechinnaji.thinkbit.news.service.NewsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val service: NewsService
) : BaseViewModel() {

     var everyNewsObserver = MutableLiveData<ArrayList<Articles>>()
     var emptyStateObserver = MutableLiveData<Boolean>()

    fun getEveryNews(){
        viewModelScope.launch {
          val response =  service.getEveryNewsService()
            if(response != null && response.totalResults != 0){
                everyNewsObserver.postValue(response.articles)
            }
        }

//        runSuspendedFunction({
//            service.getEveryNewsService()?.let {
//                everyNewsObserver.postValue(it.articles)
//                emptyStateObserver.postValue(false)
//            }
//        },{
//            emptyStateObserver.postValue(false)
//        })
    }
}