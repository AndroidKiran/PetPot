package com.droid47.petgoogle.launcher.presentation.di

import androidx.lifecycle.ViewModel
import com.droid47.petgoogle.app.di.scopes.ViewModelKey
import com.droid47.petgoogle.launcher.data.repositories.AuthTokenRepo
import com.droid47.petgoogle.launcher.domain.repositories.AuthTokenRepository
import com.droid47.petgoogle.launcher.presentation.ui.viewmodels.LauncherViewModel
import com.droid47.petgoogle.search.data.repos.PetTypeRepo
import com.droid47.petgoogle.search.data.repos.SearchRepo
import com.droid47.petgoogle.search.domain.repositories.PetTypeRepository
import com.droid47.petgoogle.search.domain.repositories.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface AbstractLauncherModule {

    @Binds
    @IntoMap
    @ViewModelKey(LauncherViewModel::class)
    fun bindNavigationViewModel(launcherViewModel: LauncherViewModel): ViewModel

//    @FragmentScope
//    @ContributesAndroidInjector(modules = [AbstractSplashModule::class])
//    fun bindSplashFragment(): SplashFragment
//
//    @FragmentScope
//    @ContributesAndroidInjector(modules = [AbstractBoardingModule::class])
//    fun bindHomeBoardingFragment(): HomeBoardFragment
//
//    @FragmentScope
//    @ContributesAndroidInjector(modules = [AbstractTncModule::class])
//    fun bindTncFragment(): TnCFragment

    @Binds
    fun bindAutTokenRepository(authTokenRepo: AuthTokenRepo): AuthTokenRepository

    @Binds
    fun bindSearchRepository(searchRepository: SearchRepo): SearchRepository

    @Binds
    fun bindPetTypeRepository(petTypeRepo: PetTypeRepo): PetTypeRepository
}