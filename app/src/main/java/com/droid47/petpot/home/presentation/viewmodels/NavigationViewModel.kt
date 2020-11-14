package com.droid47.petpot.home.presentation.viewmodels

import android.app.Application
import com.droid47.petpot.app.di.scopes.ActivityScope
import com.droid47.petpot.app.di.scopes.FragmentScope
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.widgets.BaseAndroidViewModel
import javax.inject.Inject

class NavigationViewModel @Inject constructor(
    application: Application,
    private val firebaseManager: IFirebaseManager
) : BaseAndroidViewModel(application)