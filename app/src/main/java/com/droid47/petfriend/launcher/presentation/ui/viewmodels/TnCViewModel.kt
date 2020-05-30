package com.droid47.petfriend.launcher.presentation.ui.viewmodels

import android.app.Application
import com.droid47.petfriend.base.storage.LocalPreferencesRepository
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import com.droid47.petfriend.launcher.presentation.ui.models.WebViewModel
import javax.inject.Inject

class TnCViewModel @Inject constructor(
    application: Application,
    private val localPreferencesRepository: LocalPreferencesRepository
) : BaseAndroidViewModel(application) {

    val webViewModel = WebViewModel()

    fun updateTnCStatus() {
        localPreferencesRepository.saveTnCState()
    }
}