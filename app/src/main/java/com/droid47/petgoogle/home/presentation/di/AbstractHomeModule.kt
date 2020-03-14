package com.droid47.petgoogle.home.presentation.di

import androidx.lifecycle.ViewModel
import com.droid47.petgoogle.aboutUs.presentation.AboutUsFragment
import com.droid47.petgoogle.aboutUs.presentation.di.AbstractAboutUsModule
import com.droid47.petgoogle.app.di.scopes.FragmentScope
import com.droid47.petgoogle.app.di.scopes.ViewModelKey
import com.droid47.petgoogle.base.usecase.SynchronousUseCase
import com.droid47.petgoogle.bookmark.data.BookmarkRepo
import com.droid47.petgoogle.bookmark.domain.repositories.BookmarkRepository
import com.droid47.petgoogle.bookmark.presentation.BookmarkFragment
import com.droid47.petgoogle.bookmark.presentation.di.AbstractBookmarkModule
import com.droid47.petgoogle.home.domain.usecases.PlayServiceAvailabilityUseCase
import com.droid47.petgoogle.home.presentation.BottomNavDrawerFragment
import com.droid47.petgoogle.home.presentation.SettingFragment
import com.droid47.petgoogle.home.presentation.viewmodels.HomeViewModel
import com.droid47.petgoogle.home.presentation.viewmodels.NavigationViewModel
import com.droid47.petgoogle.petDetails.data.PetDetailsRepo
import com.droid47.petgoogle.petDetails.domain.PetDetailsRepository
import com.droid47.petgoogle.petDetails.presentation.PetDetailsFragment
import com.droid47.petgoogle.petDetails.presentation.di.AbstractPetDetailsModule
import com.droid47.petgoogle.search.data.repos.FilterRepo
import com.droid47.petgoogle.search.data.repos.PetTypeRepo
import com.droid47.petgoogle.search.data.repos.SearchRepo
import com.droid47.petgoogle.search.domain.repositories.FilterRepository
import com.droid47.petgoogle.search.domain.repositories.PetTypeRepository
import com.droid47.petgoogle.search.domain.repositories.SearchRepository
import com.droid47.petgoogle.search.presentation.SearchFragment
import com.droid47.petgoogle.search.presentation.di.AbstractSearchModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface AbstractHomeModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NavigationViewModel::class)
    fun bindNavigationViewModel(navigationViewModel: NavigationViewModel): ViewModel

    @FragmentScope
    @ContributesAndroidInjector
    fun bindBottomNavDrawerFragment(): BottomNavDrawerFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [AbstractSearchModule::class])
    fun bindSearchFragment(): SearchFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [AbstractAboutUsModule::class])
    fun bindAboutUsFragment(): AboutUsFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [AbstractPetDetailsModule::class])
    fun bindPetDetailsFragment(): PetDetailsFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [AbstractBookmarkModule::class])
    fun bindBookmarkFragment(): BookmarkFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun bindSettingFragment(): SettingFragment

    @Binds
    fun bindSearchRepository(searchRepo: SearchRepo): SearchRepository

    @Binds
    fun bindFilterRepository(filterRepo: FilterRepo): FilterRepository

    @Binds
    fun bindPetSpinnerRepository(petTypeRepo: PetTypeRepo): PetTypeRepository

    @Binds
    fun bindBookmarkRepository(bookmarkRepo: BookmarkRepo): BookmarkRepository

    @Binds
    fun bindPetDetailsRepository(petDetailsRepo: PetDetailsRepo): PetDetailsRepository
}