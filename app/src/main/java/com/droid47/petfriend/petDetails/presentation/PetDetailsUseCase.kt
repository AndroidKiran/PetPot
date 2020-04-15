package com.droid47.petfriend.petDetails.presentation

import androidx.lifecycle.LiveData
import com.droid47.petfriend.search.data.models.PetResponseEntity
import io.reactivex.Single

interface PetDetailsUseCase {

    fun fetchPetDetails(id: Int): Single<PetResponseEntity>

    fun isPetStarred(id: Int): LiveData<Boolean>
}