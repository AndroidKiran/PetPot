package com.droid47.petpot.app.di.components

import com.droid47.petpot.home.presentation.di.HomeActivityComponent
import com.droid47.petpot.launcher.presentation.di.LauncherActivityComponent
import com.droid47.petpot.workmanagers.di.WorkManagerSubComponent
import dagger.Module

@Module(
    subcomponents = [LauncherActivityComponent::class, HomeActivityComponent::class,
        AppServiceComponent::class, WorkManagerSubComponent::class]
)
class AppSubComponents