package com.droid47.petpot.search.domain.interactors

import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.base.usecase.SynchronousUseCase
import javax.inject.Inject

class SaveLocationToPreferenceUseCase @Inject constructor(
    private val preferencesRepository: LocalPreferencesRepository
) : SynchronousUseCase<Unit, String> {

    override fun execute(params: String) {
        preferencesRepository.saveLocation(params)
    }
}