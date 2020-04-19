package com.droid47.petfriend.app.di.components

import com.droid47.petfriend.home.presentation.di.HomeSubComponent
import com.droid47.petfriend.launcher.presentation.di.LauncherSubComponent
import com.droid47.petfriend.workmanagers.di.WorkManagerSubComponent
import dagger.Module

@Module(subcomponents = [LauncherSubComponent::class, HomeSubComponent::class,
    AppServiceComponent::class, WorkManagerSubComponent::class])
class AppSubComponents