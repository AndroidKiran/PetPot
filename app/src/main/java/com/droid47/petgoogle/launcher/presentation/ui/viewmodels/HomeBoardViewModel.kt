package com.droid47.petgoogle.launcher.presentation.ui.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.droid47.petgoogle.R
import com.droid47.petgoogle.app.domain.repositories.LocalPreferencesRepository
import com.droid47.petgoogle.base.widgets.BaseAndroidViewModel
import javax.inject.Inject

class HomeBoardViewModel @Inject constructor(
    application: Application,
    val localPreferencesRepository: LocalPreferencesRepository
) : BaseAndroidViewModel(application) {

    private val introTitleArray = application.resources.getStringArray(R.array.introTitleArray)
    private val introDescArray = application.resources.getStringArray(R.array.introDescriptionArray)

    val positionLiveData = MutableLiveData<Int>()

    val titleLiveData: LiveData<String> = Transformations.map(positionLiveData) { position ->
        introTitleArray[position ?: 0]
    }

    val descLiveData: LiveData<String> = Transformations.map(positionLiveData) { position ->
        introDescArray[position ?: 0]
    }



    companion object {
        const val START_POSITION = 0
        const val END_POSITION = 2
    }
}