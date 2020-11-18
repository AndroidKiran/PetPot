package com.droid47.petpot.base.network

import com.droid47.petpot.launcher.data.entities.ClientCredentialModel
import dagger.Lazy
import okhttp3.*
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import javax.inject.Inject

class TokenAuthenticatorInterceptor @Inject constructor(
    private val tokenNetworkSource: Lazy<TokenNetworkSource>
) : Authenticator, Interceptor {

    var token: String = ""

    override fun authenticate(route: Route?, response: Response): Request? =
        if (isInvalidRequest(response)) {
            val tokenModel =
                tokenNetworkSource.get().getAuthToken(ClientCredentialModel()).blockingGet()
            token = "${tokenModel.tokenType} ${tokenModel.accessToken}"
            newRequestWithAccessToken(response.request, token)
        } else {
            response.request
        }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Authorization", token)

        val request = requestBuilder.method(original.method, original.body).build()
        return chain.proceed(request)
    }

    private fun isInvalidRequest(response: Response): Boolean {
        val header = response.request.header("Authorization")
        return HTTP_UNAUTHORIZED == response.code || header.isNullOrEmpty() || !header.startsWith("Bearer")
    }

    private fun newRequestWithAccessToken(
        request: Request,
        accessTokenWithBearer: String
    ): Request =
        request.newBuilder()
            .header("Authorization", accessTokenWithBearer)
            .build()

}