package com.droid47.petfriend.launcher.domain.interactors

import android.app.Application
import com.droid47.petfriend.base.extensions.LoggerExt
import com.droid47.petfriend.base.usecase.SingleUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Empty
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.launcher.data.entities.ClientCredentialModel
import com.droid47.petfriend.launcher.domain.repositories.AuthTokenRepository
import com.droid47.petfriend.search.data.models.type.PetTypeEntity
import com.droid47.petfriend.search.domain.repositories.PetTypeRepository
import com.droid47.petfriend.search.domain.repositories.SearchRepository
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function
import java.util.*
import javax.inject.Inject

class RefreshAuthTokenAndPetTypeUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val authTokenRepository: AuthTokenRepository,
    private val searchRepository: SearchRepository,
    private val petTypeRepository: PetTypeRepository,
    private val application: Application
) : SingleUseCase<BaseStateModel<List<PetTypeEntity>>, Unit>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: Unit?): Single<BaseStateModel<List<PetTypeEntity>>> =
        when (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(application)) {
            ConnectionResult.SUCCESS -> performAuthenticationWithPullData()
            else -> Single.just(Failure(IllegalStateException(PLAY_SERVICE_ERROR)))
        }

    private fun performAuthenticationWithPullData() =
        authTokenRepository.getAuthToken(ClientCredentialModel())
            .flatMap { tokenModel ->
                authTokenRepository.cacheToken(tokenModel)
                return@flatMap lookForDbDataThenFetchFromNetwork()
            }.onErrorReturn {
                Failure(it)
            }

    private fun lookForDbDataThenFetchFromNetwork(): Single<BaseStateModel<List<PetTypeEntity>>> {
        return getPetTypeListFromDb()
            .flatMap { stateModel ->
                when (stateModel) {
                    is Failure, is Empty -> getPetTypesFromNetwork()
                    else -> Single.just(stateModel)
                }
            }
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
        return searchRepository.getTypesFromNetwork()
            .flattenAsObservable { typeResponse -> typeResponse.typeEntities }
            .onErrorResumeNext(Function { Observable.empty() })
            .fetchBreeds()
            .toList()
            .insertAnimalTypes()
    }

    private fun Observable<PetTypeEntity>.fetchBreeds() =
        flatMapSingle { petType ->
            searchRepository.getBreedsFromNetwork(petType.name)
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
            searchRepository.insertAnimalType(animalTypeList)
                .map { petTypeList ->
                    LoggerExt.d("SyncExcuted====", "SUCCESS")
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