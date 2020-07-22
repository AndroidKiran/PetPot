package com.droid47.petfriend.search.presentation.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.droid47.petfriend.base.extensions.toLiveData
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.components.LiveEvent
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.domain.interactors.DataSourceType
import com.droid47.petfriend.search.domain.interactors.SubscribeToPetsUseCase
import com.droid47.petfriend.search.domain.interactors.UpdateFavoritePetUseCase
import com.droid47.petfriend.search.domain.interactors.UpdateFavouritePetsStatusUseCase
import com.droid47.petfriend.search.presentation.ui.widgets.PagedListPetAdapter
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class BookmarkViewModel @Inject constructor(
    application: Application,
    subscribeToPetsUseCase: SubscribeToPetsUseCase,
    private val updateFavoritePetUseCase: UpdateFavoritePetUseCase,
    private val updateFavouritePetsStatusUseCase: UpdateFavouritePetsStatusUseCase
) : BaseAndroidViewModel(application), PagedListPetAdapter.OnItemClickListener {

    val bookmarkListLiveData: LiveData<BaseStateModel<out PagedList<PetEntity>>> =
        subscribeToPetsUseCase.buildUseCaseObservable(Pair(DataSourceType.FavoriteType, ""))
            .toLiveData()

    private val _navigateToAnimalDetailsAction = LiveEvent<Pair<PetEntity, View>>()
    val navigateToAnimalDetailsAction: LiveEvent<Pair<PetEntity, View>>
        get() = _navigateToAnimalDetailsAction

    private val _undoBookmarkLiveData = LiveEvent<BaseStateModel<PetEntity>>()
    val undoBookmarkLiveData: LiveEvent<BaseStateModel<PetEntity>>
        get() = _undoBookmarkLiveData

    override fun onBookMarkClick(petEntity: PetEntity) {
        updateFavoritePetUseCase.execute(
            petEntity,
            object : SingleObserver<BaseStateModel<PetEntity>> {

                override fun onSuccess(stateModel: BaseStateModel<PetEntity>) {
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

    override fun onItemClick(petEntity: PetEntity, view: View) {
        _navigateToAnimalDetailsAction.postValue(Pair(petEntity, view))
    }

    fun deleteAllFavoritePets() {
        updateFavouritePetsStatusUseCase.execute(
            Pair(first = false, second = true),
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