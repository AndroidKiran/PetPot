package com.droid47.petfriend.app.data.network

import com.droid47.petfriend.app.domain.repositories.LocalPreferencesRepository
import com.droid47.petfriend.app.data.network.TokenNetworkSource
import com.droid47.petfriend.launcher.data.entities.ClientCredentialModel
import com.droid47.petfriend.launcher.data.entities.TokenModel
import com.google.gson.Gson
import dagger.Lazy
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val localPreferencesRepository: LocalPreferencesRepository,
    private val gson: Gson,
    private val tokenNetworkSource: Lazy<TokenNetworkSource>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val tokenModel =
            tokenNetworkSource.get().getAuthToken(ClientCredentialModel()).blockingGet()
        saveToken(tokenModel)
        return response.request.newBuilder()
            .header("Authorization", "${tokenModel.tokenType} ${tokenModel.accessToken}")
            .build()
    }

    private fun saveToken(tokenModel: TokenModel) {
        val tokenModelStr = gson.toJson(tokenModel)
        localPreferencesRepository.saveToken(tokenModelStr)
    }
}