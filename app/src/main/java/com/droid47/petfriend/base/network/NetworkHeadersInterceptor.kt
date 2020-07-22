package com.droid47.petfriend.base.network

import com.droid47.petfriend.base.storage.LocalPreferencesRepository
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.launcher.data.entities.TokenModel
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class NetworkHeadersInterceptor @Inject constructor(
    private val localPreferencesRepository: LocalPreferencesRepository,
    private val gson: Gson
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Authorization", getTokenFrom(localPreferencesRepository, gson))

        val request = requestBuilder.method(original.method, original.body).build()
        return chain.proceed(request)
    }

    private fun getTokenFrom(sharedPreference: LocalPreferencesRepository, gson: Gson): String =
        try {
            val tokenStr = sharedPreference.fetchToken()
            val tokenModel = gson.fromJson(tokenStr, TokenModel::class.java)
            "${tokenModel.tokenType} ${tokenModel.accessToken}"
        } catch (e: Exception) {
            CrashlyticsExt.handleException(e)
            ""
        }
}