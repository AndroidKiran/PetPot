package com.droid47.petpot.app.di.modules

import android.app.Application
import com.droid47.petpot.app.data.LocalPreferenceDataSource
import com.droid47.petpot.app.domain.JobExecutor
import com.droid47.petpot.app.domain.PetAppActivityLifecycleCallbacks
import com.droid47.petpot.app.domain.UIThread
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, StorageModule::class])
interface AppModule {

    @Binds
    @Singleton
    fun bindThreadExecutor(jobExecutor: JobExecutor): ThreadExecutor

    @Binds
    @Singleton
    fun bindPostExecutionThread(uiThread: UIThread): PostExecutionThread

    @Binds
    @Singleton
    fun bindLocalSharedPreference(localSharedPreferenceDataSource: LocalPreferenceDataSource): LocalPreferencesRepository

    @Binds
    @Singleton
    fun bindActivityLifeCycleCallback(petAppActivityLifecycleCallbacks: PetAppActivityLifecycleCallbacks): Application.ActivityLifecycleCallbacks
}