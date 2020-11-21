package com.droid47.petpot.base.widgets.anim

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class ResizeAnimation(view: View, targetHeight: Int) : Animation() {
    private val startHeight: Int = view.height
    private val targetHeight: Int = targetHeight
    val view: View = view
    override fun applyTransformation(
        interpolatedTime: Float,
        transformation: Transformation?
    ) {
        val newHeight = (startHeight + (targetHeight - startHeight) * interpolatedTime).toInt()
        view.layoutParams.height = newHeight
        view.requestLayout()
    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}