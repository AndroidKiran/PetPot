package com.droid47.petpot.workmanagers

import android.app.Application
import android.content.Context
import androidx.work.*
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.launcher.domain.interactors.SyncPetTypeUseCase
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val REQUEST_TAG = SyncPetTypeWorker::class.java.simpleName
private val IMMEDIATE_REQUEST_TAG = "${REQUEST_TAG}_IMMEDIATE"
private val PERIODIC_REQUEST_TAG = "${REQUEST_TAG}_PERIODIC"

class SyncPetTypeWorker @Inject constructor(
    application: Application,
    workerParameters: WorkerParameters,
    private val syncPetTypeUseCase: SyncPetTypeUseCase
) : RxWorker(application, workerParameters) {

    override fun createWork(): Single<Result> =
        syncPetTypeUseCase.buildUseCaseSingleWithSchedulers(false)
            .map {
                when (it) {
                    is Failure -> Result.failure()
                    else -> Result.success()
                }
            }.onErrorReturn {
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
            .addTag(PERIODIC_REQUEST_TAG)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()

        fun enqueuePeriodicRequest(context: Context) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_REQUEST_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        }

        private val request = OneTimeWorkRequest.Builder(SyncPetTypeWorker::class.java)
            .setConstraints(constraints)
            .addTag(IMMEDIATE_REQUEST_TAG)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()

        fun enqueueRequest(context: Context) {
            WorkManager.getInstance(context).enqueueUniqueWork(
                IMMEDIATE_REQUEST_TAG,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }

        fun getOneTimeRequestStatus(context: Context) =
            WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(REQUEST_TAG)
    }

}