package com.droid47.petgoogle.app.di.modules

import com.droid47.petgoogle.app.data.LocalPreferenceDataSource
import com.droid47.petgoogle.app.domain.JobExecutor
import com.droid47.petgoogle.app.domain.UIThread
import com.droid47.petgoogle.app.domain.repositories.LocalPreferencesRepository
import com.droid47.petgoogle.base.usecase.executor.PostExecutionThread
import com.droid47.petgoogle.base.usecase.executor.ThreadExecutor
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, StorageModule::class])
interface AppModule {

//    @Binds
//    @Singleton
//    fun bindApplication(bind: PetApplication): Application

    @Binds
    @Singleton
    fun bindThreadExecutor(jobExecutor: JobExecutor): ThreadExecutor

    @Binds
    @Singleton
    fun bindPostExecutionThread(uiThread: UIThread): PostExecutionThread

    @Binds
    @Singleton
    fun bindLocalSharedPreference(localSharedPreferenceDataSource: LocalPreferenceDataSource): LocalPreferencesRepository
}