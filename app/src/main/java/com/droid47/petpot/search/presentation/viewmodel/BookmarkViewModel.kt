package com.droid47.petpot.search.presentation.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.droid47.petpot.base.extensions.toLiveData
import com.droid47.petpot.base.firebase.AnalyticsAction
import com.droid47.petpot.base.firebase.CrashlyticsExt
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.widgets.BaseAndroidViewModel
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.base.widgets.components.LiveEvent
import com.droid47.petpot.search.data.models.search.FavouritePetEntity
import com.droid47.petpot.search.domain.interactors.AddOrRemoveFavoritePetUseCase
import com.droid47.petpot.search.domain.interactors.DataSourceType
import com.droid47.petpot.search.domain.interactors.RemoveAllFavouritePetsUseCase
import com.droid47.petpot.search.domain.interactors.SubscribeToFavouritePetDataSourceUseCase
import com.droid47.petpot.search.presentation.ui.widgets.PagedFavouriteListPetAdapter
import com.droid47.petpot.search.presentation.viewmodel.tracking.TrackBookmarkViewModel
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class BookmarkViewModel @Inject constructor(
    application: Application,
    subscribeToPetsUseCase: SubscribeToFavouritePetDataSourceUseCase,
    private val addOrRemoveFavoritePetUseCase: AddOrRemoveFavoritePetUseCase,
    private val removeAllFavouritePetsUseCase: RemoveAllFavouritePetsUseCase,
    val firebaseManager: IFirebaseManager
) : BaseAndroidViewModel(application), PagedFavouriteListPetAdapter.OnItemClickListener, TrackBookmarkViewModel {

    val bookmarkListLiveData: LiveData<BaseStateModel<out PagedList<FavouritePetEntity>>> =
        subscribeToPetsUseCase.buildUseCaseObservable(Pair(DataSourceType.FavoriteType, ""))
            .toLiveData()

    private val _navigateToAnimalDetailsAction = LiveEvent<Pair<FavouritePetEntity, View>>()
    val navigateToAnimalDetailsAction: LiveEvent<Pair<FavouritePetEntity, View>>
        get() = _navigateToAnimalDetailsAction

    private val _undoBookmarkLiveData = LiveEvent<BaseStateModel<FavouritePetEntity>>()
    val undoBookmarkLiveData: LiveEvent<BaseStateModel<FavouritePetEntity>>
        get() = _undoBookmarkLiveData

    override fun trackDeleteAll(actionType: Boolean) {
        firebaseManager.logUiEvent("Delete All Favorites : $actionType", AnalyticsAction.CLICK)
    }

    override fun trackBookmarkToDetails() {
        firebaseManager.logUiEvent(AnalyticsAction.BOOKMARK_TO_DETAILS, AnalyticsAction.CLICK)
    }

    override fun trackUnBookmark() {
        firebaseManager.logUiEvent("UnBookmark Favorite", AnalyticsAction.CLICK)
    }

    override fun onBookMarkClick(petEntity: FavouritePetEntity) {
        trackUnBookmark()
        addOrRemoveFavoritePetUseCase.execute(
            petEntity,
            object : SingleObserver<BaseStateModel<FavouritePetEntity>> {

                override fun onSuccess(stateModel: BaseStateModel<FavouritePetEntity>) {
                    _undoBookmarkLiveData.postValue(stateModel)
                }

                override fun onSubscribe(d: Disposable) {
                    registerDisposableRequest(REQUEST_BOOK_MARK, d)
                }

                override fun onError(e: Throwable) {
                    _undoBookmarkLiveData.postValue(Failure(e))
                }

            })
    }

    override fun onItemClick(petEntity: FavouritePetEntity, view: View) {
        trackBookmarkToDetails()
        _navigateToAnimalDetailsAction.postValue(Pair(petEntity, view))
    }

    fun deleteAllFavoritePets() {
        trackDeleteAll(true)
        removeAllFavouritePetsUseCase.execute(
            Unit,
            object : CompletableObserver {
                override fun onComplete() {
//                    val test = ""
                }

                override fun onSubscribe(d: Disposable) {
                    registerDisposableRequest(REQUEST_DELETE_ALL_BOOK_MARK, d)
                }

                override fun onError(e: Throwable) {
                    CrashlyticsExt.handleException(e)
                }
            })
    }

    companion object {
        private const val REQUEST_BOOK_MARK = 1290L
        private const val REQUEST_DELETE_ALL_BOOK_MARK = 1291L
    }
}