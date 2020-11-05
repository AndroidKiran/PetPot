package com.droid47.petpot.petDetails.domain

import com.droid47.petpot.search.data.models.PetResponseEntity
import io.reactivex.Single

interface PetDetailsRepository {

    fun getPetDetails(id: Int): Single<PetResponseEntity>

}