package com.droid47.petpot.base.widgets.discreterRecyclerView

import android.graphics.Point
import android.view.View

enum class DSVOrientation {

    HORIZONTAL {
        override fun createHelper(): Helper {
            return HorizontalHelper()
        }
    },
    VERTICAL {
        override fun createHelper(): Helper {
            return VerticalHelper()
        }
    };

    //Package private
    abstract fun createHelper(): Helper

    class HorizontalHelper : Helper {
        override fun getViewEnd(recyclerWidth: Int, recyclerHeight: Int): Int {
            return recyclerWidth
        }

        override fun getDistanceToChangeCurrent(childWidth: Int, childHeight: Int): Int {
            return childWidth
        }

        override fun setCurrentViewCenter(
            recyclerCenter: Point,
            scrolled: Int,
            outPoint: Point
        ) {
            val newX = recyclerCenter.x - scrolled
            outPoint[newX] = recyclerCenter.y
        }

        override fun shiftViewCenter(
            direction: Direction,
            shiftAmount: Int,
            outCenter: Point
        ) {
            val newX = outCenter.x + direction.applyTo(shiftAmount)
            outCenter[newX] = outCenter.y
        }

        override fun isViewVisible(
            center: Point, halfWidth: Int, halfHeight: Int, endBound: Int,
            extraSpace: Int
        ): Boolean {
            val viewLeft = center.x - halfWidth
            val viewRight = center.x + halfWidth
            return viewLeft < endBound + extraSpace && viewRight > -extraSpace
        }

        override fun hasNewBecomeVisible(lm: DiscreteScrollLayoutManager): Boolean {
            val firstChild = lm.getFirstChild() ?: return false
            val lastChild = lm.getLastChild() ?: return false
            val leftBound: Int = -lm.getExtraLayoutSpace()
            val rightBound: Int = lm.width + lm.getExtraLayoutSpace()
            val isNewVisibleFromLeft = (lm.getDecoratedLeft(firstChild) > leftBound
                    && lm.getPosition(firstChild) > 0)
            val isNewVisibleFromRight =
                (lm.getDecoratedRight(lastChild) < rightBound
                        && lm.getPosition(lastChild) < lm.getItemCount() - 1)
            return isNewVisibleFromLeft || isNewVisibleFromRight
        }

        override fun offsetChildren(amount: Int, helper: RecyclerViewProxy) {
            helper.offsetChildrenHorizontal(amount)
        }

        override fun getDistanceFromCenter(
            center: Point,
            viewCenterX: Int,
            viewCenterY: Int
        ): Float {
            return (viewCenterX - center.x).toFloat()
        }

        override fun getFlingVelocity(velocityX: Int, velocityY: Int): Int {
            return velocityX
        }

        override fun canScrollHorizontally(): Boolean {
            return true
        }

        override fun canScrollVertically(): Boolean {
            return false
        }

        override fun getPendingDx(pendingScroll: Int): Int {
            return pendingScroll
        }

        override fun getPendingDy(pendingScroll: Int): Int {
            return 0
        }
    }

    class VerticalHelper : Helper {
        override fun getViewEnd(recyclerWidth: Int, recyclerHeight: Int): Int {
            return recyclerHeight
        }

        override fun getDistanceToChangeCurrent(childWidth: Int, childHeight: Int): Int {
            return childHeight
        }

        override fun setCurrentViewCenter(
            recyclerCenter: Point,
            scrolled: Int,
            outPoint: Point
        ) {
            val newY = recyclerCenter.y - scrolled
            outPoint[recyclerCenter.x] = newY
        }

        override fun shiftViewCenter(
            direction: Direction,
            shiftAmount: Int,
            outCenter: Point
        ) {
            val newY = outCenter.y + direction.applyTo(shiftAmount)
            outCenter[outCenter.x] = newY
        }

        override fun offsetChildren(amount: Int, helper: RecyclerViewProxy) {
            helper.offsetChildrenVertical(amount)
        }

        override fun getDistanceFromCenter(
            center: Point,
            viewCenterX: Int,
            viewCenterY: Int
        ): Float {
            return (viewCenterY - center.y).toFloat()
        }

        override fun isViewVisible(
            center: Point, halfWidth: Int, halfHeight: Int, endBound: Int,
            extraSpace: Int
        ): Boolean {
            val viewTop = center.y - halfHeight
            val viewBottom = center.y + halfHeight
            return viewTop < endBound + extraSpace && viewBottom > -extraSpace
        }

        override fun hasNewBecomeVisible(lm: DiscreteScrollLayoutManager): Boolean {
            val firstChild: View = lm.getFirstChild() ?: return false
            val lastChild: View = lm.getLastChild() ?: return false
            val topBound: Int = -lm.getExtraLayoutSpace()
            val bottomBound: Int = lm.height + lm.getExtraLayoutSpace()
            val isNewVisibleFromTop = (lm.getDecoratedTop(firstChild) > topBound
                    && lm.getPosition(firstChild) > 0)
            val isNewVisibleFromBottom =
                (lm.getDecoratedBottom(lastChild) < bottomBound
                        && lm.getPosition(lastChild) < lm.itemCount - 1)
            return isNewVisibleFromTop || isNewVisibleFromBottom
        }

        override fun getFlingVelocity(velocityX: Int, velocityY: Int): Int {
            return velocityY
        }

        override fun canScrollHorizontally(): Boolean {
            return false
        }

        override fun canScrollVertically(): Boolean {
            return true
        }

        override fun getPendingDx(pendingScroll: Int): Int {
            return 0
        }

        override fun getPendingDy(pendingScroll: Int): Int {
            return pendingScroll
        }
    }
}