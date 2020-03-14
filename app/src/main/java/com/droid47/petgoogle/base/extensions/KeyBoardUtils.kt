package com.droid47.petgoogle.base.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.hideKeyboard(context: Context?) {
    if (context == null) return
    val imm = context.getSystemService(
        Context.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.hideKeyboard() {
    hideKeyboard(context)
}

fun Activity.hideKeyboard() {
    val view = currentFocus ?: return
    view.hideKeyboard(this)
}

fun View.showKeyboard() {
    showKeyboard(context)
}

fun View.showKeyboard(context: Context?) {
    if (context == null)
        return
    val imm = context.getSystemService(
        Context.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    requestFocus()
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}