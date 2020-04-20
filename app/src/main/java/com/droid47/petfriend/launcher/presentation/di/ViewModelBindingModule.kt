package com.droid47.petfriend.launcher.presentation.di

import androidx.lifecycle.ViewModel
import com.droid47.petfriend.app.di.scopes.ViewModelKey
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.HomeBoardViewModel
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.LauncherViewModel
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.SplashViewModel
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.TnCViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelBindingModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeBoardViewModel::class)
    fun bindHomeBoardingViewModel(homeBoardViewModel: HomeBoardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LauncherViewModel::class)
    fun bindNavigationViewModel(launcherViewModel: LauncherViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    fun bindSplashViewModel(splashViewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TnCViewModel::class)
    fun bindTnCViewModel(tnCViewModel: TnCViewModel): ViewModel
}