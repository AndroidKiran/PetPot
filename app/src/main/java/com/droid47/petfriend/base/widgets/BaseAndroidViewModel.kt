package com.droid47.petfriend.base.widgets

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.Disposable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseAndroidViewModel(application: Application) : AndroidViewModel(application) {

    private val requestHandler = RequestHandler<Int>()

    override fun onCleared() {
        requestHandler.dispose()
        super.onCleared()
    }

    fun cancelAllRequests() {
        requestHandler.deleteAll()
    }

    fun cancelRequest(requestId: Int) {
        requestHandler.delete(requestId)
    }

    fun registerRequest(requestId: Int, disposable: Disposable) {
        cancelRequest(requestId)
        requestHandler.add(requestId, disposable)
    }

    private inner class RequestHandler<T> : Disposable {
        private val disposablePool = ConcurrentHashMap<T, Disposable>()
        private val disposed = AtomicBoolean()

        override fun dispose() {
            if (disposed.compareAndSet(false, true)) {
                synchronized(disposablePool) {
                    for (disposable in disposablePool.values) {
                        disposable.dispose()
                    }
                    disposablePool.clear()
                }
            }
        }

        override fun isDisposed(): Boolean = disposed.get()

        fun add(id: T, disposable: Disposable) {
            if (disposed.get()) {
                disposable.dispose()
            } else {
                val prevDisposable = disposablePool.put(id, disposable)
                prevDisposable?.dispose()
            }
        }

        fun delete(id: T) {
            val disposable = disposablePool.remove(id)
            disposable?.dispose()
        }

        fun deleteAll() {
            synchronized(disposablePool) {
                for (disposable in disposablePool.values) {
                    disposable.dispose()
                }
                disposablePool.clear()
            }
        }
    }
}