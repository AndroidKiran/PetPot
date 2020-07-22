package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.usecase.SingleUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.search.data.models.PAGE_NUM
import com.droid47.petfriend.search.data.models.PetFilterCheckableEntity
import com.droid47.petfriend.search.data.models.search.SearchResponseEntity
import com.droid47.petfriend.search.domain.repositories.FilterRepository
import com.droid47.petfriend.search.domain.repositories.PetRepository
import com.droid47.petfriend.search.presentation.models.PetFilters
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class FetchPetFromNetworkAndCacheDbUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val gson: Gson,
    private val petRepository: PetRepository,
    private val filterRepository: FilterRepository
) : SingleUseCase<SearchResponseEntity, PetFilters>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: PetFilters): Single<SearchResponseEntity> {
        return petRepository.fetchPetsFromNetWork(params.transformToMap(gson))
            .addPetsToDbAndUpdatePage()
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
    }

    private fun Single<SearchResponseEntity>.addPetsToDbAndUpdatePage(): Single<SearchResponseEntity> =
        flatMap { searchResponseEntity ->
            val currentPage = searchResponseEntity.paginationEntity?.currentPage?.plus(1)
            Single.zip(petRepository.addPets(
                searchResponseEntity.animals ?: emptyList()
            ), filterRepository.updateOrInsertTheFilter(
                PetFilterCheckableEntity(
                    currentPage.toString(),
                    PAGE_NUM,
                    selected = true,
                    filterApplied = true
                )
            ).toSingleDefault(true),
                BiFunction { listOfLong: List<Long>, isCompleted: Boolean ->
                    listOfLong
                }).subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler)
                .map { searchResponseEntity }
        }

//    private fun transformToMap(petFilters: PetFilters): Map<String, @JvmSuppressWildcards Any> =
//        try {
//            val jsonStr = gson.toJson(petFilters)
//            val mapType = object : TypeToken<Map<String, Any>>() {}.type
//            gson.fromJson(jsonStr, mapType)
//        } catch (exception: Exception) {
//            CrashlyticsExt.logHandledException(exception)
//            mapOf()
//        }
}