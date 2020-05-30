package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.usecase.CompletableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.search.data.models.*
import com.droid47.petfriend.search.domain.repositories.FilterRepository
import com.droid47.petfriend.search.domain.repositories.PetTypeRepository
import com.droid47.petfriend.search.presentation.models.FilterConstants.PAGE_ONE
import com.droid47.petfriend.search.presentation.models.FilterConstants.SORT_BY_RECENT
import io.reactivex.Completable
import javax.inject.Inject

class RefreshFilterUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petTypeRepository: PetTypeRepository,
    private val filterRepository: FilterRepository
) : CompletableUseCase<String>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: String): Completable =
        petTypeRepository.getSelectedPetType()
            .flatMapCompletable { petType ->
                filterRepository.refreshFilter(mutableListOf<FilterItemEntity>().apply {
                    addAll(transformToFilterItemList(petType.breeds ?: emptyList(), BREED))
                    addAll(transformToFilterItemList(petType.colors ?: emptyList(), COLOR))
                    addAll(transformToFilterItemList(petType.coats ?: emptyList(), COAT))
                    addAll(transformToFilterItemList(petType.genders ?: emptyList(), GENDER))
                    addAll(transformToFilterItemList(petType.size, SIZE))
                    addAll(transformToFilterItemList(petType.age, AGE))
                    addAll(transformToFilterItemList(petType.status, STATUS))
                    add(FilterItemEntity(petType.name, PET_TYPE,
                        selected = true,
                        filterApplied = true
                    ))
                    add(FilterItemEntity(PAGE_ONE.toString(), PAGE_NUM,
                        selected = true,
                        filterApplied = true
                    ))
                    add(FilterItemEntity(SORT_BY_RECENT, SORT,
                        selected = true,
                        filterApplied = true
                    ))
                    if (params.isNotEmpty()) {
                        add(FilterItemEntity(params, LOCATION,
                            selected = true,
                            filterApplied = true
                        ))
                    }
                })
            }.subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)

    private fun transformToFilterItemList(
        strList: List<String>,
        type: String
    ): List<FilterItemEntity> {
        val filterItemList = mutableListOf<FilterItemEntity>()
        strList.forEach { filterName ->
            val filterItem = FilterItemEntity(
                name = filterName,
                type = type,
                selected = false,
                filterApplied = false
            )
            filterItemList.add(filterItem)
        }
        return filterItemList
    }
}