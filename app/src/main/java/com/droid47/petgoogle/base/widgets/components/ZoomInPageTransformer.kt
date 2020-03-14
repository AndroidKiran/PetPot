package com.droid47.petgoogle.base.widgets.components

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class ZoomInPageTransformer: ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val scale =
            if (position < 0) position + 1f else abs(1f - position)
        page.scaleX = scale
        page.scaleY = scale
        page.pivotX = page.width * 0.5f
        page.pivotY = page.height * 0.5f
        page.alpha = if (position < -1f || position > 1f) 0f else 1f - (scale - 1f)
    }
}