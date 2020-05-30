package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.storage.LocalPreferencesRepository
import com.droid47.petfriend.base.usecase.SynchronousUseCase
import javax.inject.Inject

class SaveLocationToPreferenceUseCase @Inject constructor(
    private val preferencesRepository: LocalPreferencesRepository
) : SynchronousUseCase<Unit, String> {

    override fun execute(params: String) {
        preferencesRepository.saveLocation(params)
    }
}