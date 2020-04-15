package com.droid47.petfriend.home.presentation.components

import android.view.View
import androidx.annotation.FloatRange
import com.droid47.petfriend.base.extensions.normalize

interface OnSandwichSlideAction {

    fun onSlide(
        @FloatRange(
            from = 0.0,
            fromInclusive = true,
            to = 1.0,
            toInclusive = true
        ) slideOffset: Float
    )
}

class HalfCounterClockwiseRotateSlideAction(
    private val view: View
) : OnSandwichSlideAction {
    override fun onSlide(slideOffset: Float) {
        view.rotation = slideOffset.normalize(
            0F,
            1F,
            180F,
            0F
        )
    }
}