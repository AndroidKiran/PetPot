package com.droid47.petfriend.base.extensions

import android.content.Context

fun Context.dp2px(dipValue: Float): Int {
    val m = resources.displayMetrics.density
    return (dipValue * m + 0.5f).toInt()
}