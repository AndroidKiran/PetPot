package com.droid47.petpot.base.widgets.snappy

import android.content.Context
import android.graphics.PointF
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class SnappySmoothScroller(context: Context) : LinearSmoothScroller(context) {
    private var snapType = SnapType.VISIBLE
    private var scrollVectorDetector: ScrollVectorDetector? = null
    private var snapInterpolator: Interpolator = DecelerateInterpolator()
    private var snapDuration = DEFAULT_SNAP_DURATION
    private var seekDuration = DEFAULT_SEEK_SCROLL_DURATION
    private var seekDistance: SeekDistance? = null
    private var snapPaddingStart = 0
    private var snapPaddingEnd = 0

    class Builder {
        private var snapType = SnapType.VISIBLE
        private var snapInterpolator: Interpolator = DecelerateInterpolator()
        private var snapDuration = -1
        private var seekDuration = -1
        private var snapPaddingStart = 0
        private var snapPaddingEnd = 0
        private var position = 0
        private var scrollVectorDetector: ScrollVectorDetector? = null

        fun setSnapType(snapType: SnapType): Builder {
            this.snapType = snapType
            return this
        }

        fun setSnapDuration(snapDuration: Int): Builder {
            this.snapDuration = snapDuration
            return this
        }

        fun setSnapInterpolator(snapInterpolator: Interpolator): Builder {
            this.snapInterpolator = snapInterpolator
            return this
        }

        fun setSnapPadding(snapPadding: Int): Builder {
            snapPaddingStart = snapPadding
            snapPaddingEnd = snapPadding
            return this
        }

        fun setSnapPaddingStart(snapPaddingStart: Int): Builder {
            this.snapPaddingStart = snapPaddingStart
            return this
        }

        fun setSnapPaddingEnd(snapPaddingEnd: Int): Builder {
            this.snapPaddingEnd = snapPaddingEnd
            return this
        }

        fun setSeekDuration(seekDuration: Int): Builder {
            this.seekDuration = seekDuration
            return this
        }

        fun setPosition(position: Int): Builder {
            this.position = position
            return this
        }

        fun setScrollVectorDetector(scrollVectorDetector: ScrollVectorDetector?): Builder {
            this.scrollVectorDetector = scrollVectorDetector
            return this
        }

        fun build(context: Context): SnappySmoothScroller {
            val scroller = SnappySmoothScroller(context)
            scroller.targetPosition = position
            if (scrollVectorDetector != null) {
                scroller.setScrollVectorDetector(scrollVectorDetector)
            }

            scroller.setSnapType(snapType)

            if (snapDuration >= 0) {
                scroller.setSnapDuration(snapDuration)
            }

            scroller.setSnapInterpolator(snapInterpolator)

            if (seekDuration >= 0) {
                scroller.setSeekDuration(seekDuration)
            }
            scroller.setSnapPaddingStart(snapPaddingStart)
            scroller.setSnapPaddingEnd(snapPaddingEnd)
            return scroller
        }
    }

    interface ScrollVectorDetector {
        fun computeScrollVectorForPosition(targetPosition: Int): PointF?
    }

    private class SeekDistance(
        val distanceInPixels: Float,
        val duration: Float
    )

    fun setSnapType(snapType: SnapType) {
        this.snapType = snapType
    }

    fun setSnapDuration(snapDuration: Int) {
        this.snapDuration = snapDuration
    }

    fun setScrollVectorDetector(scrollVectorDetector: ScrollVectorDetector?) {
        this.scrollVectorDetector = scrollVectorDetector
    }

    fun setSeekDuration(seekDuration: Int) {
        this.seekDuration = seekDuration
    }

    fun setSnapInterpolator(snapIterpolator: Interpolator) {
        snapInterpolator = snapIterpolator
    }

    fun setSnapPaddingStart(snapPaddingStart: Int) {
        this.snapPaddingStart = snapPaddingStart
    }

    fun setSnapPaddingEnd(snapPaddingEnd: Int) {
        this.snapPaddingEnd = snapPaddingEnd
    }

    override fun onTargetFound(targetView: View, state: RecyclerView.State, action: Action) {
        val dx = calculateDxToMakeVisible(targetView, horizontalSnapPreference)
        val dy = calculateDyToMakeVisible(targetView, verticalSnapPreference)
        action.update(-dx, -dy, snapDuration, snapInterpolator)
    }

    override fun onSeekTargetStep(
        dx: Int,
        dy: Int,
        state: RecyclerView.State,
        action: Action
    ) {
        if (seekDistance == null) {
            computeSeekDistance()
        }
        super.onSeekTargetStep(dx, dy, state, action)
    }

    private fun computeSeekDistance() {
        val layoutManager = layoutManager
        if (layoutManager != null && layoutManager.childCount > 0 && layoutManager.itemCount > 0
            && (layoutManager.canScrollHorizontally() || layoutManager.canScrollVertically())
        ) {
            val firstChild = layoutManager.getChildAt(0)
            if (firstChild != null) {
                val currentPosition = layoutManager.getPosition(firstChild)
                var totalWidth = 0
                var totalHeight = 0
                val count = layoutManager.childCount
                for (i in 0 until count) {
                    val child = layoutManager.getChildAt(i)
                    totalWidth += child?.width ?: 0
                    totalHeight += child?.height ?: 0
                }
                var distanceX = 0
                if (layoutManager.canScrollHorizontally()) {
                    val averageWidth = totalWidth / count
                    distanceX = abs((currentPosition - targetPosition) * averageWidth)
                }
                var distanceY = 0
                if (layoutManager.canScrollVertically()) {
                    val averageHeight = totalHeight / count
                    distanceY =
                        Math.abs((currentPosition - targetPosition) * averageHeight)
                }
                val distanceInPixels =
                    Math.sqrt(distanceX * distanceX + distanceY * distanceY.toDouble()).toInt()
                if (distanceInPixels > SEEK_MIN_DURATION) {
                    seekDistance = SeekDistance(distanceInPixels.toFloat(), seekDuration.toFloat())
                }
            }
        }
        if (seekDistance == null) {
            seekDistance = INVALID_SEEK_DISTANCE
        }
    }

    override fun calculateDtToFit(
        viewStart: Int,
        viewEnd: Int,
        boxStart: Int,
        boxEnd: Int,
        snapPreference: Int
    ): Int {
        return when (snapType) {
            SnapType.START -> boxStart - viewStart + snapPaddingStart
            SnapType.END -> boxEnd - viewEnd - snapPaddingEnd
            SnapType.CENTER -> {
                val boxDistance = boxEnd - boxStart
                val viewDistance = viewEnd - viewStart
                (boxDistance - viewDistance) / 2 - viewStart + boxStart
            }
            SnapType.VISIBLE -> {
                val dtStart = boxStart - viewStart + snapPaddingStart
                if (dtStart > 0) {
                    return dtStart
                }
                val dtEnd = boxEnd - viewEnd - snapPaddingEnd
                if (dtEnd < 0) {
                    dtEnd
                } else 0
            }
            else -> super.calculateDtToFit(
                viewStart,
                viewEnd,
                boxStart,
                boxEnd,
                snapPreference
            )
        }
    }

    override fun calculateDxToMakeVisible(view: View?, snapPreference: Int): Int {
        val dx = super.calculateDxToMakeVisible(view, snapPreference)
        if (dx == 0) {
            return dx
        }
        return when (snapType) {
            SnapType.START -> adjustDxForLeft(dx)
            SnapType.END -> adjustDxForRight(dx)
            SnapType.CENTER -> if (dx > 0) {
                adjustDxForRight(dx)
            } else {
                adjustDxForLeft(dx)
            }
            else -> dx
        }
    }

    override fun calculateDyToMakeVisible(view: View?, snapPreference: Int): Int {
        var dy = super.calculateDyToMakeVisible(view, snapPreference)
        if (dy == 0) {
            return dy
        }
        when (snapType) {
            SnapType.START -> dy = adjustDyForUp(dy)
            SnapType.END -> dy = adjustDyForDown(dy)
            SnapType.CENTER -> dy = if (dy > 0) {
                adjustDyForDown(dy)
            } else {
                adjustDyForUp(dy)
            }
            SnapType.VISIBLE -> {
            }
        }
        return dy
    }

    private fun adjustDxForLeft(dx: Int): Int {
        val layoutManager = layoutManager
        if (layoutManager == null || !layoutManager.canScrollHorizontally()) {
            return 0
        }
        val lastChild = layoutManager.getChildAt(layoutManager.childCount - 1) ?: return 0
        val position = layoutManager.getPosition(lastChild)
        if (position == layoutManager.itemCount - 1) {
            val params =
                lastChild.layoutParams as RecyclerView.LayoutParams
            val maxDx = (layoutManager.width - layoutManager.paddingRight
                    - (layoutManager.getDecoratedRight(lastChild) + params.rightMargin))
            if (dx < maxDx) {
                return maxDx
            }
        }
        return dx
    }

    private fun adjustDxForRight(dx: Int): Int {
        val layoutManager = layoutManager
        if (layoutManager == null || !layoutManager.canScrollHorizontally()) {
            return 0
        }
        val firstChild = layoutManager.getChildAt(0) ?: return 0
        val position = layoutManager.getPosition(firstChild)
        if (position == 0) {
            val params =
                firstChild.layoutParams as RecyclerView.LayoutParams
            val maxDx =
                -(layoutManager.getDecoratedLeft(firstChild) - params.leftMargin) + layoutManager.paddingLeft
            if (dx > maxDx) {
                return maxDx
            }
        }
        return dx
    }

    private fun adjustDyForUp(dy: Int): Int {
        val layoutManager = layoutManager
        if (layoutManager == null || !layoutManager.canScrollVertically()) {
            return 0
        }
        val lastChild = layoutManager.getChildAt(layoutManager.childCount - 1) ?: return 0
        val position = layoutManager.getPosition(lastChild)
        if (position == layoutManager.itemCount - 1) {
            val params =
                lastChild.layoutParams as RecyclerView.LayoutParams
            val maxDy = (layoutManager.height - layoutManager.paddingBottom
                    - (layoutManager.getDecoratedBottom(lastChild) + params.bottomMargin))
            if (dy < maxDy) {
                return maxDy
            }
        }
        return dy
    }

    private fun adjustDyForDown(dy: Int): Int {
        val layoutManager = layoutManager
        if (layoutManager == null || !layoutManager.canScrollVertically()) {
            return 0
        }
        val firstChild = layoutManager.getChildAt(0) ?: return 0
        val position = layoutManager.getPosition(firstChild)
        if (position == 0) {
            val params = firstChild.layoutParams as? RecyclerView.LayoutParams ?: return 0
            val maxDy =
                -(layoutManager.getDecoratedTop(firstChild) - params.topMargin) + layoutManager.paddingTop
            if (dy > maxDy) {
                return maxDy
            }
        }
        return dy
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        return if (scrollVectorDetector != null) {
            scrollVectorDetector!!.computeScrollVectorForPosition(targetPosition)
        } else {
            null
        }
    }

    override fun calculateTimeForScrolling(dx: Int): Int {
        val distanceInPixels = seekDistance?.distanceInPixels ?: 0f
        if (seekDistance != null && seekDistance != INVALID_SEEK_DISTANCE && distanceInPixels != 0f) {
            val proportion = dx.toFloat() / distanceInPixels
            val time = (seekDistance!!.duration * proportion).toInt()
            if (time > 0) {
                return time
            }
        }
        return super.calculateTimeForScrolling(dx)
    }

    companion object {
        private val INVALID_SEEK_DISTANCE =
            SeekDistance(0f, 0f)
        private const val SEEK_MIN_DURATION = 10000
        private const val DEFAULT_SEEK_SCROLL_DURATION = 500
        private const val DEFAULT_SNAP_DURATION = 600
    }
}