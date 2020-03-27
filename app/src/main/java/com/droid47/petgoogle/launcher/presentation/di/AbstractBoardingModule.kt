package com.droid47.petgoogle.launcher.presentation.di

import androidx.lifecycle.ViewModel
import com.droid47.petgoogle.app.di.scopes.ViewModelKey
import com.droid47.petgoogle.launcher.presentation.ui.viewmodels.HomeBoardViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface AbstractBoardingModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeBoardViewModel::class)
    fun bindHomeBoardingViewModel(homeBoardViewModel: HomeBoardViewModel): ViewModel

//    @ChildFragmentScope
//    @ContributesAndroidInjector
//    fun bindIntroFragment(): IntroFragment
}