package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.usecase.SingleUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.search.data.models.search.SearchResponseEntity
import com.droid47.petfriend.search.domain.repositories.SearchRepository
import com.droid47.petfriend.search.presentation.models.Filters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import javax.inject.Inject

class SearchPetUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val gson: Gson,
    private val searchRepository: SearchRepository
) : SingleUseCase<SearchResponseEntity, Filters>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: Filters?): Single<SearchResponseEntity> {
        return searchRepository.getSearchResponse(transformToMap(params))
    }

    private fun transformToMap(filters: Filters?): Map<String, @JvmSuppressWildcards Any> =
        try {
            when (filters) {
                null -> mapOf()
                else -> {
                    val jsonStr = gson.toJson(filters)
                    val mapType = object : TypeToken<Map<String, Any>>() {}.type
                    gson.fromJson(jsonStr, mapType)
                }
            }
        } catch (exception: Exception) {
            CrashlyticsExt.logHandledException(exception)
            mapOf()
        }

}