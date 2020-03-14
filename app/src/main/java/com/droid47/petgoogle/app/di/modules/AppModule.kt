package com.droid47.petgoogle.app.di.modules

import android.app.Application
import com.droid47.petgoogle.app.PetApplication
import com.droid47.petgoogle.app.data.LocalPreferenceDataSource
import com.droid47.petgoogle.app.di.scopes.ApplicationScope
import com.droid47.petgoogle.app.domain.JobExecutor
import com.droid47.petgoogle.app.domain.UIThread
import com.droid47.petgoogle.app.domain.repositories.LocalPreferencesRepository
import com.droid47.petgoogle.base.usecase.executor.PostExecutionThread
import com.droid47.petgoogle.base.usecase.executor.ThreadExecutor
import dagger.Binds
import dagger.Module

@Module(includes = [NetworkModule::class, StorageModule::class])
interface AppModule {

    @Binds
    @ApplicationScope
    fun bindApplication(bind: PetApplication): Application

    @Binds
    @ApplicationScope
    fun bindThreadExecutor(jobExecutor: JobExecutor): ThreadExecutor

    @Binds
    @ApplicationScope
    fun bindPostExecutionThread(uiThread: UIThread): PostExecutionThread

    @Binds
    @ApplicationScope
    fun bindLocalSharedPreference(localSharedPreferenceDataSource: LocalPreferenceDataSource): LocalPreferencesRepository
}