package com.droid47.petpot.base.widgets.components


import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class Rx2Timer private constructor(builder: Builder) {

    private val take: Long
    private val period: Long
    private val initialDelay: Long
    private val unit: TimeUnit
    private val onComplete: Action?
    private val onEmit: Consumer<Long>?
    private val onError: Consumer<Throwable>?
    private var pauseTake = 0L
    private var resumeTake = 0L

    private var isPause = false
    private var isStarted = false
    private var disposable: Disposable? = null

    init {
        take = builder.take
        period = builder.period
        initialDelay = builder.initialDelay
        unit = builder.unit
        onComplete = builder.onComplete
        onEmit = builder.onEmit
        onError = builder.onError
    }

    fun restart(): Rx2Timer {
        stop()
        return start()
    }

    fun start(): Rx2Timer {
        if (isPause) {
            return restart()
        }
        if (disposable == null || disposable?.isDisposed == true) {
            Observable.interval(initialDelay, period, unit)
                .subscribeOn(Schedulers.single())
                .take(take + 1)
                .map { aLong ->
                    pauseTake = aLong
                    take - aLong
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    override fun onComplete() {
                        onComplete?.run()
                    }

                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                        isStarted = true
                    }

                    override fun onNext(aLong: Long) {
                        onEmit?.accept(aLong)
                    }

                    override fun onError(throwable: Throwable) {
                        onError?.accept(throwable)
                    }
                })
        }
        return this
    }

    fun stop() {
        disposable?.dispose()
        if (isPause) {
            cleanPauseState()
        }
    }

    fun pause() {
        if (isPause || !isStarted) {
            return
        }
        stop()
        isPause = true
        resumeTake += pauseTake
    }

    fun resume() {
        if (!isPause) {
            return
        }
        isPause = false
        if (disposable == null || disposable?.isDisposed == true) {
            Observable.interval(initialDelay, period, unit)
                .subscribeOn(Schedulers.single())
                .take(take + 1 - resumeTake)
                .map { aLong ->
                    pauseTake = aLong
                    take - aLong - resumeTake
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    override fun onComplete() {
                        onComplete?.run()
                    }

                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                        isStarted = true
                    }

                    override fun onNext(aLong: Long) {
                        onEmit?.accept(aLong)
                    }

                    override fun onError(throwable: Throwable) {
                        onError?.accept(throwable)
                    }

                })
        }
    }

    private fun cleanPauseState() {
        isPause = false
        resumeTake = 0L
        pauseTake = 0L
        isStarted = false
    }

    class Builder internal constructor() {
        var take: Long = 60
        var period: Long = 1
        var initialDelay: Long = 0
        var unit: TimeUnit = TimeUnit.SECONDS
        var onComplete: Action? = null
        var onEmit: Consumer<Long>? = null
        var onError: Consumer<Throwable>? = null

        fun take(take: Int): Builder {
            this.take = take.toLong()
            return this
        }

        fun period(period: Int): Builder {
            this.period = period.toLong()
            return this
        }


        fun initialDelay(initialDelay: Int): Builder {
            this.initialDelay = initialDelay.toLong()
            return this
        }

        fun unit(unit: TimeUnit): Builder {
            this.unit = unit
            return this
        }

        fun onComplete(onComplete: Action): Builder {
            this.onComplete = onComplete
            return this
        }

        fun onEmit(onEmit: Consumer<Long>): Builder {
            this.onEmit = onEmit
            return this
        }

        fun onError(onError: Consumer<Throwable>): Builder {
            this.onError = onError
            return this
        }

        fun build(): Rx2Timer {
            return Rx2Timer(this)
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}