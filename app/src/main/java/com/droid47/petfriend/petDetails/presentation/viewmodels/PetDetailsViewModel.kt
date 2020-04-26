package com.droid47.petfriend.petDetails.presentation.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.droid47.petfriend.R
import com.droid47.petfriend.app.PetApplication
import com.droid47.petfriend.base.extensions.*
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.components.LiveEvent
import com.droid47.petfriend.bookmark.domain.interactors.UpdateFavoritePetUseCase
import com.droid47.petfriend.petDetails.domain.interactors.FetchFavoriteStateUseCase
import com.droid47.petfriend.petDetails.domain.interactors.FetchDetailsUseCase
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.presentation.widgets.*
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PetDetailsViewModel @Inject constructor(
    application: Application,
    private val fetchDetailsUseCase: FetchDetailsUseCase,
    private val fetchFavoriteStateUseCase: FetchFavoriteStateUseCase,
    private val updateFavoritePetUseCase: UpdateFavoritePetUseCase
) : BaseAndroidViewModel(application), PetAdapter.OnItemClickListener {

    private val _navigateToAnimalDetailsAction = LiveEvent<Pair<PetEntity, View>>()
    val navigateToAnimalDetailsAction: LiveEvent<Pair<PetEntity, View>>
        get() = _navigateToAnimalDetailsAction

    private val starSubject = PublishSubject.create<PetEntity>().toSerialized()

    val petLiveData = MutableLiveData<BaseStateModel<PetEntity>>()

    private val petId: LiveData<Int> = Transformations.map(petLiveData) {
        it?.data?.id ?: 0
    }

    val transitionName: LiveData<String> = Transformations.map(petLiveData) {
        it?.data?.transitionName ?: (it?.data?.id ?: 0).toString()
    }

    val bookMarkStatusLiveData = petId.switchMap { id ->
        fetchFavoriteStateUseCase.buildUseCaseObservable(id)
            .applyIOSchedulers()
            .toLiveData()
    }

    val nameLiveData: LiveData<String> = Transformations.map(petLiveData) {
        it?.data?.name?.wordCapitalize(' ') ?: ""
    }

    val breedLiveData: LiveData<String> = Transformations.map(petLiveData) {
        it?.data?.breedEntity?.bindBreed(application)?.stringSentenceCase()
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
        it?.data?.contactEntity?.addressEntity?.bindAddress(application)
    }

    val characteristicsLiveData: LiveData<String> = Transformations.map(petLiveData) {
        it?.data?.tags?.bindCharacteristics()?.stringSentenceCase()
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
        it?.data?.attributesEntity?.bindAttributes(application)?.stringSentenceCase()
    }

    val goodLiveData: LiveData<String> = Transformations.map(petLiveData) {
        it?.data?.environmentEntity?.bindEnvironment(
            application,
            it.data?.type
        )?.stringSentenceCase()
    }

    val descLiveData: LiveData<String> = Transformations.map(petLiveData) {
        it?.data?.desc ?: "".stringSentenceCase().fromHtml().toString()
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

    fun getAddress() = petLiveData.value?.data?.contactEntity?.addressEntity?.getAddress()

    @SuppressLint("CheckResult")
    private fun onPetStarred() {
        starSubject.debounce(200, TimeUnit.MILLISECONDS)
            .doOnSubscribe { registerRequest(REQUEST_STAR_PET, it) }
            .switchMapSingle { bookMarkStatusAndPetPair ->
                updateFavoritePetUseCase.buildUseCaseSingle(bookMarkStatusAndPetPair.apply {
                    bookmarkStatus = true
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