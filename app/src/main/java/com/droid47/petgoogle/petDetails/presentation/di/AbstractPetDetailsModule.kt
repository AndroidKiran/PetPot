package com.droid47.petgoogle.petDetails.presentation.di

import androidx.lifecycle.ViewModel
import com.droid47.petgoogle.app.di.scopes.ChildFragmentScope
import com.droid47.petgoogle.app.di.scopes.ViewModelKey
import com.droid47.petgoogle.petDetails.presentation.SimilarPetsFragment
import com.droid47.petgoogle.petDetails.presentation.viewmodels.PetDetailsViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface AbstractPetDetailsModule {

    @Binds
    @IntoMap
    @ViewModelKey(PetDetailsViewModel::class)
    fun bindPetDetailsViewModel(petDetailsViewModel: PetDetailsViewModel): ViewModel


    @ChildFragmentScope
    @ContributesAndroidInjector
    fun bindSimilarPetsFragment(): SimilarPetsFragment
}