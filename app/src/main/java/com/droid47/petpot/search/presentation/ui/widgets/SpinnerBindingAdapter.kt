package com.droid47.petpot.search.presentation.ui.widgets

import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.droid47.petpot.R

@BindingAdapter(value = ["entries"])
fun PetSpinner.bindSpinner(list: List<String>?) {
    adapter = ArrayAdapter(
        context,
        R.layout.spinner_list_style,
        list?.toTypedArray() ?: emptyArray()
    ).apply {
        setDropDownViewResource(R.layout.spinner_dropdown_item)
    }
    setSelection(selectedItemPosition)
    (adapter as? ArrayAdapter<*>)?.notifyDataSetChanged()
}

@BindingAdapter(value = ["selectedValue", "selectedValueAttrChanged"], requireAll = false)
fun PetSpinner.bindSpinnerData(
    newSelectedValue: String?,
    newTextAttrChanged: InverseBindingListener?
) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                newTextAttrChanged?.onChange()
                setSelection(position)
                onPetSelected(selectedItem)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }

//        if (newSelectedValue != null) {
//            val pos =
//                (appCompatSpinner.adapter as ArrayAdapter<String>).getPosition(newSelectedValue)
//        }
}

@InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
fun PetSpinner.captureSelectedValue(): String {
    return selectedItem as String
}
