package com.droid47.petfriend.base.firebase

import com.droid47.petfriend.BuildConfig
import com.droid47.petfriend.R
import com.droid47.petfriend.base.usecase.SingleUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.home.data.AppUpgradeEntity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import io.reactivex.Single
import javax.inject.Inject

class RemoteConfigUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val gson: Gson
) : SingleUseCase<BaseStateModel<String>, String>(threadExecutor, postExecutionThread) {

    private val config: FirebaseRemoteConfig = Firebase.remoteConfig.apply {
        setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(
                if (BuildConfig.DEBUG) 0 else ((60 * 8).toLong())
            ).build()
        )
        setDefaultsAsync(R.xml.remote_config_defaults)
    }

    override fun buildUseCaseSingle(params: String?): Single<BaseStateModel<String>> =
        Single.create<BaseStateModel<String>> { emitter ->
            try {
                config.fetchAndActivate()
                    .addOnSuccessListener {
                        emitter.onSuccess(Success(config[KEY_APP_UPGRADE].asString()))
                    }.addOnFailureListener {
                        emitter.onError(it)
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
            CrashlyticsExt.logHandledException(exception)
            null
        }

    companion object {
        const val KEY_APP_UPGRADE = "app_upgrade"
    }

}