package com.droid47.petpot.search.domain.interactors

import android.content.SharedPreferences
import android.text.TextUtils
import com.droid47.petpot.app.data.LocalPreferenceDataSource
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.base.usecase.FlowableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Success
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject

class SubscribeToLocationChangeUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val preferencesRepository: LocalPreferencesRepository
) : FlowableUseCase<BaseStateModel<String>, Unit>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseObservable(params: Unit): Flowable<BaseStateModel<String>> {
        return Flowable.create({ emitter ->
            val emitCurrentKey =
                { emitter.onNext(Success(preferencesRepository.getLocation() ?: "")) }

            val preferencesListener =
                SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (TextUtils.equals(key, LocalPreferenceDataSource.KEY_LOCATION)) {
                        emitCurrentKey()
                    }
                }

            preferencesRepository.sharedPreferences.registerOnSharedPreferenceChangeListener(
                preferencesListener
            )

            emitCurrentKey()

            emitter.setCancellable {
                preferencesRepository.sharedPreferences.unregisterOnSharedPreferenceChangeListener(
                    preferencesListener
                )
            }
        }, BackpressureStrategy.LATEST)
    }
}