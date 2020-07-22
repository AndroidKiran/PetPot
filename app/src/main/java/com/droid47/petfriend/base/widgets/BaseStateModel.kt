package com.droid47.petfriend.base.widgets

sealed class BaseStateModel<T> {
    abstract val data: T?
}

data class Success<T>(override val data: T) : BaseStateModel<T>()
data class Failure<T>(val error: Throwable, override val data: T? = null) : BaseStateModel<T>()
data class Loading<T>(override val data: T? = null) : BaseStateModel<T>()
data class Empty<T>(override val data: T? = null) : BaseStateModel<T>()