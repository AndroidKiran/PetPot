package com.droid47.petpot.app.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.droid47.petpot.base.firebase.CrashlyticsExt
import javax.inject.Inject
import javax.inject.Provider

class DaggerAwareViewModelFactory @Inject constructor(
    private val creators: @JvmSuppressWildcards Map<Class<out ViewModel>,
            Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val found = creators.entries.find { modelClass.isAssignableFrom(it.key) }
        val creator = found?.value
            ?: throw IllegalArgumentException("unknown model class $modelClass")
        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        } catch (e: Exception) {
            CrashlyticsExt.handleException(e)
            throw RuntimeException(e)
        }
    }
}
