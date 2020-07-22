package com.droid47.petfriend.app.di.modules

import android.app.Application
import com.droid47.petfriend.app.data.LocalPreferenceDataSource
import com.droid47.petfriend.app.domain.ActivityLifecycleCallbacks
import com.droid47.petfriend.app.domain.JobExecutor
import com.droid47.petfriend.app.domain.UIThread
import com.droid47.petfriend.base.storage.LocalPreferencesRepository
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
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
    fun bindActivityLifeCycleCallback(activityLifecycleCallbacks: ActivityLifecycleCallbacks): Application.ActivityLifecycleCallbacks
}