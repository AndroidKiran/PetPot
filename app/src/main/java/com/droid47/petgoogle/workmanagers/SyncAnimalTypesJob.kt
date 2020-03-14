package com.droid47.petgoogle.workmanagers

//import android.content.Context
//import androidx.work.*
//import androidx.work.WorkRequest.MIN_BACKOFF_MILLIS
//import com.droid47.petgoogle.BuildConfig
//import com.droid47.petgoogle.launcher.domain.interactors.RefreshAuthTokenAndPetTypeUseCase
//import io.reactivex.Single
//import java.util.concurrent.TimeUnit
//import javax.inject.Inject
//
//private const val REQUEST_TAG = "${BuildConfig.APPLICATION_ID}.SyncAnimalType"
//
//class SyncAnimalTypesJob @Inject constructor(private val workManager: WorkManager) {
//
//    private val constraints = Constraints.Builder()
//        .setRequiredNetworkType(NetworkType.UNMETERED)
//        .build()
//
//    private val periodicRequest = PeriodicWorkRequest
//        .Builder(
//            AnimalTypeWorker::class.java, 20,
//            TimeUnit.MINUTES,
//            30,
//            TimeUnit.MINUTES
//        )
//        .setConstraints(constraints)
//        .addTag(REQUEST_TAG)
//        .setBackoffCriteria(
//            BackoffPolicy.LINEAR,
//            PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
//            TimeUnit.MILLISECONDS
//        ).build()
//
//    private val request = OneTimeWorkRequest.Builder(AnimalTypeWorker::class.java)
//        .setConstraints(constraints)
//        .addTag(REQUEST_TAG)
//        .setBackoffCriteria(
//            BackoffPolicy.LINEAR,
//            MIN_BACKOFF_MILLIS,
//            TimeUnit.MILLISECONDS
//        ).build()
//
//    fun enqueueRequest() {
////        workManager
////            .enqueueUniquePeriodicWork(
////                REQUEST_TAG,
////                ExistingPeriodicWorkPolicy.REPLACE,
////                periodicRequest
////            )
//
//        workManager.enqueueUniqueWork(
//            REQUEST_TAG,
//            ExistingWorkPolicy.REPLACE,
//            request
//        )
//    }
//
//    class AnimalTypeWorker(context: Context, workerParameters: WorkerParameters) :
//        RxWorker(context, workerParameters) {
//        @Inject
//        lateinit var refreshAuthTokenAndPetTypeUseCase: RefreshAuthTokenAndPetTypeUseCase
//
//        override fun createWork(): Single<Result> {
//            return refreshAuthTokenAndPetTypeUseCase.buildUseCaseSingle(Unit)
//                .flatMap {
//                    Single.just(Result.success())
//                }.onErrorReturn {
//                    Result.failure()
//                }
//        }
//    }
//}