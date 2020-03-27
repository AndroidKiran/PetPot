package com.droid47.petgoogle.launcher.data.repositories

import com.droid47.petgoogle.app.domain.repositories.LocalPreferencesRepository
import com.droid47.petgoogle.launcher.data.datasources.TokenNetworkSource
import com.droid47.petgoogle.launcher.data.entities.ClientCredentialModel
import com.droid47.petgoogle.launcher.data.entities.TokenModel
import com.droid47.petgoogle.launcher.domain.repositories.AuthTokenRepository
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