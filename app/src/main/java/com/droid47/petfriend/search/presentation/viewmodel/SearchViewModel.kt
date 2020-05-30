package com.droid47.petfriend.search.presentation.viewmodel

import android.app.Application
import android.os.Parcelable
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droid47.petfriend.base.extensions.switchMap
import com.droid47.petfriend.base.extensions.toLiveData
import com.droid47.petfriend.base.extensions.toSingleLiveData
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.storage.LocalPreferencesRepository
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import com.droid47.petfriend.base.widgets.components.LiveEvent
import com.droid47.petfriend.base.widgets.components.RxStreamLiveEvent
import com.droid47.petfriend.search.data.models.FilterItemEntity
import com.droid47.petfriend.search.data.models.LOCATION
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.domain.interactors.*
import com.droid47.petfriend.search.presentation.models.SearchState
import com.droid47.petfriend.search.presentation.ui.widgets.PagedListPetAdapter
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

//private const val LIST_STATE = "list"

class SearchViewModel @Inject constructor(
    application: Application,
    private val updateFilterUseCase: UpdateFilterUseCase,
    private val removeAllPetsUseCase: RemoveAllPetsUseCase,
    private val fetchAppliedFilterUseCase: FetchAppliedFilterUseCase,
    private val petPaginationUseCase: PetPaginationUseCase
) : BaseAndroidViewModel(application), PagedListPetAdapter.OnItemClickListener {

    private val bookMarkSubject = PublishSubject.create<PetEntity>().toSerialized()

    private val _navigateToAnimalDetailsAction = LiveEvent<Pair<PetEntity, View>>()
    val navigateToAnimalDetailsAction: LiveEvent<Pair<PetEntity, View>>
        get() = _navigateToAnimalDetailsAction

    val eventLiveData = LiveEvent<Long>()
    val appliedFilterItemsLiveEvent = LiveEvent<List<FilterItemEntity>>()

    val searchStateLiveData: LiveData<SearchState> = petPaginationUseCase.searchStateLiveData
    val petsLiveData = petPaginationUseCase.buildPageListObservable().distinctUntilChanged().toSingleLiveData()

    override fun onBookMarkClick(petEntity: PetEntity) {
        bookMarkSubject.onNext(petEntity)
    }

    override fun onItemClick(petEntity: PetEntity, view: View) {
        _navigateToAnimalDetailsAction.postValue(Pair(petEntity, view))
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun retryPagination() {
        petPaginationUseCase.retry()
    }

    fun updateLocation(location: String) {
        fetchAppliedFilterUseCase.buildUseCaseSingle(true)
            .flatMapCompletable { filters ->
                if (!(filters.location ?: "").equals(location, true)) {
                    updateFilterUseCase.buildUseCaseCompletable(
                        FilterItemEntity(location, LOCATION, selected = true, filterApplied = true)
                    ).andThen(removeAllPetsUseCase.buildUseCaseCompletable(false))
                } else {
                    Completable.complete()
                }
            }.subscribe(object : CompletableObserver {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    registerDisposableRequest(REQUEST_UPDATE_FILTER, d)
                }

                override fun onError(e: Throwable) {
                    CrashlyticsExt.handleException(e)
                }
            })
    }

    fun cancelPagination() {
        petPaginationUseCase.cancelPagination()
    }

    companion object {
        private const val REQUEST_UPDATE_FILTER = 1003L
    }
}