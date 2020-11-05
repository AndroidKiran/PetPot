package com.droid47.petpot.base.bindingConfig

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.droid47.petpot.BR

class ContentLoadingConfiguration : BaseObservable() {

    @get:Bindable
    var contentLoadingText: CharSequence? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.contentLoadingText)
        }
}