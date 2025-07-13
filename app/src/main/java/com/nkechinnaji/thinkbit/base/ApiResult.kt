package com.nkechinnaji.thinkbit.base

sealed class ApiResult<out R> {

    data class Error(val exception: Throwable) : ApiResult<Nothing>()

    override fun toString(): String {
        return when(this){
            is Error -> "Error[exception=$exception]"
        }
    }
}