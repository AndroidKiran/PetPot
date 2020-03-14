package com.droid47.petgoogle.search.domain.interactors

import com.droid47.petgoogle.base.usecase.FlowableUseCase
import com.droid47.petgoogle.base.usecase.executor.PostExecutionThread
import com.droid47.petgoogle.base.usecase.executor.ThreadExecutor
import com.droid47.petgoogle.base.widgets.BaseStateModel
import com.droid47.petgoogle.base.widgets.Empty
import com.droid47.petgoogle.base.widgets.Failure
import com.droid47.petgoogle.base.widgets.Success
import com.droid47.petgoogle.search.data.models.*
import com.droid47.petgoogle.search.domain.repositories.FilterRepository
import com.droid47.petgoogle.search.presentation.models.Filters
import io.reactivex.Flowable
import java.util.*
import javax.inject.Inject

class FetchAppliedFilterUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val filterRepository: FilterRepository
) : FlowableUseCase<BaseStateModel<Filters>, Unit>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseObservable(params: Unit?): Flowable<BaseStateModel<Filters>> =
        filterRepository.fetchPageFilterOnUpdate(PAGE_NUM)
            .switchMapSingle {
                fetchAppliedFilters()
            }.onErrorReturn {
                Failure(it)
            }

    private fun fetchAppliedFilters() =
        filterRepository.getAppliedFilterItemForSearch()
            .map { filterItemList ->
                if (filterItemList.size == 1 || filterItemList.isEmpty()) {
                    return@map Empty<Filters>()
                } else {
                    return@map Success(composeFilter(filterItemList))
                }
            }.onErrorReturn {
                Failure(it)
            }

    private fun composeFilter(selectedItemEntities: List<FilterItemEntity>): Filters =
        Filters().apply {
            val ageStr = transformListToString(selectedItemEntities, AGE)
            if (ageStr.isNotEmpty()) {
                age = ageStr
            }

            val genderStr = transformListToString(selectedItemEntities, GENDER)
            if (genderStr.isNotEmpty()) {
                gender = genderStr
            }

            val colorStr = transformListToString(selectedItemEntities, COLOR)
            if (colorStr.isNotEmpty()) {
                color = colorStr
            }

            val coatStr = transformListToString(selectedItemEntities, COAT)
            if (coatStr.isNotEmpty()) {
                coat = coatStr
            }

            val sizeStr = transformListToString(selectedItemEntities, SIZE)
            if (sizeStr.isNotEmpty()) {
                size = sizeStr
            }

            val statusStr = transformListToString(selectedItemEntities, STATUS)
            if (statusStr.isNotEmpty()) {
                status = statusStr
            }

            val breedStr = transformListToString(selectedItemEntities, BREED)
            if (breedStr.isNotEmpty()) {
                breed = breedStr
            }

            val typeStr = transformListToString(selectedItemEntities, PET_TYPE).split(",")[0]
                .replace("_", ",")
            if (typeStr.isNotEmpty()) {
                type = typeStr
            }

            val pageStr = transformListToString(selectedItemEntities, PAGE_NUM).split(",")[0]
            if (pageStr.isNotEmpty()) {
                page = pageStr
            }

            val sortStr = transformListToString(selectedItemEntities, SORT).split(",")[0]
            if (sortStr.isNotEmpty()) {
                sort = sortStr
            }

            val locationStr = transformListToString(selectedItemEntities, LOCATION).split(",")[0]
            if (locationStr.isNotEmpty()) {
                location = locationStr
            }

            val limit = filterRepository.getSearchLimit()
            this.limit = limit.toString()
        }

    private fun transformListToString(filterItemEntityList: List<FilterItemEntity>, type: String): String =
        filterItemEntityList.filter { filterItem -> filterItem.type == type }
            .map { filterItem ->
                val name = filterItem.name
                if (name.toLowerCase(Locale.US).contains("scale")) {
                    name.replace(",", "_")
                } else {
                    name
                }
            }
            .toList()
            .toString()
            .replace("[", "")
            .replace("]", "")


}