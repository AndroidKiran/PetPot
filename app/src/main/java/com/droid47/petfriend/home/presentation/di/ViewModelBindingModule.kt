package com.droid47.petfriend.home.presentation.di

import androidx.lifecycle.ViewModel
import com.droid47.petfriend.aboutUs.presentation.viewmodel.AboutUsViewModel
import com.droid47.petfriend.app.di.scopes.ViewModelKey
import com.droid47.petfriend.search.presentation.viewmodel.BookmarkViewModel
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel
import com.droid47.petfriend.home.presentation.viewmodels.NavigationViewModel
import com.droid47.petfriend.petDetails.presentation.viewmodels.PetDetailsViewModel
import com.droid47.petfriend.search.presentation.viewmodel.FilterViewModel
import com.droid47.petfriend.search.presentation.viewmodel.PetSpinnerAndLocationViewModel
import com.droid47.petfriend.search.presentation.viewmodel.SearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelBindingModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NavigationViewModel::class)
    fun bindNavigationViewModel(navigationViewModel: NavigationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AboutUsViewModel::class)
    fun bindAboutUsViewModel(aboutUsViewModel: AboutUsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BookmarkViewModel::class)
    fun bindBookmarkViewModel(bookmarkViewModel: BookmarkViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PetDetailsViewModel::class)
    fun bindPetDetailsViewModel(petDetailsViewModel: PetDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PetSpinnerAndLocationViewModel::class)
    fun bindPetSpinnerViewModel(petSpinnerAndLocationViewModel: PetSpinnerAndLocationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FilterViewModel::class)
    fun bindFilterViewModel(filterViewModel: FilterViewModel): ViewModel
}