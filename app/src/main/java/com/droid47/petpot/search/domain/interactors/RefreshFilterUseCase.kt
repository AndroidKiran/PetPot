package com.droid47.petpot.search.domain.interactors

import com.droid47.petpot.base.usecase.CompletableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.search.data.models.*
import com.droid47.petpot.search.domain.repositories.FilterRepository
import com.droid47.petpot.search.domain.repositories.PetTypeRepository
import com.droid47.petpot.search.presentation.models.PetFilterConstants.PAGE_ONE
import com.droid47.petpot.search.presentation.models.PetFilterConstants.SORT_BY_DISTANCE
import com.droid47.petpot.search.presentation.models.PetFilterConstants.SORT_BY_RECENT
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
                filterRepository.refreshFilter(mutableListOf<PetFilterCheckableEntity>().apply {
                    addAll(transformToFilterItemList(petType.breeds ?: emptyList(), BREED))
                    addAll(transformToFilterItemList(petType.colors ?: emptyList(), COLOR))
                    addAll(transformToFilterItemList(petType.coats ?: emptyList(), COAT))
                    addAll(transformToFilterItemList(petType.genders ?: emptyList(), GENDER))
                    addAll(transformToFilterItemList(petType.size, SIZE))
                    addAll(transformToFilterItemList(petType.age, AGE))
                    addAll(transformToFilterItemList(petType.status, STATUS))
                    add(
                        PetFilterCheckableEntity(
                            petType.name, PET_TYPE,
                            selected = true,
                            filterApplied = true
                        )
                    )
                    add(
                        PetFilterCheckableEntity(
                            PAGE_ONE.toString(), PAGE_NUM,
                            selected = true,
                            filterApplied = true
                        )
                    )

                    if (params.isNotEmpty()) {
                        add(
                            PetFilterCheckableEntity(
                                SORT_BY_DISTANCE, SORT,
                                selected = true,
                                filterApplied = true
                            )
                        )

                        add(
                            PetFilterCheckableEntity(
                                params, LOCATION,
                                selected = true,
                                filterApplied = true
                            )
                        )
                    } else {
                        add(
                            PetFilterCheckableEntity(
                                SORT_BY_RECENT, SORT,
                                selected = true,
                                filterApplied = true
                            )
                        )
                    }
                })
            }

    private fun transformToFilterItemList(
        strList: List<String>,
        type: String
    ): List<PetFilterCheckableEntity> {
        val filterItemList = mutableListOf<PetFilterCheckableEntity>()
        strList.forEach { filterName ->
            val filterItem = PetFilterCheckableEntity(
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