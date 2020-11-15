package com.droid47.petpot.launcher.data.repositories

import com.droid47.petpot.base.network.TokenNetworkSource
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.launcher.data.entities.ClientCredentialModel
import com.droid47.petpot.launcher.data.entities.TokenModel
import com.droid47.petpot.launcher.domain.repositories.AuthTokenRepository
import com.google.gson.Gson
import io.reactivex.Single
import javax.inject.Inject

class AuthTokenRepo @Inject constructor(
    private val networkSource: TokenNetworkSource,
    private val localSharedPreferencesRepository: LocalPreferencesRepository,
    private val gson: Gson
) : AuthTokenRepository {

    override fun getAuthToken(credentialModel: ClientCredentialModel): Single<TokenModel> =
        networkSource.getAuthToken(credentialModel)

    override fun cacheToken(tokenModel: TokenModel) {
        val tokenModelStr = gson.toJson(tokenModel)
        localSharedPreferencesRepository.saveToken(tokenModelStr)
    }
}