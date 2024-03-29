package com.droid47.petpot.home.domain.usecases

import android.app.Application
import com.droid47.petpot.base.usecase.SynchronousUseCase
import com.google.android.gms.common.GoogleApiAvailability
import javax.inject.Inject

class PlayServiceAvailabilityUseCase @Inject constructor(
    private val application: Application
) : SynchronousUseCase<Int, Unit> {

    override fun execute(params: Unit): Int =
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(application)
}