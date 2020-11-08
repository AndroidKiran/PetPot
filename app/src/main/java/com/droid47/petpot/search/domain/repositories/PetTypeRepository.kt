package com.droid47.petpot.search.domain.repositories

import com.droid47.petpot.search.data.models.type.BreedResponseEntity
import com.droid47.petpot.search.data.models.type.PetTypeEntity
import com.droid47.petpot.search.data.models.type.PetTypeResponseEntity
import io.reactivex.Completable
import io.reactivex.Single


interface PetTypeRepository {

    fun fetchPetTypesFromNetwork(): Single<PetTypeResponseEntity>

    fun fetchBreedsFromNetwork(animalType: String): Single<BreedResponseEntity>

    fun insertPetTypeToDB(petTypes: List<PetTypeEntity>): Single<List<PetTypeEntity>>

    fun getPetNames(): Single<List<String>>

    fun updateSelectedPetTypeRow(name: String): Completable

    fun getSelectedPetType(): Single<PetTypeEntity>

    fun getPetTypeList(): Single<List<PetTypeEntity>>
}