package com.droid47.petfriend.base.extensions

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

private var screenHeight = 0
private var screenWidth = 0

fun getScreenHeight(context: Context): Int {
    if (screenHeight == 0) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenHeight = size.y
    }
    return screenHeight
}


fun getScreenWidth(context: Context): Int {
    if (screenWidth == 0) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
    }
    return screenWidth
}