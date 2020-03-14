package com.droid47.petgoogle.search.domain.repositories

import com.droid47.petgoogle.search.data.models.type.PetTypeEntity
import io.reactivex.Completable
import io.reactivex.Single

interface PetTypeRepository {
    fun getPetNames(): Single<List<String>>

    fun updateSelectedPetTypeRow(name: String): Completable

    fun getSelectedPetType(): Single<PetTypeEntity>

    fun getPetTypeList(): Single<List<PetTypeEntity>>
}