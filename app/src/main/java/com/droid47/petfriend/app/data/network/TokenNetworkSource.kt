package com.droid47.petfriend.app.data.network

import com.droid47.petfriend.launcher.data.entities.ClientCredentialModel
import com.droid47.petfriend.launcher.data.entities.TokenModel
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenNetworkSource {

    @POST("/v2/oauth2/token")
    fun getAuthToken(@Body clientCredential: ClientCredentialModel): Single<TokenModel>
}