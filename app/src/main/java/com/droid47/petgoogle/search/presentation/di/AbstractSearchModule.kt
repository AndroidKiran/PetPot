package com.droid47.petgoogle.search.presentation.di

import androidx.lifecycle.ViewModel
import com.droid47.petgoogle.app.di.scopes.ViewModelKey
import com.droid47.petgoogle.search.presentation.viewmodel.FilterViewModel
import com.droid47.petgoogle.search.presentation.viewmodel.PetSpinnerAndLocationViewModel
import com.droid47.petgoogle.search.presentation.viewmodel.SearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface AbstractSearchModule {

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