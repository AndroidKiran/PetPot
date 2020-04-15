package com.droid47.petfriend.base.extensions

import android.content.Context
import com.droid47.petfriend.R
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.search.data.models.search.ErrorEntity
import com.google.gson.Gson
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException

fun Throwable.getErrorRequestMessage(context: Context): Triple<String, String, String> {
    var errorMessage = context.getString(R.string.something_went_wrong)
    var errorSubTitle = context.getString(R.string.please_try_again)
    var actionText = context.getString(R.string.retry)

    when {
        this is HttpException -> {
            val errorBody = this.response()?.errorBody()?.string() ?: ""
            val errorEntity = Gson().fromJson(errorBody, ErrorEntity::class.java)

            try {
                errorMessage = errorEntity.invalidParams?.get(0)?.message ?: ""
                if (errorMessage.contains("location")) {
                    errorMessage = context.getString(R.string.non_serving_location)
                    errorSubTitle = context.getString(R.string.try_other_location)
                    actionText = context.getString(R.string.clear)
                }
            } catch (exception: Exception) {
                CrashlyticsExt.logHandledException(exception)
            }
        }

        this is ConnectException || !AppUtils.isNetworkAvailable(context) || this is UnknownHostException -> {
            errorMessage = context.getString(R.string.no_internet)
            errorSubTitle = context.getString(R.string.no_internet_subtitle)
        }

    }
    return Triple(errorMessage, errorSubTitle, actionText)
}