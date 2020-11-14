package com.droid47.petpot.petDetails.presentation.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.toLiveData
import androidx.paging.PagedList
import com.droid47.petpot.R
import com.droid47.petpot.app.PetApplication
import com.droid47.petpot.app.di.scopes.ActivityScope
import com.droid47.petpot.app.di.scopes.FragmentScope
import com.droid47.petpot.base.extensions.*
import com.droid47.petpot.base.firebase.AnalyticsAction
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.widgets.BaseAndroidViewModel
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.components.LiveEvent
import com.droid47.petpot.petDetails.domain.interactors.FetchSelectedPetFromDbUseCase
import com.droid47.petpot.petDetails.presentation.viewmodels.tracking.TrackPetDetailsViewModel
import com.droid47.petpot.search.data.models.search.PetEntity
import com.droid47.petpot.search.domain.interactors.DataSourceType
import com.droid47.petpot.search.domain.interactors.SubscribeToPetDataSourceUseCase
import com.droid47.petpot.search.domain.interactors.UpdateFavoritePetUseCase
import com.droid47.petpot.search.presentation.ui.widgets.*
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PetDetailsViewModel @Inject constructor(
    application: Application,
    subscribeToPetsUseCase: SubscribeToPetDataSourceUseCase,
    private val fetchSelectedPetFromDbUseCase: FetchSelectedPetFromDbUseCase,
    private val updateFavoritePetUseCase: UpdateFavoritePetUseCase,
    val firebaseManager: IFirebaseManager
) : BaseAndroidViewModel(application), PagedListPetAdapter.OnItemClickListener,
    TrackPetDetailsViewModel {

    private val starSubject = PublishSubject.create<PetEntity>().toSerialized()
    var openingAnimationRequired = true
    val transitionId: MutableLiveData<Int> = MutableLiveData()
    val petsLiveData: LiveData<BaseStateModel<out PagedList<PetEntity>>> =
        subscribeToPetsUseCase.buildUseCaseObservable(Pair(DataSourceType.NonFavoriteType, ""))
            .toLiveData()

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

    override fun trackPetImageSwipe() {
        firebaseManager.logUiEvent("Pet Image Swipe", AnalyticsAction.SWIPE)
    }

    override fun trackDetailsToMap() {
        firebaseManager.logUiEvent(AnalyticsAction.PET_DETAILS_TO_MAP, AnalyticsAction.CLICK)
    }

    override fun trackFavoriteSelected(bookmarked: Boolean) {
        firebaseManager.logUiEvent("Pet bookmarked : $bookmarked", AnalyticsAction.CLICK)
    }

    override fun trackShare() {
        firebaseManager.logUiEvent("Pet Shared", AnalyticsAction.CLICK)
    }

    override fun trackMail() {
        firebaseManager.logUiEvent("Mail to shelter", AnalyticsAction.CLICK)

    }

    override fun trackCall() {
        firebaseManager.logUiEvent("Call to shelter", AnalyticsAction.CLICK)
    }

    override fun trackSimilarPetSelected() {
        firebaseManager.logUiEvent("Similar Pet Selected", AnalyticsAction.CLICK)
    }

    override fun onBookMarkClick(petEntity: PetEntity) {
        trackFavoriteSelected(!petEntity.bookmarkStatus)
        starSubject.onNext(petEntity.apply {
            bookmarkStatus = !petEntity.bookmarkStatus
            bookmarkedAt = System.currentTimeMillis()
        })
    }

    override fun onItemClick(petEntity: PetEntity, view: View) {
        trackSimilarPetSelected()
        _navigateToAnimalDetailsAction.postValue(Pair(petEntity, view))
    }

    fun getAddress() = petLiveData.value?.data?.contactEntity?.addressEntity?.getAddress()

    @SuppressLint("CheckResult")
    private fun onPetStarred() {
        starSubject.debounce(400, TimeUnit.MILLISECONDS)
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