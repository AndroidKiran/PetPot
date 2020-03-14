package com.droid47.petgoogle.app.domain.repositories

interface LocalPreferencesRepository {

    fun saveToken(tokenStr: String)

    fun fetchToken(): String

    fun fetchAppliedTheme(): String

    fun fetchSearchLimit(): Int

    fun saveOnBoardingState()

    fun getOnBoardingState(): Boolean

    fun saveTnCState()

    fun getTnCState(): Boolean

    fun saveFcmToken(token: String)

    fun getFcmToken(): String?
}