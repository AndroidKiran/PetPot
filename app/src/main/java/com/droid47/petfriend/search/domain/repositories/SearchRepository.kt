package com.droid47.petfriend.search.domain.repositories

import com.droid47.petfriend.search.data.models.search.SearchResponseEntity
import com.droid47.petfriend.search.data.models.type.BreedResponseEntity
import com.droid47.petfriend.search.data.models.type.PetTypeEntity
import com.droid47.petfriend.search.data.models.type.PetTypeResponseEntity
import io.reactivex.Single

interface SearchRepository {

    fun getSearchResponse(options: Map<String, @JvmSuppressWildcards Any>): Single<SearchResponseEntity>

    fun getTypesFromNetwork(): Single<PetTypeResponseEntity>

    fun getBreedsFromNetwork(animalType: String): Single<BreedResponseEntity>

    fun insertAnimalType(animalTypeEntities: List<PetTypeEntity>): Single<List<PetTypeEntity>>
}