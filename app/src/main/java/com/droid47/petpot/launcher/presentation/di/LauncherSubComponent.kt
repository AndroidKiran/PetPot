package com.droid47.petpot.launcher.presentation.di

import com.droid47.petpot.app.di.scopes.ActivityScope
import com.droid47.petpot.launcher.presentation.ui.HomeBoardFragment
import com.droid47.petpot.launcher.presentation.ui.LauncherActivity
import com.droid47.petpot.launcher.presentation.ui.SplashFragment
import com.droid47.petpot.launcher.presentation.ui.TnCFragment
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ViewModelBindingModule::class])
interface LauncherSubComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): LauncherSubComponent
    }

    fun inject(activity: LauncherActivity)
    fun inject(fragment: SplashFragment)
    fun inject(fragment: HomeBoardFragment)
    fun inject(fragment: TnCFragment)

}