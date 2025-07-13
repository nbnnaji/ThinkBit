package com.nkechinnaji.thinkbit.base

data class LoadingState private constructor(val status: Status, val msg: String? = null){
    companion object {
        val IDLE = LoadingState(Status.IDLE)
        val LOADED = LoadingState(Status.SUCCESS)
        val LOADING = LoadingState(Status.RUNNING)
        fun error(msg: String?) = LoadingState(Status.FAILED, msg)
    }

    enum class Status {
        IDLE,
        RUNNING,
        SUCCESS,
        FAILED,
    }
    }

