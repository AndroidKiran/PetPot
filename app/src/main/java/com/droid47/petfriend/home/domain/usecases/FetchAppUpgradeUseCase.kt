package com.droid47.petfriend.home.domain.usecases

import com.droid47.petfriend.BuildConfig
import com.droid47.petfriend.base.firebase.FirebaseRemoteConfig
import com.droid47.petfriend.base.usecase.SingleUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.home.data.AppUpgradeEntity
import com.droid47.petfriend.home.data.UpgradeInfoEntity
import com.google.firebase.remoteconfig.ktx.get
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.SingleEmitter
import javax.inject.Inject

private const val KEY_APP_UPGRADE = "app_upgrade"

class FetchAppUpgradeUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val gson: Gson,
    private val firebaseRemoteConfig: FirebaseRemoteConfig
) : SingleUseCase<BaseStateModel<UpgradeInfoEntity>, Unit>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: Unit?): Single<BaseStateModel<UpgradeInfoEntity>> {
        return Single.create<BaseStateModel<UpgradeInfoEntity>> { emitter ->
            try {
                fetchConfig(firebaseRemoteConfig.config, emitter)
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }
    }

    private fun fetchConfig(
        remoteConfig: com.google.firebase.remoteconfig.FirebaseRemoteConfig,
        emitter: SingleEmitter<BaseStateModel<UpgradeInfoEntity>>
    ) {
        remoteConfig.fetchAndActivate()
            .addOnSuccessListener {
                try {
                    val appUpgradeEntity = gson.fromJson(
                        remoteConfig[KEY_APP_UPGRADE].asString(),
                        AppUpgradeEntity::class.java
                    )
                    val upgradeInfoEntityList = appUpgradeEntity?.upgradeInfoList ?: emptyList()
                    var upgradeInfoEntity: UpgradeInfoEntity? = null
                    for (upgradeInfoItem in upgradeInfoEntityList) {
                        if (BuildConfig.VERSION_CODE == upgradeInfoItem.appVersionCode) {
                            upgradeInfoEntity = upgradeInfoItem
                            break
                        }
                    }
                    emitter.onSuccess(
                        if (upgradeInfoEntity != null)
                            Success(upgradeInfoEntity)
                        else
                            Failure(IllegalStateException("UpgradeInfoItem is null"))
                    )
                } catch (exception: Exception) {
                    emitter.onError(exception)
                }
            }.addOnFailureListener {
                emitter.onError(it)
            }
    }


}