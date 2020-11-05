package com.droid47.petpot.workmanagers.di

import androidx.work.RxWorker
import com.droid47.petpot.workmanagers.SyncPetTypeWorker
import com.droid47.petpot.workmanagers.TriggerLocalNotificationWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface WorkersBindingModule {

    @Binds
    @IntoMap
    @WorkerKey(SyncPetTypeWorker::class)
    fun bindSyncPetTypeWorker(worker: SyncPetTypeWorker): RxWorker

    @Binds
    @IntoMap
    @WorkerKey(TriggerLocalNotificationWorker::class)
    fun bindLocalNotificationWorker(worker: TriggerLocalNotificationWorker): RxWorker
}