package com.droid47.petpot.base.widgets.inAppUpdate

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability


class InAppUpdateStatus {

    private var appUpdateInfo: AppUpdateInfo? = null
    private var installState: InstallState? = null
    private var errorCode = NO_ERROR

    fun setAppUpdateInfo(appUpdateInfo: AppUpdateInfo?) {
        this.appUpdateInfo = appUpdateInfo
    }

    fun setInstallState(installState: InstallState?) {
        this.installState = installState
    }

    fun setErrorCode(errorCode: Int) {
        this.errorCode = errorCode
    }

    val isDownloading: Boolean
        get() = installState?.installStatus() == InstallStatus.DOWNLOADING

    val isDownloaded: Boolean
        get() = installState?.installStatus() == InstallStatus.DOWNLOADED

    val isFailed: Boolean
        get() = installState?.installStatus() == InstallStatus.FAILED

    val isUpdateAvailable: Boolean
        get() = appUpdateInfo?.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

    fun availableVersionCode(): Int = appUpdateInfo?.availableVersionCode() ?: NO_UPDATE


    companion object {
        private const val NO_UPDATE = 0
        private const val NO_ERROR = 90
    }
}