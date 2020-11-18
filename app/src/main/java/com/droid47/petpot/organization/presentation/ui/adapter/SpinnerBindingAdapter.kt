package com.droid47.petpot.organization.presentation.ui.adapter

import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.droid47.petpot.R
import com.droid47.petpot.organization.data.models.State


@BindingAdapter(value = ["stateEntries"])
fun AppCompatSpinner.bindAppSpinner(list: List<State>?) {
    val stateStrList: List<String> = list?.mapNotNull { state -> state.name } ?: emptyList()
    adapter = ArrayAdapter(
        context,
        R.layout.spinner_list_style,
        stateStrList.toTypedArray()
    ).apply {
        setDropDownViewResource(R.layout.spinner_dropdown_item)
    }
    setSelection(selectedItemPosition)
    (adapter as? ArrayAdapter<*>)?.notifyDataSetChanged()
}

@BindingAdapter(value = ["selectedValue", "selectedValueAttrChanged"], requireAll = false)
fun AppCompatSpinner.bindSpinnerData(
    newSelectedValue: Int,
    newTextAttrChanged: InverseBindingListener
) {
    onItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            newTextAttrChanged.onChange()
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
    setSelection(newSelectedValue, true)

}

@InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
fun AppCompatSpinner.captureSelectedValue(): Int {
    return selectedItemPosition
}
