package com.droid47.petgoogle.petDetails.presentation.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.droid47.petgoogle.R
import com.droid47.petgoogle.app.PetApplication
import com.droid47.petgoogle.base.extensions.*
import com.droid47.petgoogle.base.widgets.BaseAndroidViewModel
import com.droid47.petgoogle.base.widgets.BaseStateModel
import com.droid47.petgoogle.base.widgets.Failure
import com.droid47.petgoogle.base.widgets.components.LiveEvent
import com.droid47.petgoogle.bookmark.domain.interactors.AddOrRemoveBookmarkUseCase
import com.droid47.petgoogle.petDetails.domain.interactors.FetchBookmarkStateUseCase
import com.droid47.petgoogle.petDetails.domain.interactors.FetchDetailsUseCase
import com.droid47.petgoogle.search.data.models.search.PetEntity
import com.droid47.petgoogle.search.presentation.widgets.PetAdapter
import com.droid47.petgoogle.search.presentation.widgets.PetInfoUtil
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PetDetailsViewModel @Inject constructor(
    application: Application,
    private val fetchDetailsUseCase: FetchDetailsUseCase,
    private val fetchBookmarkStateUseCase: FetchBookmarkStateUseCase,
    private val addOrRemoveBookmarkUseCase: AddOrRemoveBookmarkUseCase
) : BaseAndroidViewModel(application), PetAdapter.OnItemClickListener {

    private val _navigateToAnimalDetailsAction = LiveEvent<Pair<PetEntity, View>>()
    val navigateToAnimalDetailsAction: LiveEvent<Pair<PetEntity, View>>
        get() = _navigateToAnimalDetailsAction

    private val starSubject = PublishSubject.create<PetEntity>().toSerialized()

    val petLiveData = MutableLiveData<BaseStateModel<PetEntity>>()

    private val petId = Transformations.map(petLiveData) {
        it?.data?.id ?: 0
    }

    val transitionName = Transformations.map(petLiveData) {
        it?.data?.transitionName ?: it?.data?.id ?: 0
    }

    val bookMarkStatusLiveData = petId.switchMap { id ->
        fetchBookmarkStateUseCase.buildUseCaseObservable(id)
            .applyIOSchedulers()
            .toLiveData()
    }

    val nameLiveData: LiveData<String> = Transformations.map(petLiveData) {
        it?.data?.name?.wordCapitalize(' ') ?: ""
    }

    val breedLiveData: LiveData<String> = Transformations.map(petLiveData) {
        PetInfoUtil.bindBreed(application, it?.data?.breedEntity).stringSentenceCase()
    }

    val ageLiveData: LiveData<String> = Transformations.map(petLiveData) {
        it?.data?.age?.wordCapitalize(' ') ?: ""
    }

    val genderLiveData: LiveData<String> = Transformations.map(petLiveData) {
        it?.data?.gender?.wordCapitalize(' ') ?: ""
    }

    val sizeLiveData: LiveData<String> = Transformations.map(petLiveData) {
        it?.data?.size?.wordCapitalize(' ') ?: ""
    }

    val addressLiveData: LiveData<String> = Transformations.map(petLiveData) {
        PetInfoUtil.bindAddress(it?.data?.contactEntity?.addressEntity)
    }

    val characteristicsLiveData: LiveData<String> = Transformations.map(petLiveData) {
        PetInfoUtil.bindCharacteristics(it?.data?.tags).stringSentenceCase()
    }

    val coatLiveData: LiveData<String> = Transformations.map(petLiveData) {
        it?.data?.coat.wordCapitalize(' ') ?: ""
    }

    val homeTrainedLiveData: LiveData<String> = Transformations.map(petLiveData) {
        if (it?.data?.attributesEntity?.houseTrained == true) {
            getApplication<PetApplication>().getString(R.string.yes)
        } else {
            getApplication<PetApplication>().getString(R.string.no)
        }.wordCapitalize(' ') ?: ""
    }

    val healthLiveData: LiveData<String> = Transformations.map(petLiveData) {
        PetInfoUtil.bindAttributes(application, it?.data?.attributesEntity).stringSentenceCase()
    }

    val goodLiveData: LiveData<String> = Transformations.map(petLiveData) {
        PetInfoUtil.bindEnvironment(
            application,
            it?.data?.environmentEntity,
            it?.data?.type
        ).stringSentenceCase()
    }

    val descLiveData: LiveData<String> = Transformations.map(petLiveData) {
        it?.data?.desc ?: "".stringSentenceCase().fromHtml().toString() ?: ""
    }


    val phoneNumLiveData = Transformations.map(petLiveData) { stateModel ->
        stateModel.data?.contactEntity?.phone?.stringSentenceCase() ?: ""
    }


    val emailLiveData = Transformations.map(petLiveData) { stateModel ->
        stateModel.data?.contactEntity?.email?.stringSentenceCase() ?: ""
    }

    val urlLiveData = Transformations.map(petLiveData) { stateModel ->
        stateModel.data?.url ?: ""
    }

    init {
        onPetStarred()
    }

    override fun onBookMarkClick(petEntity: PetEntity) {
        starSubject.onNext(petEntity)
    }

    override fun onItemClick(petEntity: PetEntity, view: View) {
        _navigateToAnimalDetailsAction.postValue(Pair(petEntity, view))
    }

    fun fetchPetFromNetwork(petId: Int) {
        fetchDetailsUseCase.execute(petId, object : SingleObserver<BaseStateModel<PetEntity>> {

            override fun onSuccess(stateModel: BaseStateModel<PetEntity>) {
                petLiveData.postValue(stateModel)
            }

            override fun onSubscribe(d: Disposable) {
                registerRequest(REQUEST_PET_INFO, d)
            }

            override fun onError(e: Throwable) {
                petLiveData.postValue(Failure(e))
            }
        })
    }

    fun onBookMarkClick(petEntity: PetEntity, status: Boolean) {
        onBookMarkClick(petEntity.apply {
            bookmarkStatus = !status
        })
    }

    @SuppressLint("CheckResult")
    private fun onPetStarred() {
        starSubject.debounce(200, TimeUnit.MILLISECONDS)
            .doOnSubscribe { registerRequest(REQUEST_STAR_PET, it) }
            .switchMapSingle { bookMarkStatusAndPetPair ->
                addOrRemoveBookmarkUseCase.buildUseCaseSingle(bookMarkStatusAndPetPair.apply {
                    bookmarkedAt = System.currentTimeMillis()
                })
            }.applyIOSchedulers()
            .subscribe(this::onPetStarSuccess, this::onPetStarError)
    }

    private fun onPetStarSuccess(baseStateModel: BaseStateModel<PetEntity>) {
        val baseState = baseStateModel
    }

    private fun onPetStarError(throwable: Throwable) {
        val err = throwable
    }

    companion object {
        private const val REQUEST_PET_INFO = 12200
        private const val REQUEST_STAR_PET = 12201
    }
}