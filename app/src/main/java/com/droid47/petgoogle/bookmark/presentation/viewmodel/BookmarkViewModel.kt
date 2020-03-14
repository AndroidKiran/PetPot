package com.droid47.petgoogle.bookmark.presentation.viewmodel

import android.app.Application
import android.view.View
import com.droid47.petgoogle.base.extensions.applyIOSchedulers
import com.droid47.petgoogle.base.extensions.toLiveData
import com.droid47.petgoogle.base.widgets.BaseAndroidViewModel
import com.droid47.petgoogle.base.widgets.BaseStateModel
import com.droid47.petgoogle.base.widgets.Failure
import com.droid47.petgoogle.base.widgets.components.LiveEvent
import com.droid47.petgoogle.bookmark.domain.interactors.AddOrRemoveBookmarkUseCase
import com.droid47.petgoogle.bookmark.domain.interactors.DeleteAllBookmarkUseCase
import com.droid47.petgoogle.bookmark.domain.interactors.FetchBookmarkListUseCase
import com.droid47.petgoogle.search.data.models.search.PetEntity
import com.droid47.petgoogle.search.presentation.widgets.PetAdapter
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class BookmarkViewModel @Inject constructor(
    application: Application,
    fetchBookmarkListUseCase: FetchBookmarkListUseCase,
    private val addOrRemoveBookmarkUseCase: AddOrRemoveBookmarkUseCase,
    private val deleteAllBookmarkUseCase: DeleteAllBookmarkUseCase
) : BaseAndroidViewModel(application), PetAdapter.OnItemClickListener {

    val bookmarkListLiveData = fetchBookmarkListUseCase.buildUseCaseObservable()
        .applyIOSchedulers()
        .toLiveData()

    private val _navigateToAnimalDetailsAction = LiveEvent<Pair<PetEntity, View>>()
    val navigateToAnimalDetailsAction: LiveEvent<Pair<PetEntity, View>>
        get() = _navigateToAnimalDetailsAction

    private val _undoBookmarkLiveData = LiveEvent<BaseStateModel<PetEntity>>()
    val undoBookmarkLiveData: LiveEvent<BaseStateModel<PetEntity>>
        get() = _undoBookmarkLiveData

    override fun onBookMarkClick(petEntity: PetEntity) {
        addOrRemoveBookmarkUseCase.execute(
            petEntity,
            object : SingleObserver<BaseStateModel<PetEntity>> {

                override fun onSuccess(stateModel: BaseStateModel<PetEntity>) {
                    _undoBookmarkLiveData.postValue(stateModel)
                }

                override fun onSubscribe(d: Disposable) {
                    registerRequest(REQUEST_BOOK_MARK, d)
                }

                override fun onError(e: Throwable) {
                    _undoBookmarkLiveData.postValue(Failure(e))
                }

            })
    }

    override fun onItemClick(petEntity: PetEntity, view: View) {
        _navigateToAnimalDetailsAction.postValue(Pair(petEntity, view))
    }

    fun deleteAllBookmark() {
        deleteAllBookmarkUseCase.execute(observer = object : CompletableObserver {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
                registerRequest(REQUEST_DELETE_ALL_BOOK_MARK, d)
            }

            override fun onError(e: Throwable) {
                _undoBookmarkLiveData.postValue(Failure(e))
            }
        })
    }

    companion object {
        private const val REQUEST_BOOK_MARK = 1290
        private const val REQUEST_DELETE_ALL_BOOK_MARK = 1291
    }

}