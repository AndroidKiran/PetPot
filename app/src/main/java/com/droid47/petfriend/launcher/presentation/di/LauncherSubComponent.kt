package com.droid47.petfriend.launcher.presentation.di

import com.droid47.petfriend.app.di.scopes.ActivityScope
import com.droid47.petfriend.launcher.presentation.ui.*
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
    fun inject(appIntroFragment: AppIntroFragment)

}