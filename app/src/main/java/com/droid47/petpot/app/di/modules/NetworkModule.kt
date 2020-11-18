package com.droid47.petpot.app.di.modules

import android.app.Application
import com.droid47.petpot.BuildConfig
import com.droid47.petpot.base.network.OffsetDateTimeConverter
import com.droid47.petpot.base.network.TokenAuthenticatorInterceptor
import com.droid47.petpot.base.network.TokenNetworkSource
import com.droid47.petpot.search.data.datasource.SearchNetworkSource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.Lazy
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.threeten.bp.OffsetDateTime
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
    fun provideOffsetDateTimeAdapter(): OffsetDateTimeConverter = OffsetDateTimeConverter()


    @Provides
    @JvmStatic
    @Singleton
    fun provideGson(offsetDateTimeConverter: OffsetDateTimeConverter): Gson = GsonBuilder()
        .excludeFieldsWithModifiers(Modifier.TRANSIENT)
        .registerTypeAdapter(object : TypeToken<OffsetDateTime>() {}.type, offsetDateTimeConverter)
        .create()

    @Provides
    @JvmStatic
    @Singleton
    fun provideAuthenticator(
        tokenNetworkSource: Lazy<TokenNetworkSource>
    ): TokenAuthenticatorInterceptor =
        TokenAuthenticatorInterceptor(tokenNetworkSource)

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
        authenticatorInterceptor: TokenAuthenticatorInterceptor,
        cache: Cache
    ): OkHttpClient =
        OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .authenticator(authenticatorInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authenticatorInterceptor)
            .build()


    @Provides
    @JvmStatic
    @Singleton
    fun provideNetworkClient(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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