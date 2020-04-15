package com.droid47.petfriend.launcher.domain.repositories

import com.droid47.petfriend.launcher.data.entities.ClientCredentialModel
import com.droid47.petfriend.launcher.data.entities.TokenModel
import io.reactivex.Single

interface AuthTokenRepository {

    fun getAuthToken(credentialModel: ClientCredentialModel): Single<TokenModel>

    fun cacheToken(tokenModel: TokenModel)
}