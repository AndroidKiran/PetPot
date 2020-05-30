package com.droid47.petfriend.petDetails.presentation.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.toLiveData
import androidx.paging.PagedList
import com.droid47.petfriend.R
import com.droid47.petfriend.app.PetApplication
import com.droid47.petfriend.base.extensions.*
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.components.LiveEvent
import com.droid47.petfriend.petDetails.domain.interactors.FetchSelectedPetFromDbUseCase
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.domain.interactors.DataSourceType
import com.droid47.petfriend.search.domain.interactors.SubscribeToPetsUseCase
import com.droid47.petfriend.search.domain.interactors.UpdateFavoritePetUseCase
import com.droid47.petfriend.search.presentation.ui.widgets.*
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PetDetailsViewModel @Inject constructor(
    application: Application,
    subscribeToPetsUseCase: SubscribeToPetsUseCase,
    private val fetchSelectedPetFromDbUseCase: FetchSelectedPetFromDbUseCase,
    private val updateFavoritePetUseCase: UpdateFavoritePetUseCase
) : BaseAndroidViewModel(application), PagedListPetAdapter.OnItemClickListener {

    private val starSubject = PublishSubject.create<PetEntity>().toSerialized()
    var openingAnimationRequired = true
    val transitionId: MutableLiveData<Int> = MutableLiveData()
    val petsLiveData: LiveData<BaseStateModel<out PagedList<PetEntity>>> =
        subscribeToPetsUseCase.buildUseCaseObservable(Pair(DataSourceType.NonFavoriteType, "")).toLiveData()

    private val _navigateToAnimalDetailsAction = LiveEvent<Pair<PetEntity, View>>()
    val navigateToAnimalDetailsAction: LiveEvent<Pair<PetEntity, View>>
        get() = _navigateToAnimalDetailsAction

    val petId: MutableLiveData<Int> = MutableLiveData()
    val petLiveData = petId.switchMap {
        if (it == null) {
            MutableLiveData()
        } else {
            fetchSelectedPetFromDbUseCase.buildUseCaseObservable(it).toLiveData()
        }
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
        starSubject.onNext(petEntity.apply {
            bookmarkStatus = !petEntity.bookmarkStatus
            bookmarkedAt = System.currentTimeMillis()
        })
    }

    override fun onItemClick(petEntity: PetEntity, view: View) {
        _navigateToAnimalDetailsAction.postValue(Pair(petEntity, view))
    }

    fun getAddress() = petLiveData.value?.data?.contactEntity?.addressEntity?.getAddress()

    @SuppressLint("CheckResult")
    private fun onPetStarred() {
        starSubject.debounce(200, TimeUnit.MILLISECONDS)
            .doOnSubscribe { registerDisposableRequest(REQUEST_STAR_PET, it) }
            .switchMapSingle { bookMarkStatusAndPetPair ->
                updateFavoritePetUseCase.buildUseCaseSingle(bookMarkStatusAndPetPair)
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
        private const val REQUEST_PET_INFO = 12200L
        private const val REQUEST_STAR_PET = 12201L
    }
}