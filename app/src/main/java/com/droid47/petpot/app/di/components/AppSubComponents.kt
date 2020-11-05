package com.droid47.petpot.app.di.components

import com.droid47.petpot.home.presentation.di.HomeSubComponent
import com.droid47.petpot.launcher.presentation.di.LauncherSubComponent
import com.droid47.petpot.workmanagers.di.WorkManagerSubComponent
import dagger.Module

@Module(subcomponents = [LauncherSubComponent::class, HomeSubComponent::class,
    AppServiceComponent::class, WorkManagerSubComponent::class])
class AppSubComponents