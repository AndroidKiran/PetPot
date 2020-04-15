package com.droid47.petfriend.base.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.forEach
import com.airbnb.lottie.LottieAnimationView
import com.droid47.petfriend.R
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout

@Suppress("DEPRECATION")
fun TextView.setTextAppearanceCompat(context: Context, resId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        setTextAppearance(resId)
    } else {
        setTextAppearance(context, resId)
    }
}

/**
 * Search this view and any children for a [ColorDrawable] `background` and return it's `color`,
 * else return `colorSurface`.
 */
@ColorInt
fun View.descendantBackgroundColor(): Int {
    val bg = backgroundColor()
    if (bg != null) {
        return bg
    } else if (this is ViewGroup) {
        forEach {
            val childBg = descendantBackgroundColorOrNull()
            if (childBg != null) {
                return childBg
            }
        }
    }
    return context.themeColor(R.attr.colorSurface)
}

@ColorInt
private fun View.descendantBackgroundColorOrNull(): Int? {
    val bg = backgroundColor()
    if (bg != null) {
        return bg
    } else if (this is ViewGroup) {
        forEach {
            val childBg = backgroundColor()
            if (childBg != null) {
                return childBg
            }
        }
    }
    return null
}

/**
 * Check if this [View]'s `background` is a [ColorDrawable] and if so, return it's `color`,
 * otherwise `null`.
 */
@ColorInt
fun View.backgroundColor(): Int? {
    val bg = background
    if (bg is ColorDrawable) {
        return bg.color
    } else {
        val tint = backgroundTintList?.defaultColor
        if (tint != null && tint != -1) return tint
    }
    return null
}

/**
 * Walk up from a [View] looking for an ancestor with a given `id`.
 */
fun View.findAncestorById(@IdRes ancestorId: Int): View {
    return when {
        id == ancestorId -> this
        parent is View -> (parent as View).findAncestorById(ancestorId)
        else -> throw IllegalArgumentException("$ancestorId not a valid ancestor")
    }
}

/**
 * A copy of the KTX method, adding the ability to add extra padding the bottom of the [Bitmap];
 * useful when it will be used in a [android.graphics.BitmapShader] with
 * a [android.graphics.Shader.TileMode.CLAMP][CLAMP tile mode].
 */
fun View.drawToBitmap(@Px extraPaddingBottom: Int = 0): Bitmap {
    check(ViewCompat.isLaidOut(this)) {
        "View needs to be laid out before calling drawToBitmap()"
    }
    return Bitmap.createBitmap(width, height + extraPaddingBottom, ARGB_8888).applyCanvas {
        translate(-scrollX.toFloat(), -scrollY.toFloat())
        draw(this)
    }
}


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.showOrHide(show: Boolean) {
    if (show) this.visible()
    else this.gone()
}

fun ShimmerFrameLayout.initHmeShimmer() {
    val shimmerBuilder = Shimmer.AlphaHighlightBuilder().apply {
        setDuration(1200L)
        setDirection(Shimmer.Direction.TOP_TO_BOTTOM)
        setIntensity(0.35f)
        setHighlightAlpha(1.0f)
    }.build()
    setShimmer(shimmerBuilder)
}

fun ShimmerFrameLayout.showAndStartShimmer() {
    startShimmer()
    visible()
}

fun ShimmerFrameLayout.stopAndHideShimmer() {
    stopShimmer()
    gone()
}

fun LottieAnimationView.playLottie() {
    if (!this.isAnimating) {
        this.playAnimation()
    }
}

fun LottieAnimationView.pauseLottie() {
    if (this.isAnimating) {
        this.pauseAnimation()
    }
}

fun navigationItemBackground(context: Context): Drawable? {
    // Need to inflate the drawable and CSL via AppCompatResources to work on Lollipop
    var background =
        AppCompatResources.getDrawable(context, R.drawable.navigation_item_background)
    if (background != null) {
        val tint = AppCompatResources.getColorStateList(
            context, R.color.navigation_item_background_tint
        )
        background = DrawableCompat.wrap(background.mutate())
        background.setTintList(tint)
    }
    return background
}

fun View.updateWidth(width: Int) {
    layoutParams = layoutParams.apply {
        this.width = width
    }
}

fun View.updateHeight(height: Int) {
    layoutParams = layoutParams.apply {
        this.height = height
    }
}
