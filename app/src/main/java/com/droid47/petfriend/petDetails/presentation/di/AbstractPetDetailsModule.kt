package com.droid47.petfriend.petDetails.presentation.di

import androidx.lifecycle.ViewModel
import com.droid47.petfriend.app.di.scopes.ViewModelKey
import com.droid47.petfriend.petDetails.presentation.viewmodels.PetDetailsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface AbstractPetDetailsModule {

    @Binds
    @IntoMap
    @ViewModelKey(PetDetailsViewModel::class)
    fun bindPetDetailsViewModel(petDetailsViewModel: PetDetailsViewModel): ViewModel
}