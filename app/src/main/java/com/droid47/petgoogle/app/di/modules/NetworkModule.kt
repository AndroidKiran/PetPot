package com.droid47.petgoogle.app.di.modules

import android.app.Application
import com.droid47.petgoogle.BuildConfig
import com.droid47.petgoogle.app.NetworkHeadersInterceptor
import com.droid47.petgoogle.app.TokenAuthenticator
import com.droid47.petgoogle.app.di.scopes.ApplicationScope
import com.droid47.petgoogle.app.domain.repositories.LocalPreferencesRepository
import com.droid47.petgoogle.launcher.data.datasources.TokenNetworkSource
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

@Module(includes = [NetworkApiModule::class])
object NetworkModule {

    @Provides
    @JvmStatic
    @ApplicationScope
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
    @ApplicationScope
    fun provideGson(): Gson = GsonBuilder()
        .excludeFieldsWithModifiers(Modifier.TRANSIENT)
        .create()

//        .disableHtmlEscaping()


    @Provides
    @JvmStatic
    @ApplicationScope
    fun provideNetworkHeaderInterceptor(
        localPreferencesRepository: LocalPreferencesRepository,
        gson: Gson
    ): NetworkHeadersInterceptor =
        NetworkHeadersInterceptor(localPreferencesRepository, gson)

    @Provides
    @JvmStatic
    @ApplicationScope
    fun provideAuthenticator(localPreferencesRepository: LocalPreferencesRepository,
                             gson: Gson,
                             tokenNetworkSource: Lazy<TokenNetworkSource>): TokenAuthenticator =
        TokenAuthenticator(localPreferencesRepository,gson, tokenNetworkSource)

    @Provides
    @JvmStatic
    @ApplicationScope
    fun providesOkHttpCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    @JvmStatic
    @ApplicationScope
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        headersInterceptor: NetworkHeadersInterceptor,
        authenticator: TokenAuthenticator,
        cache: Cache
    ): OkHttpClient =
        OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .authenticator(authenticator)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(headersInterceptor)
            .build()


    @Provides
    @JvmStatic
    @ApplicationScope
    fun providesGsonConverterFactory(gson: Gson): Converter.Factory = GsonConverterFactory.create(gson)

    @Provides
    @JvmStatic
    @ApplicationScope
    fun providesCallAdapterFactory(): CallAdapter.Factory = RxJava2CallAdapterFactory.create()

    @Provides
    @JvmStatic
    @ApplicationScope
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


}