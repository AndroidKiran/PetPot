package com.droid47.petgoogle.app.di.modules

import com.droid47.petgoogle.launcher.data.datasources.TokenNetworkSource
import com.droid47.petgoogle.search.data.datasource.SearchNetworkSource
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
object NetworkApiModule {

    @Provides
    @JvmStatic
    @Singleton
    fun provideAuthTokenApi(retrofit: Retrofit): TokenNetworkSource =
        retrofit.create(TokenNetworkSource::class.java)

    @Provides
    @JvmStatic
    @Singleton
    fun provideAnimalTypeApi(retrofit: Retrofit): SearchNetworkSource =
        retrofit.create(SearchNetworkSource::class.java)
}