package com.droid47.petpot.search.data.repos

import com.droid47.petpot.search.data.datasource.PetTypeDao
import com.droid47.petpot.search.data.models.type.PetTypeEntity
import com.droid47.petpot.search.domain.repositories.PetTypeRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class PetTypeRepo @Inject constructor(
    private val petTypeDao: PetTypeDao
) : PetTypeRepository {

    override fun getPetNames(): Single<List<String>> = petTypeDao.getPetNames()

    override fun updateSelectedPetTypeRow(name: String): Completable =
        Completable.create { emitter ->
            try {
                petTypeDao.updateSelectedAnimalItem(name)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun getSelectedPetType(): Single<PetTypeEntity> = Single.create { emitter ->
        try {
            emitter.onSuccess(petTypeDao.getSelectedPet())
        } catch (exception: Exception) {
            emitter.onError(exception)
        }
    }

    override fun getPetTypeList(): Single<List<PetTypeEntity>> = petTypeDao.getPetTypeList()
}