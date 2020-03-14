package com.droid47.petgoogle.launcher.presentation.di

import androidx.lifecycle.ViewModel
import com.droid47.petgoogle.app.di.scopes.ChildFragmentScope
import com.droid47.petgoogle.app.di.scopes.ViewModelKey
import com.droid47.petgoogle.launcher.presentation.ui.IntroFragment
import com.droid47.petgoogle.launcher.presentation.ui.viewmodels.HomeBoardViewModel
import com.droid47.petgoogle.launcher.presentation.ui.viewmodels.TnCViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface AbstractTncModule {

    @Binds
    @IntoMap
    @ViewModelKey(TnCViewModel::class)
    abstract fun bindTnCViewModel(tnCViewModel: TnCViewModel): ViewModel
}