package com.droid47.petpot.base.widgets.discreterRecyclerView

import android.graphics.Point

interface Helper {
    fun getViewEnd(recyclerWidth: Int, recyclerHeight: Int): Int
    fun getDistanceToChangeCurrent(childWidth: Int, childHeight: Int): Int
    fun setCurrentViewCenter(
        recyclerCenter: Point,
        scrolled: Int,
        outPoint: Point
    )

    fun shiftViewCenter(
        direction: Direction,
        shiftAmount: Int,
        outCenter: Point
    )

    fun getFlingVelocity(velocityX: Int, velocityY: Int): Int
    fun getPendingDx(pendingScroll: Int): Int
    fun getPendingDy(pendingScroll: Int): Int
    fun offsetChildren(amount: Int, helper: RecyclerViewProxy)
    fun getDistanceFromCenter(
        center: Point,
        viewCenterX: Int,
        viewCenterY: Int
    ): Float

    fun isViewVisible(
        center: Point,
        halfWidth: Int,
        halfHeight: Int,
        endBound: Int,
        extraSpace: Int
    ): Boolean

    fun hasNewBecomeVisible(lm: DiscreteScrollLayoutManager): Boolean
    fun canScrollVertically(): Boolean
    fun canScrollHorizontally(): Boolean
}
