package com.droid47.petpot.base.widgets

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.droid47.petpot.base.livedata.NetworkConnectionLiveData
import io.reactivex.disposables.Disposable
import org.reactivestreams.Subscription
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseAndroidViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val requestDisposableHandler = RequestDisposableHandler<Long>()
    private val requestSubscriptionHandler = RequestSubscriptionHandler<Long>()
    val networkConnectionLiveData = NetworkConnectionLiveData(application)

    override fun onCleared() {
        cancelAllRequests()
        super.onCleared()
    }

    fun cancelAllRequests() {
        requestDisposableHandler.deleteAll()
        requestSubscriptionHandler.deleteAll()
    }

    fun cancelRequest(requestId: Long) {
        requestDisposableHandler.delete(requestId)
        requestSubscriptionHandler.delete(requestId)
    }

    fun registerDisposableRequest(requestId: Long, disposable: Disposable) {
        cancelRequest(requestId)
        requestDisposableHandler.add(requestId, disposable)
    }

    fun registerSubscriptionRequest(requestId: Long, subscription: Subscription) {
        cancelRequest(requestId)
        requestSubscriptionHandler.add(requestId, subscription)
    }

    private inner class RequestDisposableHandler<T> : Disposable {
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

    private inner class RequestSubscriptionHandler<T> : Subscription {
        private val disposablePool = ConcurrentHashMap<T, Subscription>()
        private val disposed = AtomicBoolean()

        fun add(id: T, disposable: Subscription) {
            if (disposed.get()) {
                disposable.cancel()
            } else {
                val prevDisposable = disposablePool.put(id, disposable)
                prevDisposable?.cancel()
            }
        }

        fun delete(id: T) {
            val disposable = disposablePool.remove(id)
            disposable?.cancel()
        }

        fun deleteAll() {
            synchronized(disposablePool) {
                for (disposable in disposablePool.values) {
                    disposable.cancel()
                }
                disposablePool.clear()
            }
        }

        override fun cancel() {
            if (disposed.compareAndSet(false, true)) {
                synchronized(disposablePool) {
                    for (disposable in disposablePool.values) {
                        disposable.cancel()
                    }
                    disposablePool.clear()
                }
            }
        }

        override fun request(id: Long) {
            try {
            } catch (exception: Exception) {

            }
        }
    }

}