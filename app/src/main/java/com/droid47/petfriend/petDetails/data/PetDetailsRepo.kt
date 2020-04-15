package com.droid47.petfriend.petDetails.data

import com.droid47.petfriend.petDetails.domain.PetDetailsRepository
import com.droid47.petfriend.search.data.datasource.SearchNetworkSource
import com.droid47.petfriend.search.data.models.PetResponseEntity
import io.reactivex.Single
import javax.inject.Inject

class PetDetailsRepo @Inject constructor(
    private val searchNetworkSource: SearchNetworkSource
) : PetDetailsRepository {

    override fun getPetDetails(id: Int): Single<PetResponseEntity> =
        searchNetworkSource.getPetDetails(id)
}