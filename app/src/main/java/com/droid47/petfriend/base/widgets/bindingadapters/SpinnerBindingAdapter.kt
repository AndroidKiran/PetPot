package com.droid47.petfriend.base.widgets.bindingadapters

import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.log
import com.droid47.petfriend.search.presentation.ui.widgets.PetSpinner

object SpinnerBindingAdapter {

    @JvmStatic
    @BindingAdapter(value = ["entries"])
    fun bindSpinner(appCompatSpinner: PetSpinner, list: List<String>?) {
        val context = appCompatSpinner.context
        appCompatSpinner.adapter = ArrayAdapter(
            context,
            R.layout.spinner_list_style,
            list?.toTypedArray() ?: emptyArray()
        ).apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
        }
        appCompatSpinner.setSelection(appCompatSpinner.selectedItemPosition)
        (appCompatSpinner.adapter as? ArrayAdapter<String>)?.notifyDataSetChanged()
    }

    @JvmStatic
    @BindingAdapter(value = ["selectedValue", "selectedValueAttrChanged"], requireAll = false)
    fun bindSpinnerData(
        appCompatSpinner: PetSpinner,
        newSelectedValue: String?,
        newTextAttrChanged: InverseBindingListener
    ) {
        appCompatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.selectedItem as? String ?: return
                if (!TextUtils.isEmpty(selectedItem)
                    && !selectedItem.equals(newSelectedValue, true)
                ) {
                    newTextAttrChanged.onChange()
                    appCompatSpinner.setSelection(position)
                    appCompatSpinner.onPetSelected(selectedItem)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

//        if (newSelectedValue != null) {
//            val pos =
//                (appCompatSpinner.adapter as ArrayAdapter<String>).getPosition(newSelectedValue)
//        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
    fun captureSelectedValue(appCompatSpinner: AppCompatSpinner): String {
        return appCompatSpinner.selectedItem as String
    }

}