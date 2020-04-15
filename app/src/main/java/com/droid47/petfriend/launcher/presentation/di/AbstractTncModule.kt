package com.droid47.petfriend.launcher.presentation.di

import androidx.lifecycle.ViewModel
import com.droid47.petfriend.app.di.scopes.ViewModelKey
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.TnCViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface AbstractTncModule {

    @Binds
    @IntoMap
    @ViewModelKey(TnCViewModel::class)
    abstract fun bindTnCViewModel(tnCViewModel: TnCViewModel): ViewModel
}