package com.droid47.petfriend.app.data.network

import com.droid47.petfriend.app.domain.repositories.LocalPreferencesRepository
import com.droid47.petfriend.launcher.data.entities.ClientCredentialModel
import com.droid47.petfriend.launcher.data.entities.TokenModel
import com.google.gson.Gson
import dagger.Lazy
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val localPreferencesRepository: LocalPreferencesRepository,
    private val gson: Gson,
    private val tokenNetworkSource: Lazy<TokenNetworkSource>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? =
        if (isInvalidRequest(response)) {
            val tokenModel =
                tokenNetworkSource.get().getAuthToken(ClientCredentialModel()).blockingGet()
            saveToken(tokenModel)
            newRequestWithAccessToken(
                response.request,
                "${tokenModel.tokenType} ${tokenModel.accessToken}"
            )
        } else {
            response.request
        }


    private fun saveToken(tokenModel: TokenModel) {
        val tokenModelStr = gson.toJson(tokenModel)
        localPreferencesRepository.saveToken(tokenModelStr)
    }

    private fun isInvalidRequest(response: Response): Boolean {
        val header = response.request.header("Authorization")
        return HTTP_UNAUTHORIZED == response.code || header == null || !header.startsWith("Bearer")
    }

    private fun newRequestWithAccessToken(
        request: Request,
        accessTokenWithBearer: String
    ): Request =
        request.newBuilder()
            .header("Authorization", accessTokenWithBearer)
            .build()

}