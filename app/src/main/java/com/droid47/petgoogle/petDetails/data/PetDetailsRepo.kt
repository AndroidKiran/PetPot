package com.droid47.petgoogle.petDetails.data

import com.droid47.petgoogle.petDetails.domain.PetDetailsRepository
import com.droid47.petgoogle.search.data.datasource.SearchNetworkSource
import com.droid47.petgoogle.search.data.models.PetResponseEntity
import io.reactivex.Single
import javax.inject.Inject

class PetDetailsRepo @Inject constructor(
    private val searchNetworkSource: SearchNetworkSource
) : PetDetailsRepository {

    override fun getPetDetails(id: Int): Single<PetResponseEntity> = searchNetworkSource.getPetDetails(id)
}