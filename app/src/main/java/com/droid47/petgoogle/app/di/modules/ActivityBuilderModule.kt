package com.droid47.petgoogle.app.di.modules

import com.droid47.petgoogle.app.di.scopes.ActivityScope
import com.droid47.petgoogle.home.presentation.HomeActivity
import com.droid47.petgoogle.home.presentation.di.AbstractHomeModule
import com.droid47.petgoogle.launcher.presentation.di.AbstractLauncherModule
import com.droid47.petgoogle.launcher.presentation.ui.LauncherActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [ViewModelModule::class])
interface ActivityBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [AbstractLauncherModule::class])
    fun contributeNavigationActivity(): LauncherActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [AbstractHomeModule::class])
    fun contributeNewHomeActivity(): HomeActivity
}
