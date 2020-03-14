package com.droid47.petgoogle.base.widgets.inAppUpdate

import android.content.IntentSender.SendIntentException
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.droid47.petgoogle.R
import com.droid47.petgoogle.base.widgets.BaseStateModel
import com.droid47.petgoogle.base.widgets.Failure
import com.droid47.petgoogle.base.widgets.Success
import com.droid47.petgoogle.base.widgets.components.LiveEvent
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability


class InAppUpdateManager private constructor(
    var appCompatActivity: AppCompatActivity,
    var updateType: Int = AppUpdateType.FLEXIBLE,
    var resumeUpdates: Boolean = false,
    var useCustomNotification: Boolean = false,
    var snackBarMessage: String? = null,
    var snackBarAction: String? = null,
    var snackBarActionColor: Int = R.color.design_default_color_on_primary
) : LifecycleObserver {

    val appUpdateManagerLiveData = LiveEvent<BaseStateModel<InAppUpdateStatus>>()
    private var appUpdateManager: AppUpdateManager? = null
    private var snackBar: Snackbar? = null
    private val inAppUpdateStatus = InAppUpdateStatus()

    init {
        setUpManager()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        if (resumeUpdates) checkNewAppVersionState()
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        unRegisterListener()
    }

    fun checkForUpdate() {
        initCheckForUpdate()
    }

    fun completeUpdate() {
        appUpdateManager?.completeUpdate()
    }

    private fun setUpManager() {
        setupSnackBar()
        appCompatActivity.lifecycle.addObserver(this@InAppUpdateManager)
        appUpdateManager = AppUpdateManagerFactory.create(appCompatActivity)
        registerListener()
    }

    private fun registerListener() {
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager?.registerListener(installStateUpdateListener)
        }
    }

    private fun unRegisterListener() {
        appUpdateManager?.unregisterListener(installStateUpdateListener)
    }

    private fun setupSnackBar() {
        val rootView: View = appCompatActivity.window.decorView.findViewById(android.R.id.content)
        snackBar = Snackbar.make(
            rootView,
            snackBarMessage?:"",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(snackBarAction?:"") {
                completeUpdate()
            }
        }
    }

    private fun popupSnackBarForUserConfirmation() {
        if (!useCustomNotification) {
            if (snackBar != null && snackBar?.isShownOrQueued == true) snackBar?.dismiss()
            snackBar?.show()
        }
    }

    private fun startAppUpdateImmediate(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager?.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE,  // The current activity making the update request.
                appCompatActivity,  // Include a request code to later monitor this update request.
                IN_APP_UPDATE_REQUEST_CODE
            )
        } catch (e: SendIntentException) {
            reportUpdateError(UPDATE_ERROR_START_APP_UPDATE_IMMEDIATE, e)
        }
    }

    private fun startAppUpdateFlexible(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager?.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.FLEXIBLE,  // The current activity making the update request.
                appCompatActivity,  // Include a request code to later monitor this update request.
                IN_APP_UPDATE_REQUEST_CODE
            )
        } catch (e: SendIntentException) {
            reportUpdateError(UPDATE_ERROR_START_APP_UPDATE_FLEXIBLE, e)
        }
    }

    private fun reportUpdateError(errorCode: Int, error: Throwable) {
        appUpdateManagerLiveData.postValue(Failure(error, inAppUpdateStatus.apply {
            setErrorCode(errorCode)
        }))
    }

    private fun reportStatus() {
        appUpdateManagerLiveData.postValue(Success(inAppUpdateStatus))
    }

    private fun checkNewAppVersionState() {
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener {
            val appUpdateInfo = it ?: return@addOnSuccessListener
            inAppUpdateStatus.setAppUpdateInfo(appUpdateInfo)

            //FLEXIBLE:
            // If the update is downloaded but not installed,
            // notify the user to complete the update.
            when {
                appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED -> {
                    popupSnackBarForUserConfirmation()
                    reportStatus()
                }

                appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS ->
                    startAppUpdateImmediate(appUpdateInfo)
            }
        }
    }

    private val installStateUpdateListener = InstallStateUpdatedListener {
        val installState = it?:return@InstallStateUpdatedListener
        inAppUpdateStatus.setInstallState(installState)
        reportStatus()

        // Show module progress, log state, or install the update.
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            popupSnackBarForUserConfirmation()
        }
    }

    private fun initCheckForUpdate() {
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener {
            val appUpdateInfo = it ?: return@addOnSuccessListener
            inAppUpdateStatus.setAppUpdateInfo(appUpdateInfo)
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE)
                return@addOnSuccessListener
            when {
                updateType == AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) ->
                    startAppUpdateFlexible(appUpdateInfo)

                updateType == AppUpdateType.IMMEDIATE -> startAppUpdateImmediate(appUpdateInfo)
            }
            reportStatus()
        }
    }

    data class Builder constructor(
        var appCompatActivity: AppCompatActivity,
        var updateType: Int = AppUpdateType.FLEXIBLE,
        var resumeUpdates: Boolean = false,
        var useCustomNotification: Boolean = false,
        var snackBarMessage: String? = null,
        var snackBarAction: String? = null,
        var snackBarActionColor: Int = R.color.design_default_color_on_primary
    ) {

        fun setActivity(appCompatActivity: AppCompatActivity) = apply {
            this.appCompatActivity = appCompatActivity
        }

        fun setAppUpdateType(updateType: Int) = apply {
            this.updateType = updateType
        }

        fun resumeUpdateProcess(resumeUpdates: Boolean) = apply {
            this.resumeUpdates = resumeUpdates
        }

        fun useCustomeNotification(useCustomNotification: Boolean) = apply {
            this.useCustomNotification = useCustomNotification
        }

        fun setSnackbarMsg(snackBarMessage: String) = apply {
            this.snackBarMessage = snackBarMessage
        }

        fun setSnackBarAction(snackBarAction: String) = apply {
            this.snackBarAction = snackBarAction
        }

        fun setSnackBarActionColor(snackBarActionColor: Int) = apply {
            this.snackBarActionColor = snackBarActionColor
        }

        fun build() =
            InAppUpdateManager(
                appCompatActivity,
                updateType,
                resumeUpdates,
                useCustomNotification,
                snackBarMessage,
                snackBarAction,
                snackBarActionColor
            )
    }

    companion object {
        const val IN_APP_UPDATE_REQUEST_CODE = 988

        const val UPDATE_ERROR_START_APP_UPDATE_FLEXIBLE = 100
        const val UPDATE_ERROR_START_APP_UPDATE_IMMEDIATE = 101
    }
}