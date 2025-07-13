package com.nkechinnaji.thinkbit.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nkechinnaji.thinkbit.base.ApiResult
import com.nkechinnaji.thinkbit.base.LoadingState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

    protected fun <T> createMutableLiveData(): LiveData<T> = MutableLiveData()
    var isLoading: LiveData<LoadingState> = createMutableLiveData()

    protected fun <T> LiveData<T>.postValue(value: T) {
        when (this) {
            is MutableLiveData<T> -> postValue(value)
            else -> throw Exception("Not using createMutableLiveData() or createSingleLiveData() to create live data")
        }
    }

    protected fun <T> LiveData<T>.setValue(value: T) {
        when (this) {
            is MutableLiveData<T> -> setValue(value)
            else -> throw Exception("Not using createMutableLiveData() or createSingleLiveData() to create live data")
        }
    }

    fun <T> runSuspendedFunction(
        block: suspend CoroutineScope.() -> T,
        errorCallback: ((ApiResult.Error) -> Unit)? = null
    ) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            isLoading.postValue(LoadingState.LOADED)
            errorCallback?.invoke(ApiResult.Error(throwable))
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            val asyncJob = async {
                block
            }

            isLoading.postValue(LoadingState.LOADING)
            asyncJob.await()
            isLoading.postValue(LoadingState.LOADED)
        }
    }
}