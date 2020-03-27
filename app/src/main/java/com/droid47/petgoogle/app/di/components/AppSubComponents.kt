package com.droid47.petgoogle.app.di.components

import com.droid47.petgoogle.home.presentation.di.HomeSubComponent
import com.droid47.petgoogle.launcher.presentation.di.LauncherSubComponent
import dagger.Module

@Module(subcomponents = [LauncherSubComponent::class, HomeSubComponent::class, AppServiceComponent::class])
class AppSubComponents