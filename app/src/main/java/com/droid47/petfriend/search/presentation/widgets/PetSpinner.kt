package com.droid47.petfriend.search.presentation.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSpinner
import com.droid47.petfriend.base.extensions.clearDisposable
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.search.presentation.viewmodel.PetSpinnerAndLocationViewModel
import com.droid47.petfriend.search.presentation.viewmodel.PetSpinnerAndLocationViewModel.Companion.REQUEST_REFRESH_SELECTED_PET
import io.reactivex.CompletableObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class PetSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.imageButtonStyle
) : AppCompatSpinner(context, attrs, defStyleAttr) {

    private var petSpinnerAndLocationViewModel: PetSpinnerAndLocationViewModel? = null
    private var location: String? = null

    //    private val baseSharedPreference = BaseSharedPreference(context)
    private val subject = PublishSubject.create<String>().toSerialized()
    private val compositeDisposable = CompositeDisposable()

    init {
        initInsertEvent()
    }

    override fun onDetachedFromWindow() {
        compositeDisposable.clearDisposable()
        super.onDetachedFromWindow()
    }

    fun setPetSpinnerViewModelAndLocation(petSpinnerAndLocationViewModel: PetSpinnerAndLocationViewModel) {
        this.petSpinnerAndLocationViewModel = petSpinnerAndLocationViewModel
    }

    fun setLocation(location: String?) {
        this.location = location
    }

    fun onPetSelected(item: String) {
        subject.onNext(item)
    }

    @SuppressLint("CheckResult")
    private fun initInsertEvent() {
        subject.debounce(100, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .doOnSubscribe { compositeDisposable.add(it) }
            .switchMapCompletable { petSpinnerAndLocationViewModel?.refreshSelectedPet(it) }
            .subscribeWith(object : CompletableObserver {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    petSpinnerAndLocationViewModel?.registerDisposableRequest(REQUEST_REFRESH_SELECTED_PET, d)
                }

                override fun onError(e: Throwable) {
                    CrashlyticsExt.handleException(e)
                }

            })
    }

}