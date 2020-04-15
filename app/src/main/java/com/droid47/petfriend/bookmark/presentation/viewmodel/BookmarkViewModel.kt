package com.droid47.petfriend.bookmark.presentation.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droid47.petfriend.base.extensions.applyIOSchedulers
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Loading
import com.droid47.petfriend.base.widgets.components.LiveEvent
import com.droid47.petfriend.bookmark.domain.interactors.AddOrRemoveBookmarkUseCase
import com.droid47.petfriend.bookmark.domain.interactors.DeleteAllBookmarkUseCase
import com.droid47.petfriend.bookmark.domain.interactors.FetchBookmarkListUseCase
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.presentation.widgets.PetAdapter
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class BookmarkViewModel @Inject constructor(
    application: Application,
    private val fetchBookmarkListUseCase: FetchBookmarkListUseCase,
    private val addOrRemoveBookmarkUseCase: AddOrRemoveBookmarkUseCase,
    private val deleteAllBookmarkUseCase: DeleteAllBookmarkUseCase
) : BaseAndroidViewModel(application), PetAdapter.OnItemClickListener {

    private val compositeDisposable = CompositeDisposable();
    private val _bookmarkListLiveData = MutableLiveData<BaseStateModel<List<PetEntity>>>()
    val bookmarkListLiveData: LiveData<BaseStateModel<List<PetEntity>>>
        get() = _bookmarkListLiveData

    private val _navigateToAnimalDetailsAction = LiveEvent<Pair<PetEntity, View>>()
    val navigateToAnimalDetailsAction: LiveEvent<Pair<PetEntity, View>>
        get() = _navigateToAnimalDetailsAction

    private val _undoBookmarkLiveData = LiveEvent<BaseStateModel<PetEntity>>()
    val undoBookmarkLiveData: LiveEvent<BaseStateModel<PetEntity>>
        get() = _undoBookmarkLiveData

    init {
        listenToBookmarkItems()
    }

    private fun listenToBookmarkItems() {
        compositeDisposable.add(
            fetchBookmarkListUseCase.buildUseCaseObservable()
                .doOnSubscribe {
                    _bookmarkListLiveData.postValue(Loading())
                }
                .applyIOSchedulers()
                .subscribeWith(
                    object : DisposableSubscriber<BaseStateModel<List<PetEntity>>>() {

                        override fun onComplete() {

                        }

                        override fun onNext(stateModel: BaseStateModel<List<PetEntity>>?) {
                            _bookmarkListLiveData.postValue(
                                stateModel ?: Failure(IllegalStateException("Bookmark list error"))
                            )
                        }

                        override fun onError(throwable: Throwable?) {
                            _bookmarkListLiveData.postValue(
                                Failure(
                                    throwable ?: IllegalStateException("Bookmark list error")
                                )
                            )
                        }
                    }
                )
        )
    }

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