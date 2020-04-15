package com.droid47.petfriend.base.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.droid47.petfriend.base.firebase.CrashlyticsExt


fun Context.openAppPageInPlayStore() {
    val intent =
        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(this, "App not Unavailable", Toast.LENGTH_SHORT).show()
    }
}

fun Context.openUrlInBrowser(url: String?) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(this, "Browse option Unavailable", Toast.LENGTH_SHORT).show()
    }
}

fun Context.sendEmail(sendTo: Array<String>, subject: String? = null, body: String? = null) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "plain/text"
        putExtra(Intent.EXTRA_EMAIL, sendTo)
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(Intent.createChooser(intent, ""))
    } else {
        Toast.makeText(this, "Mail Unavailable", Toast.LENGTH_SHORT).show()
    }
}

fun Context.shareMyApp(subject: String?, message: String) {
    try {
        val appUrl =
            "https://play.google.com/store/apps/details?id=$packageName"
        val shareAppIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            var leadingText = "\n" + message + "\n\n"
            leadingText += appUrl + "\n\n"
            putExtra(Intent.EXTRA_TEXT, leadingText)
        }
        startActivity(Intent.createChooser(shareAppIntent, "Share using"))
    } catch (e: Exception) {
        CrashlyticsExt.logHandledException(e)
    }
}

fun Context.rateMyApp() {
    val uri = Uri.parse("market://details?id=$packageName")
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    goToMarket.addFlags(
        Intent.FLAG_ACTIVITY_NO_HISTORY
                or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
                or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
    )
    try {
        startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
        )
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Playstore Unavailable", Toast.LENGTH_SHORT).show()
        }
        CrashlyticsExt.logHandledException(e)
    }
}

fun Context.openDialer(phoneNum: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNum")
    }
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(this, "Dialer Unavailable", Toast.LENGTH_SHORT).show()
    }
}

fun Context.share(sharingMsg: String?, emailSubject: String?, title: String?) {
    try {
        val sharingIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sharingMsg)
            putExtra(Intent.EXTRA_SUBJECT, emailSubject)
        }
        startActivity(Intent.createChooser(sharingIntent, title))
    } catch (exception: Exception) {
        CrashlyticsExt.logHandledException(exception)
    }
}

fun Context.showLocationInMap(locationName: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$locationName"))
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(this, "Map Unavailable", Toast.LENGTH_SHORT).show()
    }
}