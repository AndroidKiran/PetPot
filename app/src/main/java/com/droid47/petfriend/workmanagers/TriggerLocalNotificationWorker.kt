package com.droid47.petfriend.workmanagers

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.work.*
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.d
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.workmanagers.domain.FetchBookmarkListUseCase
import com.droid47.petfriend.workmanagers.notification.BigTextStyle
import com.droid47.petfriend.workmanagers.notification.NotificationModel
import com.droid47.petfriend.workmanagers.notification.NotificationModel.NotificationChannel.Companion.CHANNEL_ID
import com.droid47.petfriend.workmanagers.notification.PetNotificationManager
import io.reactivex.Single
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

private val REQUEST_TAG = TriggerLocalNotificationWorker::class.java.simpleName

class TriggerLocalNotificationWorker @Inject constructor(
    private val application: Application,
    workerParameters: WorkerParameters,
    private val fetchBookmarkListUseCase: FetchBookmarkListUseCase
) : RxWorker(application, workerParameters) {

    override fun createWork(): Single<Result> =
        Single.fromCallable {
            isEvenDay()
        }.flatMap { isEvenDay ->
            d("Favourite===", "flatMap")
            if (!isEvenDay) {
                getFavouritePet()
            } else {
                Single.just(toNotificationModel())
            }
        }.doOnSuccess {
            d("Favourite===", "doOnSuccess")
            val notificationModel = it ?: return@doOnSuccess
            showNotification(notificationModel)
        }.map {
            d("Favourite===", "success")
            Result.success()
        }.onErrorReturn {
            d("Favourite===", "${it.printStackTrace()}   ==== Failure")
            Result.failure()
        }

    private fun isEvenDay(): Boolean {
        val dayOfWeek = try {
            Calendar.getInstance().apply {
                time = Date()
            }.get(Calendar.DAY_OF_WEEK)
        } catch (exception: Exception) {
            0
        }
        return dayOfWeek % 2 == 0
    }

    private fun getFavouritePet(): Single<NotificationModel> =
        fetchBookmarkListUseCase.buildUseCaseSingle()
            .map { baseStateModel ->
                createNotificationModelFrom(baseStateModel.data ?: emptyList())
            }

    private fun createNotificationModelFrom(petList: List<PetEntity>): NotificationModel =
        if (petList.isEmpty()) {
            toNotificationModel()
        } else {
            toNotificationModel(petList[Random.nextInt(0, petList.size)])
        }

    private fun toNotificationModel() =
        NotificationModel(
            channelId = CHANNEL_ID,
            contentTitle = application.getString(R.string.notification_content_title),
            contentText = application.getString(R.string.notification_content_text),
            notificationStyle = BigTextStyle,
            navigationArgs = Bundle().apply {
                putInt(NotificationModel.EXTRA_NAVIGATION_FRAGMENT_ID, R.id.navigation_search)
            }
        )

    private fun toNotificationModel(petEntity: PetEntity): NotificationModel =
        NotificationModel(
            channelId = CHANNEL_ID,
            contentTitle = String.format(
                application.getString(R.string.notification_favourite_content_title),
                petEntity.name ?: ""
            ),
            contentText = application.getString(R.string.notification_favourite_content_text),
            contentUrl = petEntity.getPetPhoto(),
            navigationArgs = Bundle().apply {
                putInt(NotificationModel.EXTRA_NAVIGATION_FRAGMENT_ID, R.id.navigation_pet_details)
                putParcelable(NotificationModel.EXTRA_PET_ENTITY, petEntity)
            }
        )

    private fun showNotification(notificationModel: NotificationModel) {
        PetNotificationManager(application).showNotification(notificationModel)
    }

    companion object {
        private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        private val periodicRequest = PeriodicWorkRequest.Builder(
            TriggerLocalNotificationWorker::class.java,
            20,
            TimeUnit.MINUTES,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MINUTES
        ).setConstraints(constraints)
            .addTag(REQUEST_TAG)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()


        fun enqueuePeriodicRequest(context: Context) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                REQUEST_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        }
    }
}