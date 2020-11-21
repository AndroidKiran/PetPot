package com.droid47.petpot.base.widgets.components

import androidx.annotation.MainThread
import androidx.collection.ArraySet
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.atomic.AtomicReference

class RxStreamLiveEvent<T> constructor(private val publisher: Publisher<T>) :
    MediatorLiveData<T>() {
    private val observers = ArraySet<LiveEventObserver<in T>>()
    private val subscriber = AtomicReference<LiveDataSubscriber>()

    override fun onActive() {
        super.onActive()
        val liveDataSubscriber = LiveDataSubscriber()
        subscriber.set(liveDataSubscriber)
        publisher.subscribe(liveDataSubscriber)
    }

    override fun onInactive() {
        super.onInactive()
        subscriber.getAndSet(null)?.cancelSubscription()
    }

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val wrapper = LiveEventObserver(observer)
        observers.add(wrapper)
        super.observe(owner, wrapper)
    }

    @MainThread
    override fun removeObserver(observer: Observer<in T>) {
        if (observers.remove(observer)) {
            super.removeObserver(observer)
            return
        }
        val iterator = observers.iterator()
        while (iterator.hasNext()) {
            val wrapper = iterator.next()
            if (wrapper.observer == observer) {
                iterator.remove()
                super.removeObserver(wrapper)
                break
            }
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        observers.forEach { it.newValue() }
        super.setValue(t)
    }

    override fun postValue(value: T?) {
        observers.forEach { it.newValue() }
        super.postValue(value)
    }

    private class LiveEventObserver<T>(val observer: Observer<T>) : Observer<T> {

        private var pending = false

        override fun onChanged(t: T?) {
            if (pending) {
                pending = false
                observer.onChanged(t)
            }
        }

        fun newValue() {
            pending = true
        }
    }

    inner class LiveDataSubscriber : AtomicReference<Subscription?>(), Subscriber<T> {

        override fun onSubscribe(s: Subscription) {
            if (compareAndSet(null, s)) {
                s.request(Long.MAX_VALUE)
            } else {
                s.cancel()
            }
        }

        override fun onNext(item: T) {
            postValue(item)
        }

        override fun onError(ex: Throwable) {
            subscriber.compareAndSet(this, null)
        }

        override fun onComplete() {
            subscriber.compareAndSet(this, null)
        }

        fun cancelSubscription() {
            val s = get()
            s?.cancel()
        }
    }
}