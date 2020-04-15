package com.droid47.petfriend.base.widgets

sealed class BaseStateModel<T> {
    abstract val data: T?
}

class Success<T>(override val data: T) : BaseStateModel<T>()
class Failure<T>(val error: Throwable, override val data: T? = null) : BaseStateModel<T>()
class Loading<T>(override val data: T? = null) : BaseStateModel<T>()
class Empty<T>(override val data: T? = null) : BaseStateModel<T>()