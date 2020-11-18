package com.droid47.petpot.workmanagers.notification

import android.app.*
import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.droid47.petpot.R
import com.droid47.petpot.base.extensions.BuildVersionUtil
import com.droid47.petpot.base.extensions.getDimen
import com.droid47.petpot.base.extensions.getScreenWidth
import com.droid47.petpot.workmanagers.notification.NotificationModel.NotificationChannel.Companion.CHANNEL_ID
import javax.inject.Inject
import kotlin.random.Random

private const val GROUP_KEY_PET_ALERT = "com.android.example.PET_ALERT"

class PetNotificationManager @Inject constructor(private val context: Application) {

    fun showNotification(notificationModel: NotificationModel) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = getNotificationChannel(notificationModel.notificationChannel)
        if (notificationChannel != null && BuildVersionUtil.isOreoOrHigher) {
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.cancelAll()
        notificationManager.notify(
            Random.nextInt(1, 100),
            buildNotification(notificationModel)
        )
    }

    fun clearAllNotification() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    private fun buildNotification(notificationModel: NotificationModel): Notification =
        NotificationCompat.Builder(context, notificationModel.channelId ?: CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(notificationModel.contentTitle)
            .setContentText(notificationModel.contentText)
            .setPriority(notificationModel.priority)
            .setNotificationStyle(notificationModel)
            .setContentIntent(getContentIntent(notificationModel))
            .setAutoCancel(notificationModel.notificationAutoCancel)
            .setGroup(GROUP_KEY_PET_ALERT)
            .setGroupSummary(true)
            .build()

    private fun NotificationCompat.Builder.setNotificationStyle(notificationModel: NotificationModel): NotificationCompat.Builder {
        when (notificationModel.notificationStyle) {
            BigPictureStyle -> {
                val bitmap = Glide.with(context)
                    .asBitmap()
                    .apply(
                        RequestOptions.bitmapTransform(
                            CropTransformation(
                                context.getScreenWidth(),
                                context.getDimen(R.dimen.notification_pic_height),
                                CropTransformation.CropType.TOP
                            )
                        )
                    ).load(notificationModel.contentUrl)
                    .submit(
                        context.getScreenWidth(),
                        context.getDimen(R.dimen.notification_pic_height)
                    )
                    .get() ?: BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_launcher_foreground
                )

                setLargeIcon(bitmap)
                setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null)
                )
            }
            else -> {
                setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(notificationModel.contentText)
                )
            }
        }
        return this
    }

    private fun getContentIntent(notificationModel: NotificationModel): PendingIntent =
        NavDeepLinkBuilder(context)
            .setGraph(notificationModel.navigationGraphId)
            .setDestination(notificationModel.navigationFragmentId)
            .setArguments(notificationModel.navigationArgs)
            .createPendingIntent()

    private fun getNotificationChannel(notificationChannel: NotificationModel.NotificationChannel): NotificationChannel? =
        if (BuildVersionUtil.isOreoOrHigher) {
            NotificationChannel(
                notificationChannel.channelId,
                notificationChannel.channelName,
                notificationChannel.channelImportance
            ).apply {
                description = notificationChannel.channelDescription
            }
        } else {
            null
        }

}