package com.droid47.petpot.base.extensions

import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.view.ViewCompat
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.viewpager.widget.ViewPager
import com.google.android.material.animation.ArgbEvaluatorCompat
import kotlinx.android.synthetic.main.onboarding_page_item.view.*
import kotlin.math.abs
import kotlin.math.roundToInt


private const val MIN_SCALE = 0.75f

fun lerp(
    startValue: Float,
    endValue: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Float {
    return startValue + fraction * (endValue - startValue)
}

fun lerp(
    startValue: Int,
    endValue: Int,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Int {
    return (startValue + fraction * (endValue - startValue)).roundToInt()
}

fun lerp(
    startValue: Float,
    endValue: Float,
    @FloatRange(
        from = 0.0,
        fromInclusive = true,
        to = 1.0,
        toInclusive = false
    ) startFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = false, to = 1.0, toInclusive = true) endFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Float {
    if (fraction < startFraction) return startValue
    if (fraction > endFraction) return endValue

    return lerp(
        startValue,
        endValue,
        (fraction - startFraction) / (endFraction - startFraction)
    )
}

fun lerp(
    startValue: Int,
    endValue: Int,
    @FloatRange(
        from = 0.0,
        fromInclusive = true,
        to = 1.0,
        toInclusive = false
    ) startFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = false, to = 1.0, toInclusive = true) endFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Int {
    if (fraction < startFraction) return startValue
    if (fraction > endFraction) return endValue

    return lerp(
        startValue,
        endValue,
        (fraction - startFraction) / (endFraction - startFraction)
    )
}

fun lerp(
    startValue: CornerRounding,
    endValue: CornerRounding,
    @FloatRange(
        from = 0.0,
        fromInclusive = true,
        to = 1.0,
        toInclusive = false
    ) startFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = false, to = 1.0, toInclusive = true) endFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): CornerRounding {
    if (fraction < startFraction) return startValue
    if (fraction > endFraction) return endValue

    return CornerRounding(
        lerp(
            startValue.topLeftRadius,
            endValue.topLeftRadius,
            startFraction,
            endFraction,
            fraction
        ),
        lerp(
            startValue.topRightRadius,
            endValue.topRightRadius,
            startFraction,
            endFraction,
            fraction
        ),
        lerp(
            startValue.bottomRightRadius,
            endValue.bottomRightRadius,
            startFraction,
            endFraction,
            fraction
        ),
        lerp(
            startValue.bottomLeftRadius,
            endValue.bottomLeftRadius,
            startFraction,
            endFraction,
            fraction
        )
    )
}

@ColorInt
fun lerpArgb(
    @ColorInt startColor: Int,
    @ColorInt endColor: Int,
    @FloatRange(
        from = 0.0,
        fromInclusive = true,
        to = 1.0,
        toInclusive = false
    ) startFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = false, to = 1.0, toInclusive = true) endFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Int {
    if (fraction < startFraction) return startColor
    if (fraction > endFraction) return endColor

    return ArgbEvaluatorCompat.getInstance().evaluate(
        (fraction - startFraction) / (endFraction - startFraction),
        startColor,
        endColor
    )
}

fun Float.normalize(
    inputMin: Float,
    inputMax: Float,
    outputMin: Float,
    outputMax: Float
): Float {
    if (this < inputMin) {
        return outputMin
    } else if (this > inputMax) {
        return outputMax
    }

    return outputMin * (1 - (this - inputMin) / (inputMax - inputMin)) +
            outputMax * ((this - inputMin) / (inputMax - inputMin))
}

fun View.setParallaxTransformation(position: Float) {
    apply {
        val parallaxView = this.img
        when {
            position < -1 -> // [-Infinity,-1)
                // This page is way off-screen to the left.
                alpha = 1f
            position <= 1 -> { // [-1,1]
                parallaxView.translationX = -position * (width / 2) //Half the normal speed
            }
            else -> // (1,+Infinity]
                // This page is way off-screen to the right.
                alpha = 1f
        }
    }
}

fun View.setScaleTransformation(position: Float, maxTranslateOffsetX: Int) {
    val viewPager = parent as ViewPager
    val leftInScreen: Int = left - viewPager.scrollX
    val centerXInViewPager: Int = leftInScreen + measuredWidth / 2
    val offsetX: Int = centerXInViewPager - viewPager.measuredWidth / 2
    val offsetRate: Float = offsetX.toFloat() * 0.38f / viewPager.measuredWidth
    val scaleFactor = 1 - abs(offsetRate)

    if (scaleFactor > 0) {
        scaleX = scaleFactor
        scaleY = scaleFactor
        translationX = -maxTranslateOffsetX * offsetRate
    }
    ViewCompat.setElevation(this, scaleFactor)
}


fun View.setZoomInTransformation(position: Float) {
    apply {
        val scale =
            if (position < 0) position + 1f else Math.abs(1f - position)
        scaleX = scale
        scaleY = scale
        pivotX = width * 0.5f
        pivotY = height * 0.5f
        alpha = if (position < -1f || position > 1f) 0f else 1f - (scale - 1f)
    }
}

fun View.setDepthPageTransformation(position: Float) {
    apply {
        val pageWidth: Int = width
        when {
            position < -1 -> { // [-Infinity,-1)
                // This page is way off-screen to the left.
                alpha = 0f
            }
            position <= 0 -> { // [-1,0]
                // Use the default slide transition when moving to the left page
                alpha = 1f
                translationX = 0f
                translationZ = 0f
                scaleX = 1f
                scaleY = 1f
            }
            position <= 1 -> { // (0,1]
                // Fade the page out.
                alpha = 1 - position

                // Counteract the default slide transition
                translationX = pageWidth * -position
                // Move it behind the left page
                translationZ = -1f

                // Scale the page down (between MIN_SCALE and 1)
                val scaleFactor: Float =
                    (MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(
                        position
                    )))
                scaleX = scaleFactor
                scaleY = scaleFactor
            }
            else -> { // (1,+Infinity]
                // This page is way off-screen to the right.
                alpha = 0f
            }
        }
    }
}

/**
 * A class which adds [DynamicAnimation.OnAnimationEndListener]s to the given `springs` and invokes
 * `onEnd` when all have finished.
 */
class MultiSpringEndListener(
    onEnd: (Boolean) -> Unit,
    vararg springs: SpringAnimation
) {
    private val listeners = ArrayList<DynamicAnimation.OnAnimationEndListener>(springs.size)

    private var wasCancelled = false

    init {
        springs.forEach {
            val listener = object : DynamicAnimation.OnAnimationEndListener {
                override fun onAnimationEnd(
                    animation: DynamicAnimation<out DynamicAnimation<*>>?,
                    canceled: Boolean,
                    value: Float,
                    velocity: Float
                ) {
                    animation?.removeEndListener(this)
                    wasCancelled = wasCancelled or canceled
                    listeners.remove(this)
                    if (listeners.isEmpty()) {
                        onEnd(wasCancelled)
                    }
                }
            }
            it.addEndListener(listener)
            listeners.add(listener)
        }
    }
}

fun listenForAllSpringsEnd(
    onEnd: (Boolean) -> Unit,
    vararg springs: SpringAnimation
) = MultiSpringEndListener(onEnd, *springs)
