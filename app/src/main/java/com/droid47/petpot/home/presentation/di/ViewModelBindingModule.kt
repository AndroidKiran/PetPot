package com.droid47.petpot.home.presentation.di

import androidx.lifecycle.ViewModel
import com.droid47.petpot.aboutUs.presentation.viewmodel.AboutUsViewModel
import com.droid47.petpot.app.di.scopes.ViewModelKey
import com.droid47.petpot.home.presentation.viewmodels.HomeViewModel
import com.droid47.petpot.home.presentation.viewmodels.NavigationViewModel
import com.droid47.petpot.organization.presentation.viewmodel.OrganizationViewModel
import com.droid47.petpot.petDetails.presentation.viewmodels.PetDetailsViewModel
import com.droid47.petpot.search.presentation.viewmodel.*
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

    @Binds
    @IntoMap
    @ViewModelKey(OrganizationViewModel::class)
    fun bindOrganisationViewModel(organizationViewModel: OrganizationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    fun bindSettingsViewModel(settingsViewModel: SettingsViewModel): ViewModel
}