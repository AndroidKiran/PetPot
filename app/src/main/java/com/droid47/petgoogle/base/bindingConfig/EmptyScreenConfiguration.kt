package com.droid47.petgoogle.base.bindingConfig

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.droid47.petgoogle.BR

class EmptyScreenConfiguration : BaseObservable() {

    @get:Bindable
    var emptyScreenDrawable: Drawable? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyScreenDrawable)
        }

    @get:Bindable
    var emptyScreenTitleText: CharSequence? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyScreenTitleText)
        }

    @get:Bindable
    var emptyScreenSubTitleText: CharSequence? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyScreenSubTitleText)
        }

    @get:Bindable
    var emptyScreenBtnText: CharSequence? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyScreenBtnText)
        }

    @get:Bindable
    var btnClickListener: View.OnClickListener? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.btnClickListener)
        }

}