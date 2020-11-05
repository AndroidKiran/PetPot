package com.droid47.petpot.base.firebase

import com.droid47.petpot.BuildConfig
import com.droid47.petpot.R
import com.droid47.petpot.base.usecase.SingleUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.base.widgets.Success
import com.droid47.petpot.home.data.AppUpgradeEntity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import io.reactivex.Single
import javax.inject.Inject

class RemoteConfigUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val gson: Gson
) : SingleUseCase<BaseStateModel<String>, String>(threadExecutor, postExecutionThread) {

    private val config: FirebaseRemoteConfig = Firebase.remoteConfig.apply {
        setDefaultsAsync(R.xml.remote_config_defaults)
        setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else ((60 * 8).toLong())
            }
        )
    }

    override fun buildUseCaseSingle(params: String): Single<BaseStateModel<String>> =
        Single.create<BaseStateModel<String>> { emitter ->
            try {
                config.fetchAndActivate().addOnCompleteListener {
                    if (it.isSuccessful) {
                        emitter.onSuccess(Success(config[params].asString()))
                    } else {
                        emitter.onError(
                            it.exception ?: IllegalStateException("Remote config fetch error")
                        )
                    }
                }
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }.onErrorReturn {
            Failure(it)
        }

    fun getUpgradeInfoEntity(value: String): AppUpgradeEntity? =
        try {
            gson.fromJson(value, AppUpgradeEntity::class.java)
        } catch (exception: Exception) {
            CrashlyticsExt.handleException(exception)
            null
        }

    companion object {
        const val KEY_APP_UPGRADE = "app_upgrade"
    }

}