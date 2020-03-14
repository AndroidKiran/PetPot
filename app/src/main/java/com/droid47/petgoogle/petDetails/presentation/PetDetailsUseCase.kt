package com.droid47.petgoogle.petDetails.presentation

import androidx.lifecycle.LiveData
import com.droid47.petgoogle.search.data.models.PetResponseEntity
import io.reactivex.Single

interface PetDetailsUseCase {

    fun fetchPetDetails(id: Int): Single<PetResponseEntity>

    fun isPetStarred(id: Int): LiveData<Boolean>
}