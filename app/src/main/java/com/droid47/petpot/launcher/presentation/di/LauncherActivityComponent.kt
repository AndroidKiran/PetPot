package com.droid47.petpot.launcher.presentation.di

import com.droid47.petpot.app.di.scopes.ActivityScope
import com.droid47.petpot.app.di.scopes.FragmentScope
import com.droid47.petpot.launcher.presentation.ui.HomeBoardFragment
import com.droid47.petpot.launcher.presentation.ui.LauncherActivity
import com.droid47.petpot.launcher.presentation.ui.SplashFragment
import com.droid47.petpot.launcher.presentation.ui.TnCFragment
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ViewModelBindingModule::class])
interface LauncherActivityComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): LauncherActivityComponent
    }

    @ActivityScope
    fun inject(activity: LauncherActivity)

    @FragmentScope
    fun inject(fragment: SplashFragment)

    @FragmentScope
    fun inject(fragment: HomeBoardFragment)

    @FragmentScope
    fun inject(fragment: TnCFragment)
}