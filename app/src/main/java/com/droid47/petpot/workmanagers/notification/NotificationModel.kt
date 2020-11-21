package com.droid47.petpot.workmanagers.notification

import android.os.Bundle
import android.os.Parcelable
import androidx.core.app.NotificationCompat
import com.droid47.petpot.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationModel(
    val channelId: String? = null,
    val contentTitle: String? = null,
    val contentText: String? = null,
    val contentUrl: String? = null,
    val notificationStyle: NotificationStyle = BigPictureStyle,
    val navigationGraphId: Int = R.navigation.launcher_navigation_graph,
    val navigationFragmentId: Int = R.id.navigation_splash,
    val navigationArgs: Bundle = Bundle(),
    val actionPrimary: String? = null,
    val actionSecondary: String? = null,
    val notificationChannel: NotificationChannel = NotificationChannel(channelId ?: ""),
    val priority: Int = NotificationCompat.PRIORITY_DEFAULT,
    val notificationAutoCancel: Boolean = true
) : Parcelable {

    @Parcelize
    data class NotificationChannel(
        val channelId: String? = null,
        val channelName: String = CHANNEL_SEARCH,
        val channelDescription: String = CHANNEL_DESCRIPTION,
        val channelImportance: Int = android.app.NotificationManager.IMPORTANCE_DEFAULT
    ) : Parcelable {

        companion object {
            const val CHANNEL_SEARCH = "channel_pet_notify"
            const val CHANNEL_DESCRIPTION = ""
            const val CHANNEL_ID = "pet_notification_id"
        }
    }

    companion object {
        const val VIEW = "View"
        const val SHARE = "Share"
        const val FIND_A_FRND = "Find a friend"
        const val EXTRA_NAVIGATION_FRAGMENT_ID = "navigation_fragment_id"
        const val EXTRA_PET_ENTITY = "petEntity"
    }
}

sealed class NotificationStyle : Parcelable

@Parcelize
object BigTextStyle : NotificationStyle()

@Parcelize
object BigPictureStyle : NotificationStyle()


