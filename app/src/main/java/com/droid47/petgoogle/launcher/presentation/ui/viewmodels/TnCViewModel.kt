package com.droid47.petgoogle.launcher.presentation.ui.viewmodels

import android.app.Application
import com.droid47.petgoogle.app.domain.repositories.LocalPreferencesRepository
import com.droid47.petgoogle.base.widgets.BaseAndroidViewModel
import com.droid47.petgoogle.launcher.presentation.ui.models.WebViewModel
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