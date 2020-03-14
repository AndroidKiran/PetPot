package com.droid47.petgoogle.base.widgets.bindingadapters

import androidx.databinding.*
import com.droid47.petgoogle.base.widgets.components.CheckableImageButton

object CheckableBindingAdapter {

    @JvmStatic
    @BindingAdapter("android:checked")
    fun setChecked(checkableImageButton: CheckableImageButton, checked: Boolean) {
        if (checkableImageButton.isChecked != checked) {
            checkableImageButton.isChecked = checked
        }
    }

    @JvmStatic
    @BindingAdapter(
        value = ["android:onCheckedChanged", "android:checkedAttrChanged"],
        requireAll = false
    )
    fun setListeners(
        checkableImageButton: CheckableImageButton,
        listener: CheckableImageButton.OnCheckedChangeListener?,
        attrChange: InverseBindingListener?
    ) {
        if (attrChange == null) {
            checkableImageButton.setOnCheckedChangeListener(listener)
        } else {
            checkableImageButton.setOnCheckedChangeListener(object : CheckableImageButton.OnCheckedChangeListener {
                override fun onCheckedChanged(button: CheckableImageButton?, isChecked: Boolean) {
                    listener?.onCheckedChanged(button, isChecked)
                    attrChange.onChange()
                }
            })
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "android:checked", event = "android:checkedAttrChanged")
    fun captureCheckAble(checkableImageButton: CheckableImageButton): Boolean {
        return checkableImageButton.isChecked
    }
}