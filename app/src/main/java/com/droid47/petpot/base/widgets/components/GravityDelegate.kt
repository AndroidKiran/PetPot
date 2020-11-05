package com.droid47.petpot.base.widgets.components

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

internal class GravityDelegate(
    gravity: Int,
    enableSnapLast: Boolean,
    listener: GravitySnapHelper.SnapListener?
) {
    private var verticalHelper: OrientationHelper? = null
    private var horizontalHelper: OrientationHelper? = null
    private var gravity = 0
    private var isRtlHorizontal = false
    private var snapLastItem = false
    private var listener: GravitySnapHelper.SnapListener? = null
    private var snapping = false
    private val mScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == 2) {
                    snapping = false
                }
                if (newState == 0 && snapping && this@GravityDelegate.listener != null) {
                    val position = getSnappedPosition(recyclerView)
                    if (position != -1) {
                        this@GravityDelegate.listener?.onSnap(position)
                    }
                    snapping = false
                }
            }
        }

    fun attachToRecyclerView(recyclerView: RecyclerView?) {
        if (recyclerView != null) {
            recyclerView.onFlingListener = null
            if (gravity == 8388611 || gravity == 8388613) {
                isRtlHorizontal = isRtl
            }
            if (listener != null) {
                recyclerView.addOnScrollListener(mScrollListener)
            }
        }
    }

    private val isRtl: Boolean
        get() = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1

    fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        val out = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            if (gravity == 8388611) {
                out[0] =
                    distanceToStart(
                        targetView,
                        getHorizontalHelper(layoutManager), false
                    )
            } else {
                out[0] =
                    distanceToEnd(
                        targetView,
                        getHorizontalHelper(layoutManager), false
                    )
            }
        } else {
            out[0] = 0
        }
        if (layoutManager.canScrollVertically()) {
            if (gravity == 48) {
                out[1] =
                    distanceToStart(
                        targetView,
                        getVerticalHelper(layoutManager), false
                    )
            } else {
                out[1] =
                    distanceToEnd(targetView, getVerticalHelper(layoutManager), false)
            }
        } else {
            out[1] = 0
        }
        return out
    }

    fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        var snapView: View? = null
        if (layoutManager is LinearLayoutManager) {
            when (gravity) {
                48 -> snapView =
                    findStartView(layoutManager, getVerticalHelper(layoutManager))
                80 -> snapView =
                    findEndView(layoutManager, getVerticalHelper(layoutManager))
                8388611 -> snapView =
                    findStartView(layoutManager, getHorizontalHelper(layoutManager))
                8388613 -> snapView =
                    findEndView(layoutManager, getHorizontalHelper(layoutManager))
            }
        }
        snapping = snapView != null
        return snapView
    }

    fun enableLastItemSnap(snap: Boolean) {
        snapLastItem = snap
    }

    private fun distanceToStart(
        targetView: View,
        helper: OrientationHelper,
        fromEnd: Boolean
    ): Int {
        return if (isRtlHorizontal && !fromEnd) distanceToEnd(
            targetView,
            helper,
            true
        ) else helper.getDecoratedStart(targetView) - helper.startAfterPadding
    }

    private fun distanceToEnd(
        targetView: View,
        helper: OrientationHelper,
        fromStart: Boolean
    ): Int {
        return if (isRtlHorizontal && !fromStart) distanceToStart(
            targetView,
            helper,
            true
        ) else helper.getDecoratedEnd(targetView) - helper.endAfterPadding
    }

    private fun findStartView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        return if (layoutManager is LinearLayoutManager) {
            val reverseLayout = layoutManager.reverseLayout
            val firstChild =
                if (reverseLayout) layoutManager.findLastVisibleItemPosition() else layoutManager.findFirstVisibleItemPosition()
            var offset = 1
            if (layoutManager is GridLayoutManager) {
                offset += layoutManager.spanCount - 1
            }
            if (firstChild == -1) {
                null
            } else {
                val child = layoutManager.findViewByPosition(firstChild)
                val visibleWidth: Float
                visibleWidth = if (isRtlHorizontal) {
                    (helper.totalSpace - helper.getDecoratedStart(child)).toFloat() / helper.getDecoratedMeasurement(
                        child
                    ).toFloat()
                } else {
                    helper.getDecoratedEnd(child).toFloat() / helper.getDecoratedMeasurement(
                        child
                    ).toFloat()
                }
                val endOfList: Boolean = if (!reverseLayout) {
                    layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1
                } else {
                    layoutManager.findFirstCompletelyVisibleItemPosition() == 0
                }
                if (visibleWidth > 0.5f && !endOfList) {
                    child
                } else if (snapLastItem && endOfList) {
                    child
                } else if (endOfList) {
                    null
                } else {
                    if (reverseLayout) layoutManager.findViewByPosition(firstChild - offset) else layoutManager.findViewByPosition(
                        firstChild + offset
                    )
                }
            }
        } else {
            null
        }
    }

    private fun findEndView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        return if (layoutManager is LinearLayoutManager) {
            val reverseLayout = layoutManager.reverseLayout
            val lastChild =
                if (reverseLayout) layoutManager.findFirstVisibleItemPosition() else layoutManager.findLastVisibleItemPosition()
            var offset = 1
            if (layoutManager is GridLayoutManager) {
                offset += layoutManager.spanCount - 1
            }
            if (lastChild == -1) {
                null
            } else {
                val child = layoutManager.findViewByPosition(lastChild)
                val visibleWidth: Float
                visibleWidth = if (isRtlHorizontal) {
                    helper.getDecoratedEnd(child).toFloat() / helper.getDecoratedMeasurement(
                        child
                    ).toFloat()
                } else {
                    (helper.totalSpace - helper.getDecoratedStart(child)).toFloat() / helper.getDecoratedMeasurement(
                        child
                    ).toFloat()
                }
                val startOfList: Boolean = if (!reverseLayout) {
                    layoutManager.findFirstCompletelyVisibleItemPosition() == 0
                } else {
                    layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1
                }
                if (visibleWidth > 0.5f && !startOfList) {
                    child
                } else if (snapLastItem && startOfList) {
                    child
                } else if (startOfList) {
                    null
                } else {
                    if (reverseLayout) layoutManager.findViewByPosition(lastChild + offset) else layoutManager.findViewByPosition(
                        lastChild - offset
                    )
                }
            }
        } else {
            null
        }
    }

    private fun getSnappedPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            if (gravity == 8388611 || gravity == 48) {
                return layoutManager.findFirstCompletelyVisibleItemPosition()
            }
            if (gravity == 8388613 || gravity == 80) {
                return layoutManager.findLastCompletelyVisibleItemPosition()
            }
        }
        return -1
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        return verticalHelper ?: OrientationHelper.createVerticalHelper(layoutManager).also {
            verticalHelper = it
        }
    }

    private fun getHorizontalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        return horizontalHelper ?: OrientationHelper.createHorizontalHelper(layoutManager).also {
            verticalHelper = it
        }
    }

    init {
        require(!(gravity != 8388611 && gravity != 8388613 && gravity != 80 && gravity != 48)) { "Invalid gravity value. Use START | END | BOTTOM | TOP constants" }
        snapLastItem = enableSnapLast
        this.gravity = gravity
        this.listener = listener
    }
}