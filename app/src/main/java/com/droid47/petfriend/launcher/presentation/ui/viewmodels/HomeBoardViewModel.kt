package com.droid47.petfriend.launcher.presentation.ui.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.droid47.petfriend.R
import com.droid47.petfriend.app.domain.repositories.LocalPreferencesRepository
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import javax.inject.Inject

class HomeBoardViewModel @Inject constructor(
    application: Application,
    val localPreferencesRepository: LocalPreferencesRepository
) : BaseAndroidViewModel(application) {

    val positionLiveData = MutableLiveData<Int>()

//    val titleLiveData: LiveData<String> = Transformations.map(positionLiveData) { position ->
//        introTitleArray[position ?: 2]
//    }
//
//    val descLiveData: LiveData<String> = Transformations.map(positionLiveData) { position ->
//        introDescArray[position ?: 1]
//    }


    companion object {
        const val START_POSITION = 0
        const val END_POSITION = 2
    }
}