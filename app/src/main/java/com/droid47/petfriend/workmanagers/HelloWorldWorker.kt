package com.droid47.petfriend.workmanagers

import android.app.Application
import android.widget.Toast
import androidx.work.OneTimeWorkRequest
import androidx.work.RxWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import io.reactivex.Single
import javax.inject.Inject

class HelloWorldWorker @Inject constructor(
    private val application: Application,
    workerParameters: WorkerParameters
) : RxWorker(application, workerParameters) {

    override fun createWork(): Single<Result> {
        return Single.just("Worker Running")
            .doOnSuccess { message ->
                Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
            }.map { Result.success() }
            .onErrorReturnItem(Result.failure())
    }

    companion object {
        fun start() {
            WorkManager
                .getInstance()
                .enqueue(OneTimeWorkRequest.from(HelloWorldWorker::class.java))
        }
    }
}