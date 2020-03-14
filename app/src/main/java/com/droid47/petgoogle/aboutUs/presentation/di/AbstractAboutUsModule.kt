package com.droid47.petgoogle.aboutUs.presentation.di

import androidx.lifecycle.ViewModel
import com.droid47.petgoogle.aboutUs.presentation.AboutUsFragment
import com.droid47.petgoogle.aboutUs.presentation.viewmodel.AboutUsViewModel
import com.droid47.petgoogle.app.di.scopes.FragmentScope
import com.droid47.petgoogle.app.di.scopes.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface AbstractAboutUsModule {

    @Binds
    @IntoMap
    @ViewModelKey(AboutUsViewModel::class)
    fun bindAboutUsViewModel(aboutUsViewModel: AboutUsViewModel): ViewModel

}