package com.droid47.petpot.search.domain.interactors

import com.droid47.petpot.base.usecase.SingleUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.search.data.models.*
import com.droid47.petpot.search.domain.repositories.FilterRepository
import com.droid47.petpot.search.presentation.models.PetFilterConstants
import com.droid47.petpot.search.presentation.models.PetFilters
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class FetchAppliedFilterUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val filterRepository: FilterRepository
) : SingleUseCase<PetFilters, Boolean>(
    threadExecutor,
    postExecutionThread
) {

    override fun buildUseCaseSingle(params: Boolean): Single<PetFilters> =
        filterRepository.getAppliedFilterItemForSearch()
            .map {
                composeFilter(it, params)
            }


    private fun composeFilter(
        selectedEntityPets: List<PetFilterCheckableEntity>,
        isFirstPage: Boolean
    ): PetFilters =
        PetFilters().apply {
            val ageStr = transformListToString(selectedEntityPets, AGE)
            if (ageStr.isNotEmpty()) {
                age = ageStr
            }

            val genderStr = transformListToString(selectedEntityPets, GENDER)
            if (genderStr.isNotEmpty()) {
                gender = genderStr
            }

            val colorStr = transformListToString(selectedEntityPets, COLOR)
            if (colorStr.isNotEmpty()) {
                color = colorStr
            }

            val coatStr = transformListToString(selectedEntityPets, COAT)
            if (coatStr.isNotEmpty()) {
                coat = coatStr
            }

            val sizeStr = transformListToString(selectedEntityPets, SIZE)
            if (sizeStr.isNotEmpty()) {
                size = sizeStr
            }

            val statusStr = transformListToString(selectedEntityPets, STATUS)
            if (statusStr.isNotEmpty()) {
                status = statusStr
            }

            val breedStr = transformListToString(selectedEntityPets, BREED)
            if (breedStr.isNotEmpty()) {
                breed = breedStr
            }

            val typeStr = transformListToString(selectedEntityPets, PET_TYPE).split(",")[0]
                .replace("_", ",")
            if (typeStr.isNotEmpty()) {
                type = typeStr
            }

            if (isFirstPage) {
                page = PetFilterConstants.PAGE_ONE.toString()
            } else {
                val pageStr = transformListToString(selectedEntityPets, PAGE_NUM).split(",")[0]
                if (pageStr.isNotEmpty()) {
                    page = pageStr
                }
            }

            val sortStr = transformListToString(selectedEntityPets, SORT).split(",")[0]
            if (sortStr.isNotEmpty()) {
                sort = sortStr
            }

            val locationStr = transformListToString(selectedEntityPets, LOCATION).split(",")[0]
            if (locationStr.isNotEmpty()) {
                location = locationStr
            }
        }


    private fun transformListToString(
        petFilterEntityList: List<PetFilterCheckableEntity>,
        type: String
    ): String =
        petFilterEntityList.filter { filterItem -> filterItem.type == type }
            .map { filterItem ->
                val name = filterItem.name?.toLowerCase(Locale.US) ?: ""
                if (name.contains("scale")) {
                    name.replace(",", "_")
                } else {
                    name
                }
            }
            .toList()
            .toString()
            .replace(", ", ",")
            .replace("[", "")
            .replace("]", "")
}