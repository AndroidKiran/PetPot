package com.droid47.petgoogle.workmanagers
//
//import android.content.Context
//import androidx.work.*
//import com.droid47.petgoogle.BuildConfig
//import com.droid47.petgoogle.bookmark.domain.interactors.FetchBookmarkListUseCase
//import com.droid47.petgoogle.launcher.domain.interactors.RefreshAuthTokenAndPetTypeUseCase
//import io.reactivex.Single
//import java.util.concurrent.TimeUnit
//import javax.inject.Inject
//
//private const val REQUEST_TAG = "${BuildConfig.APPLICATION_ID}.SyncAnimalType"
//
//class FavouritePetNotificationJob @Inject constructor(private val workManager: WorkManager) {
//
//    @Inject
//    lateinit var bookmarkListUseCase: FetchBookmarkListUseCase
//
//    private val constraints = Constraints.Builder()
//        .setRequiredNetworkType(NetworkType.METERED)
//        .build()
//
//    private val periodicRequest = PeriodicWorkRequest
//        .Builder(
//            NotificationWorker::class.java, 20,
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
//    fun enqueueRequest() {
//        workManager
//            .enqueueUniquePeriodicWork(
//                REQUEST_TAG,
//                ExistingPeriodicWorkPolicy.REPLACE,
//                periodicRequest
//            )
//    }
//
//    inner class NotificationWorker(context: Context, workerParameters: WorkerParameters) :
//        RxWorker(context, workerParameters) {
//        override fun createWork(): Single<Result> {
//            return bookmarkListUseCase.buildUseCaseObservable(Unit)
//                .singleOrError()
//                .flatMap {
//                    Single.just(Result.success())
//                }
//        }
//    }
//}