package com.droid47.petgoogle.search.data.repos

import com.droid47.petgoogle.search.data.datasource.PetTypeDao
import com.droid47.petgoogle.search.data.datasource.SearchNetworkSource
import com.droid47.petgoogle.search.data.models.search.SearchResponseEntity
import com.droid47.petgoogle.search.data.models.type.BreedResponseEntity
import com.droid47.petgoogle.search.data.models.type.PetTypeEntity
import com.droid47.petgoogle.search.data.models.type.PetTypeResponseEntity
import com.droid47.petgoogle.search.domain.repositories.SearchRepository
import io.reactivex.Single
import javax.inject.Inject

class SearchRepo @Inject constructor(
    private val searchNetworkSource: SearchNetworkSource,
    private val petTypeDao: PetTypeDao
) : SearchRepository {

    override fun insertAnimalType(animalTypeEntities: List<PetTypeEntity>): Single<List<PetTypeEntity>> {
        return petTypeDao.insertPetTypeList(animalTypeEntities)
            .map { animalTypeEntities }
    }

    override fun getSearchResponse(options: Map<String, @JvmSuppressWildcards Any>): Single<SearchResponseEntity> {
        return searchNetworkSource.getPets(options)
    }

    override fun getTypesFromNetwork(): Single<PetTypeResponseEntity> {
        return searchNetworkSource.getPetTypes()
    }

    override fun getBreedsFromNetwork(animalType: String): Single<BreedResponseEntity> {
        return searchNetworkSource.getBreeds(animalType)
    }
}