package com.droid47.petfriend.app.di.modules

import android.app.Application
import com.droid47.petfriend.BuildConfig
import com.droid47.petfriend.app.data.LocalPreferenceDataSource
import com.droid47.petfriend.base.network.NetworkHeadersInterceptor
import com.droid47.petfriend.base.network.TokenAuthenticator
import com.droid47.petfriend.base.storage.LocalPreferencesRepository
import com.droid47.petfriend.base.network.TokenNetworkSource
import com.droid47.petfriend.search.data.datasource.SearchNetworkSource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Lazy
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Modifier
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @JvmStatic
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.HEADERS
            level = HttpLoggingInterceptor.Level.BODY
        } else {
            level = HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    @JvmStatic
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .excludeFieldsWithModifiers(Modifier.TRANSIENT)
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()

    @Provides
    @JvmStatic
    @Singleton
    fun provideNetworkHeaderInterceptor(
        localPreferencesRepository: LocalPreferencesRepository,
        gson: Gson
    ): NetworkHeadersInterceptor =
        NetworkHeadersInterceptor(
            localPreferencesRepository,
            gson
        )

    @Provides
    @JvmStatic
    @Singleton
    fun provideAuthenticator(
        localPreferencesRepository: LocalPreferencesRepository,
        gson: Gson,
        tokenNetworkSource: Lazy<TokenNetworkSource>
    ): TokenAuthenticator =
        TokenAuthenticator(
            localPreferencesRepository,
            gson,
            tokenNetworkSource
        )

    @Provides
    @JvmStatic
    @Singleton
    fun providesOkHttpCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    @JvmStatic
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        headersInterceptor: NetworkHeadersInterceptor,
        authenticator: TokenAuthenticator,
        cache: Cache
    ): OkHttpClient =
        OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .authenticator(authenticator)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(headersInterceptor)
            .build()

    @Provides
    @JvmStatic
    @Singleton
    fun providesGsonConverterFactory(gson: Gson): Converter.Factory =
        GsonConverterFactory.create(gson)

    @Provides
    @JvmStatic
    @Singleton
    fun providesCallAdapterFactory(): CallAdapter.Factory = RxJava2CallAdapterFactory.create()

    @Provides
    @JvmStatic
    @Singleton
    fun provideNetworkClient(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: Converter.Factory,
        callAdapterFactor: CallAdapter.Factory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(callAdapterFactor)
        .client(okHttpClient)
        .build()

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