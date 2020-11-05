package com.droid47.petpot.launcher.domain.interactors

import androidx.work.ListenableWorker
import com.droid47.petpot.base.extensions.applyIOSchedulers
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.launcher.data.entities.ClientCredentialModel
import com.droid47.petpot.launcher.data.entities.TokenModel
import com.droid47.petpot.launcher.domain.repositories.AuthTokenRepository
import com.droid47.petpot.launcher.presentation.usecases.AuthTokenUseCase
import com.google.gson.Gson
import io.reactivex.Single
import javax.inject.Inject

class AuthTokenInteractor @Inject constructor(
    private val authTokenRepository: AuthTokenRepository,
    private val gson: Gson,
    private val localPreferencesRepository: LocalPreferencesRepository
) : AuthTokenUseCase {

    override fun getLocalToken(): TokenModel? {
        val preferenceStr = localPreferencesRepository.fetchToken()
        return if (preferenceStr.isNullOrBlank()) {
            null
        } else {
            gson.fromJson(preferenceStr, TokenModel::class.java)
        }
    }

    override fun getToken(credentialModel: ClientCredentialModel): Single<ListenableWorker.Result> =
        authTokenRepository.getAuthToken(credentialModel)
            .applyIOSchedulers()
            .flatMap { tokenModel ->
                Single.fromCallable {
                    val tokenModelStr = gson.toJson(tokenModel)
                    localPreferencesRepository.saveToken(tokenModelStr)
                    return@fromCallable ListenableWorker.Result.success()
                }
            }.onErrorReturn {
                ListenableWorker.Result.failure()
            }
}