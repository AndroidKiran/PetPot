package com.droid47.petfriend.bookmark.presentation.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.toLiveData
import androidx.paging.PagedList
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.components.LiveEvent
import com.droid47.petfriend.bookmark.domain.interactors.DataSourceType
import com.droid47.petfriend.bookmark.domain.interactors.RemoveAllPetsUseCase
import com.droid47.petfriend.bookmark.domain.interactors.SubscribeToPetsUseCase
import com.droid47.petfriend.bookmark.domain.interactors.UpdateFavoritePetUseCase
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.presentation.widgets.PagedListPetAdapter
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class BookmarkViewModel @Inject constructor(
    application: Application,
    subscribeToPetsUseCase: SubscribeToPetsUseCase,
    private val updateFavoritePetUseCase: UpdateFavoritePetUseCase,
    private val removeAllPetsUseCase: RemoveAllPetsUseCase
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
        removeAllPetsUseCase.execute(observer = object : SingleObserver<Int> {
            override fun onSuccess(t: Int) {

            }

            override fun onSubscribe(d: Disposable) {
                registerDisposableRequest(REQUEST_DELETE_ALL_BOOK_MARK, d)
            }

            override fun onError(e: Throwable) {
                _undoBookmarkLiveData.postValue(Failure(e))
            }
        })
    }

    companion object {
        private const val REQUEST_BOOK_MARK = 1290L
        private const val REQUEST_DELETE_ALL_BOOK_MARK = 1291L
    }

}