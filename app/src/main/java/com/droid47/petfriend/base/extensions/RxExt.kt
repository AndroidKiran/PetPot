package com.droid47.petfriend.base.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.droid47.petfriend.base.widgets.components.RxStreamLiveEvent
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Publisher

fun <T> Single<T>.applyIOSchedulers(): Single<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.applyIOSchedulers(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun Completable.applyIOSchedulers(): Completable {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Flowable<T>.applyIOSchedulers(): Flowable<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun CompositeDisposable?.clearDisposable() {
    this?.let { if (!this.isDisposed) this.clear() }
}

fun Disposable?.disposeDisposable() {
    this?.let { if (!this.isDisposed) this.dispose() }
}

fun <T> Publisher<T>.toLiveData(): LiveData<T> = LiveDataReactiveStreams.fromPublisher(this)

fun <T> Publisher<T>.toSingleLiveData(): RxStreamLiveEvent<T> = RxStreamLiveEvent(this)
