package com.droid47.petpot.search.presentation.viewmodel

import android.app.Application
import android.os.Parcelable
import android.view.View
import androidx.lifecycle.LiveData
import com.droid47.petpot.app.di.scopes.ActivityScope
import com.droid47.petpot.app.di.scopes.FragmentScope
import com.droid47.petpot.base.extensions.toLiveData
import com.droid47.petpot.base.extensions.toSingleLiveData
import com.droid47.petpot.base.firebase.AnalyticsAction
import com.droid47.petpot.base.firebase.CrashlyticsExt
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.widgets.BaseAndroidViewModel
import com.droid47.petpot.base.widgets.components.LiveEvent
import com.droid47.petpot.search.data.models.LOCATION
import com.droid47.petpot.search.data.models.PetFilterCheckableEntity
import com.droid47.petpot.search.data.models.search.PetEntity
import com.droid47.petpot.search.domain.interactors.FetchAppliedFilterUseCase
import com.droid47.petpot.search.domain.interactors.PetPaginationUseCase
import com.droid47.petpot.search.domain.interactors.RemoveAllPetsUseCase
import com.droid47.petpot.search.domain.interactors.UpdateFilterUseCase
import com.droid47.petpot.search.presentation.models.ItemPaginationState
import com.droid47.petpot.search.presentation.ui.widgets.PagedListPetAdapter
import com.droid47.petpot.search.presentation.viewmodel.tracking.TrackSearchViewModel
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

//private const val LIST_STATE = "list"

class SearchViewModel @Inject constructor(
    application: Application,
    private val updateFilterUseCase: UpdateFilterUseCase,
    private val removeAllPetsUseCase: RemoveAllPetsUseCase,
    private val fetchAppliedFilterUseCase: FetchAppliedFilterUseCase,
    private val petPaginationUseCase: PetPaginationUseCase,
    val firebaseManager: IFirebaseManager
) : BaseAndroidViewModel(application), PagedListPetAdapter.OnItemClickListener,
    TrackSearchViewModel {

    private val bookMarkSubject = PublishSubject.create<PetEntity>().toSerialized()

    private val _navigateToAnimalDetailsAction = LiveEvent<Pair<PetEntity, View>>()
    val navigateToAnimalDetailsAction: LiveEvent<Pair<PetEntity, View>>
        get() = _navigateToAnimalDetailsAction

    val eventLiveData = LiveEvent<Long>()
    val appliedFilterItemsLiveEvent = LiveEvent<List<PetFilterCheckableEntity>>()

    val itemPaginationStateLiveData: LiveData<ItemPaginationState> =
        petPaginationUseCase.itemPaginationStateLiveData
    val petsLiveData = petPaginationUseCase.buildPageListObservable().toLiveData()

    override fun onBookMarkClick(petEntity: PetEntity) {
        bookMarkSubject.onNext(petEntity)
    }

    override fun onItemClick(petEntity: PetEntity, view: View) {
        _navigateToAnimalDetailsAction.postValue(Pair(petEntity, view))
    }

    override fun trackSearchToDetails() {
        firebaseManager.logUiEvent(AnalyticsAction.SEARCH_TO_DETAILS_TRANSITION,
            AnalyticsAction.CLICK)
    }

    override fun trackSearchToFilter() {
        firebaseManager.logUiEvent(AnalyticsAction.SEARCH_TO_FILTER_TRANSITION,
            AnalyticsAction.CLICK)
    }

    override fun trackRetrySearch() {
        firebaseManager.logUiEvent("Retry Search", AnalyticsAction.CLICK)
    }

    fun retryPagination() {
        trackRetrySearch()
        petPaginationUseCase.retry()
    }

    fun updateLocation(location: String) {
        fetchAppliedFilterUseCase.buildUseCaseSingle(true)
            .flatMapCompletable { filters ->
                if (!(filters.location ?: "").equals(location, true)) {
                    updateFilterUseCase.buildUseCaseCompletable(
                        PetFilterCheckableEntity(
                            location,
                            LOCATION,
                            selected = true,
                            filterApplied = true
                        )
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