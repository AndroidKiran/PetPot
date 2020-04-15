package com.droid47.petfriend.petDetails.domain

import com.droid47.petfriend.search.data.models.PetResponseEntity
import io.reactivex.Single

interface PetDetailsRepository {

    fun getPetDetails(id: Int): Single<PetResponseEntity>

}