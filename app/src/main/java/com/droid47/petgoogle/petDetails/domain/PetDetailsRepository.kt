package com.droid47.petgoogle.petDetails.domain

import com.droid47.petgoogle.search.data.models.PetResponseEntity
import io.reactivex.Single

interface PetDetailsRepository {

    fun getPetDetails(id: Int): Single<PetResponseEntity>

}