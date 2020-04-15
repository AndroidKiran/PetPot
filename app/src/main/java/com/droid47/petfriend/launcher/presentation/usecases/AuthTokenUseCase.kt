package com.droid47.petfriend.launcher.presentation.usecases

import androidx.work.ListenableWorker
import com.droid47.petfriend.launcher.data.entities.ClientCredentialModel
import com.droid47.petfriend.launcher.data.entities.TokenModel
import io.reactivex.Single

interface AuthTokenUseCase {

    fun getToken(credentialModel: ClientCredentialModel): Single<ListenableWorker.Result>

    fun getLocalToken(): TokenModel?
}