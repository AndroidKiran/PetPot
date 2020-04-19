package com.droid47.petfriend.workmanagers.di

import androidx.work.RxWorker
import androidx.work.WorkerParameters
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Provider

@Subcomponent(modules = [WorkersBindingModule::class])
interface WorkManagerSubComponent {

    fun workers(): Map<Class<out RxWorker>, Provider<RxWorker>>

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance param: WorkerParameters): WorkManagerSubComponent
    }
}