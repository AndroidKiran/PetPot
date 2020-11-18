package com.droid47.petpot.search.domain.interactors

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.text.TextUtils
import com.droid47.petpot.app.data.LocalPreferenceDataSource
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.base.usecase.SubjectUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Success
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SaveLocationToPreferenceUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val preferencesRepository: LocalPreferencesRepository
) : SubjectUseCase<Unit, String>(threadExecutor, postExecutionThread) {

    @SuppressLint("CheckResult")
    override fun buildUseCaseSubject(observer: Observer<Unit>): Subject<String> {
        saveLocationSubject.debounce(400, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .switchMapSingle { location ->
                Single.fromCallable {
                    preferencesRepository.saveLocation(location)
                }
            }.subscribe(observer)
        return saveLocationSubject
    }

}