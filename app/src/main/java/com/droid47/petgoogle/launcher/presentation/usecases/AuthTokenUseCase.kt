package com.droid47.petgoogle.launcher.presentation.usecases

import androidx.work.ListenableWorker
import com.droid47.petgoogle.launcher.data.entities.ClientCredentialModel
import com.droid47.petgoogle.launcher.data.entities.TokenModel
import io.reactivex.Single

interface AuthTokenUseCase {

    fun getToken(credentialModel: ClientCredentialModel): Single<ListenableWorker.Result>

    fun getLocalToken(): TokenModel?
}