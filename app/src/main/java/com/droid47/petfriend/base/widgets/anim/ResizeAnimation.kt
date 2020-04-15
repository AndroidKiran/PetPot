package com.droid47.petfriend.base.widgets.anim

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class ResizeAnimation(view: View, targetHeight: Int) : Animation() {
    private val startHeight: Int = view.height
    private val targetHeight: Int = targetHeight
    var view: View = view
    override fun applyTransformation(
        interpolatedTime: Float,
        transformation: Transformation?
    ) {
        val newHeight = (startHeight + (targetHeight - startHeight) * interpolatedTime).toInt()
        view.layoutParams.height = newHeight
        view.requestLayout()
    }

    override fun initialize(
        width: Int,
        height: Int,
        parentWidth: Int,
        parentHeight: Int
    ) {
        super.initialize(width, height, parentWidth, parentHeight)
    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}