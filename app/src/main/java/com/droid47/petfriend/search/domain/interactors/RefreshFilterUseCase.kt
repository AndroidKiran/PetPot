package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.usecase.CompletableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.search.data.models.*
import com.droid47.petfriend.search.domain.repositories.FilterRepository
import com.droid47.petfriend.search.domain.repositories.PetTypeRepository
import io.reactivex.Completable
import javax.inject.Inject

class RefreshFilterUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petTypeRepository: PetTypeRepository,
    private val filterRepository: FilterRepository
) : CompletableUseCase<String>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: String?): Completable =
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
                    add(FilterItemEntity(petType.name, PET_TYPE, true))
                    add(FilterItemEntity(1.toString(), PAGE_NUM, true))
                    add(FilterItemEntity("recent", SORT, true))
                    val locationStr = params ?: ""
                    if (locationStr.isNotEmpty()) {
                        add(FilterItemEntity(locationStr, LOCATION, true))
                    }
                })
            }

    private fun transformToFilterItemList(
        strList: List<String>,
        type: String
    ): List<FilterItemEntity> {
        val filterItemList = mutableListOf<FilterItemEntity>()
        strList.forEach { filterName ->
            val filterItem = FilterItemEntity(
                name = filterName,
                type = type,
                selected = false
            )
            filterItemList.add(filterItem)
        }
        return filterItemList
    }
}