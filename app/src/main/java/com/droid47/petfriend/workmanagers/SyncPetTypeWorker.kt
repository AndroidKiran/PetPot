package com.droid47.petfriend.workmanagers

import android.app.Application
import android.content.Context
import androidx.work.*
import com.droid47.petfriend.base.extensions.applyIOSchedulers
import com.droid47.petfriend.base.extensions.log
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.launcher.domain.interactors.SyncPetTypeUseCase
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val REQUEST_TAG = SyncPetTypeWorker::class.java.simpleName

class SyncPetTypeWorker @Inject constructor(
    application: Application,
    workerParameters: WorkerParameters,
    private val syncPetTypeUseCase: SyncPetTypeUseCase
) : RxWorker(application, workerParameters) {

    override fun createWork(): Single<Result> =
        syncPetTypeUseCase.buildUseCaseSingle(false)
            .applyIOSchedulers()
            .map {
                log("SyncPetType=====", "SUCCESS")
                when (it) {
                    is Failure -> Result.failure()
                    else -> Result.success()
                }
            }.onErrorReturn {
                log("SyncPetType=====", "FAILURE")
                Result.failure()
            }

    companion object {
        private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.METERED)
            .build()

        private val periodicRequest = PeriodicWorkRequest.Builder(
            SyncPetTypeWorker::class.java,
            15,
            TimeUnit.DAYS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MINUTES
        ).setConstraints(constraints)
            .addTag(REQUEST_TAG)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()

        private val request = OneTimeWorkRequest.Builder(SyncPetTypeWorker::class.java)
            .setConstraints(constraints)
            .addTag(REQUEST_TAG)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()

        fun enqueueRequest(context: Context) {
            WorkManager.getInstance(context).enqueueUniqueWork(
                REQUEST_TAG,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }

        fun enqueuePeriodicRequest(context: Context) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                REQUEST_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        }
    }

}