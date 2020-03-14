package com.droid47.petgoogle.base.extensions

import android.content.Context
import android.net.ConnectivityManager
import com.droid47.petgoogle.base.firebase.CrashlyticsExt

object AppUtils {

    @JvmStatic
    fun isNetworkAvailable(context: Context): Boolean {
        try {
            val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        } catch (e: Exception) {
            CrashlyticsExt.logHandledException(e)
            // Exception: saying no permission, but we are asking for permission.
//            CrashLogUtils.handleException(e)
        }

        // Returning true if exception happens, else no internet connection card will come even
        // internet is available, which is a buggy state. Returning true wont show error card.
        return true
    }

    @JvmStatic
    fun applyBracketFilter(inputStr: String): String {
        var updatedStr = inputStr.substring(1, inputStr.length - 1)
        updatedStr = updatedStr.replace(", $", "")
        return updatedStr
    }
}