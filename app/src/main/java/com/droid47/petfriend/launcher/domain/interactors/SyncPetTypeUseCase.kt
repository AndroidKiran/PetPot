package com.droid47.petfriend.launcher.domain.interactors

import android.app.Application
import com.droid47.petfriend.base.usecase.SingleUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Empty
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.search.data.models.type.PetTypeEntity
import com.droid47.petfriend.search.domain.repositories.PetTypeRepository
import com.droid47.petfriend.search.domain.repositories.PetRepository
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function
import java.util.*
import javax.inject.Inject

class SyncPetTypeUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petRepository: PetRepository,
    private val petTypeRepository: PetTypeRepository,
    private val application: Application
) : SingleUseCase<BaseStateModel<List<PetTypeEntity>>, Boolean>(
    threadExecutor,
    postExecutionThread
) {

    override fun buildUseCaseSingle(params: Boolean): Single<BaseStateModel<List<PetTypeEntity>>> =
        when (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(application)) {
            ConnectionResult.SUCCESS -> lookForDbDataThenFetchFromNetwork(params)
            else -> Single.just(Failure(IllegalStateException(PLAY_SERVICE_ERROR)))
        }

    private fun lookForDbDataThenFetchFromNetwork(lookForCacheData: Boolean): Single<BaseStateModel<List<PetTypeEntity>>> =
        when {
            lookForCacheData -> getPetTypeListFromDb()
                .flatMap { stateModel ->
                    when (stateModel) {
                        is Failure, is Empty -> getPetTypesFromNetwork()
                        else -> Single.just(stateModel)
                    }
                }
            else -> getPetTypesFromNetwork()
        }


    private fun getPetTypeListFromDb(): Single<BaseStateModel<List<PetTypeEntity>>> =
        petTypeRepository.getPetTypeList()
            .map { petTypeList ->
                when {
                    petTypeList.isEmpty() -> Empty(petTypeList)
                    else -> Success(petTypeList)
                }
            }.onErrorReturn {
                Failure(it)
            }

    private fun getPetTypesFromNetwork(): Single<BaseStateModel<List<PetTypeEntity>>> {
        return petRepository.fetchPetTypesFromNetwork()
            .flattenAsObservable { typeResponse -> typeResponse.typeEntities }
            .onErrorResumeNext( Function { Observable.empty() })
            .fetchBreeds()
            .toList()
            .insertAnimalTypes()
    }

    private fun Observable<PetTypeEntity>.fetchBreeds() =
        flatMapSingle { petType ->
            petRepository.fetchBreedsFromNetwork(petType.name)
                .map { breedResponse ->
                    petType.apply {
                        breeds = breedResponse.breeds
                            ?.map { breedsItem ->
                                breedsItem.name?.toLowerCase(Locale.US) ?: ""
                            } ?: emptyList()
                    }
                }
        }


    private fun Single<List<PetTypeEntity>>.insertAnimalTypes() =
        flatMap { animalTypeList ->
            petRepository.insertPetTypeToDB(animalTypeList)
                .map { petTypeList ->
                    if (petTypeList.isEmpty()) {
                        Failure(IllegalStateException("Empty data"), petTypeList)
                    } else {
                        Success(petTypeList)
                    }
                }.onErrorReturn {
                    Failure(it)
                }
        }

    companion object {
        const val PLAY_SERVICE_ERROR = "Require play service"
    }
}