package com.droid47.petgoogle.base.bindingConfig

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.droid47.petgoogle.BR

class ErrorViewConfiguration : BaseObservable() {

    @get:Bindable
    var errorScreenDrawable: Drawable? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.errorScreenDrawable)
        }

    @get:Bindable
    var errorScreenText: CharSequence? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.errorScreenText)
        }

    @get:Bindable
    var errorScreenTextSubTitle: CharSequence? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.errorScreenTextSubTitle)
        }

    @get:Bindable
    var errorBtnText: CharSequence? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.errorBtnText)
        }

    @get:Bindable
    var errorRetryClickListener: View.OnClickListener? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.errorRetryClickListener)
        }
}