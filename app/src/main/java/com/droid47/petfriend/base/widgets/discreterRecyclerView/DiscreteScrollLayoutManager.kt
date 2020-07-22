package com.droid47.petfriend.base.widgets.discreterRecyclerView

import android.content.Context
import android.graphics.Point
import android.graphics.PointF
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.droid47.petfriend.base.widgets.discreterRecyclerView.transform.DiscreteScrollItemTransformer
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val EXTRA_POSITION = "extra_position"
private const val DEFAULT_TIME_FOR_ITEM_SETTLE = 400
private const val DEFAULT_FLING_THRESHOLD = 100 //Decrease to increase sensitivity.
private const val DEFAULT_TRANSFORM_CLAMP_ITEM_COUNT = 1
private const val NO_POSITION = -1
private const val SCROLL_TO_SNAP_TO_ANOTHER_ITEM = 0.6f

class DiscreteScrollLayoutManager(
    private val context: Context,
    private val scrollStateListener: ScrollStateListener,
    private val orientation: DSVOrientation
) : RecyclerView.LayoutManager() {

    private var itemTransformer: DiscreteScrollItemTransformer? = null
    private var timeForItemSettle = DEFAULT_TIME_FOR_ITEM_SETTLE
    private var pendingPosition = NO_POSITION
    private var currentPosition = NO_POSITION
    private var flingThreshold = DEFAULT_FLING_THRESHOLD
    private var shouldSlideOnFling = false
    private val recyclerCenter = Point()
    private val currentViewCenter = Point()
    private val viewCenterIterator = Point()
    private val detachedCache = SparseArray<View>()
    private var orientationHelper = orientation.createHelper()
    private val recyclerViewProxy = RecyclerViewProxy(this)
    private var transformClampItemCount = DEFAULT_TRANSFORM_CLAMP_ITEM_COUNT
    private var pendingScroll = 0
    private var scrolled = 0
    private var viewWidth = 0
    private var viewHeight = 0
    private var isFirstOrEmptyLayout = false
    private var childHalfWidth = 0
    private var childHalfHeight = 0
    private var extraLayoutSpace = 0
    private var scrollToChangeCurrent = 0
    private var offscreenItems = 0
    private var dataSetChangeShiftedPosition = false
    private var currentScrollState = RecyclerView.SCROLL_STATE_IDLE

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (state.itemCount == 0) {
            recyclerViewProxy.removeAndRecycleAllViews(recycler)
            pendingPosition = NO_POSITION
            currentPosition = pendingPosition
            pendingScroll = 0
            scrolled = pendingScroll
            return
        }
        ensureValidPosition(state)
        updateRecyclerDimensions(state)

        //onLayoutChildren may be called multiple times and this check is required so that the flag
        //won't be cleared until onLayoutCompleted

        //onLayoutChildren may be called multiple times and this check is required so that the flag
        //won't be cleared until onLayoutCompleted
        if (!isFirstOrEmptyLayout) {
            isFirstOrEmptyLayout = recyclerViewProxy.getChildCount() == 0
            if (isFirstOrEmptyLayout) {
                initChildDimensions(recycler)
            }
        }

        recyclerViewProxy.detachAndScrapAttachedViews(recycler)
        fill(recycler)
        applyItemTransformToChildren()
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        if (isFirstOrEmptyLayout) {
            scrollStateListener.onCurrentViewFirstLayout()
            isFirstOrEmptyLayout = false
        } else if (dataSetChangeShiftedPosition) {
            scrollStateListener.onDataSetChangeChangedPosition()
            dataSetChangeShiftedPosition = false
        }
    }

    override fun onItemsAdded(
        recyclerView: RecyclerView,
        positionStart: Int,
        itemCount: Int
    ) {
        var newPosition = currentPosition
        if (currentPosition == NO_POSITION) {
            newPosition = 0
        } else if (currentPosition >= positionStart) {
            newPosition = Math.min(
                currentPosition + itemCount,
                recyclerViewProxy.getItemCount() - 1
            )
        }
        onNewPosition(newPosition)
    }

    override fun onItemsRemoved(
        recyclerView: RecyclerView,
        positionStart: Int,
        itemCount: Int
    ) {
        var newPosition = currentPosition
        if (recyclerViewProxy.getItemCount() == 0) {
            newPosition = NO_POSITION
        } else if (currentPosition >= positionStart) {
            if (currentPosition < positionStart + itemCount) {
                //If currentPosition is in the removed items, then the new item became current
                currentPosition = NO_POSITION
            }
            newPosition = max(0, currentPosition - itemCount)
        }
        onNewPosition(newPosition)
    }

    override fun onItemsChanged(recyclerView: RecyclerView) {

        //notifyDataSetChanged() was called. We need to ensure that currentPosition is not out of bounds
        currentPosition = min(
            max(0, currentPosition),
            recyclerViewProxy.getItemCount() - 1
        )
        dataSetChangeShiftedPosition = true
    }

    override fun scrollToPosition(position: Int) {
        if (currentPosition == position) {
            return
        }
        currentPosition = position
        recyclerViewProxy.requestLayout()
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        if (currentPosition == position || pendingPosition != NO_POSITION) {
            return
        }
        checkTargetPosition(state, position)
        if (currentPosition == NO_POSITION) {
            //Layout not happened yet
            currentPosition = position
        } else {
            startSmoothPendingScroll(position)
        }
    }

    override fun canScrollHorizontally(): Boolean {
        return orientationHelper.canScrollHorizontally()
    }

    override fun canScrollVertically(): Boolean {
        return orientationHelper.canScrollVertically()
    }

    override fun onScrollStateChanged(state: Int) {
        if (currentScrollState == RecyclerView.SCROLL_STATE_IDLE && currentScrollState != state) {
            scrollStateListener.onScrollStart()
        }
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            //Scroll is not finished until current view is centered
            val isScrollEnded: Boolean = onScrollEnd()
            if (isScrollEnded) {
                scrollStateListener.onScrollEnd()
            } else {
                //Scroll continues and we don't want to set currentScrollState to STATE_IDLE,
                //because this will then trigger .scrollStateListener.onScrollStart()
                return
            }
        } else if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
            onDragStart()
        }
        currentScrollState = state
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State?
    ): Int {
        return scrollBy(dx, recycler)
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State?
    ): Int {
        return scrollBy(dy, recycler)
    }

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        if (recyclerViewProxy.getChildCount() > 0) {
            val firstChild = getFirstChild() ?: return
            val lastChild = getLastChild() ?: return
            event.fromIndex = getPosition(firstChild)
            event.toIndex = getPosition(lastChild)
        }
    }

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }

    override fun computeVerticalScrollRange(state: RecyclerView.State): Int {
        return computeScrollRange(state)
    }

    override fun computeVerticalScrollOffset(state: RecyclerView.State): Int {
        return computeScrollOffset(state)
    }

    override fun computeVerticalScrollExtent(state: RecyclerView.State): Int {
        return computeScrollExtent(state)
    }

    override fun computeHorizontalScrollRange(state: RecyclerView.State): Int {
        return computeScrollRange(state)
    }

    override fun computeHorizontalScrollOffset(state: RecyclerView.State): Int {
        return computeScrollOffset(state)
    }

    override fun computeHorizontalScrollExtent(state: RecyclerView.State): Int {
        return computeScrollExtent(state)
    }

    override fun onAdapterChanged(
        oldAdapter: RecyclerView.Adapter<*>?,
        newAdapter: RecyclerView.Adapter<*>?
    ) {
        pendingPosition = NO_POSITION
        pendingScroll = 0
        scrolled = pendingScroll
        currentPosition = if (newAdapter is InitialPositionProvider) {
            (newAdapter as InitialPositionProvider).getInitialPosition()
        } else {
            0
        }
        recyclerViewProxy.removeAllViews()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        if (pendingPosition != NO_POSITION) {
            currentPosition = pendingPosition
        }
        bundle.putInt(EXTRA_POSITION, currentPosition)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as Bundle
        currentPosition = bundle.getInt(EXTRA_POSITION)
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    fun setItemTransformer(itemTransformer: DiscreteScrollItemTransformer) {
        this.itemTransformer = itemTransformer
    }

    fun getExtraLayoutSpace(): Int {
        return extraLayoutSpace
    }

    fun getNextPosition(): Int {
        return when {
            scrolled == 0 -> {
                currentPosition
            }
            pendingPosition != NO_POSITION -> {
                pendingPosition
            }
            else -> {
                currentPosition + Direction.fromDelta(scrolled).applyTo(1)
            }
        }
    }

    fun setTimeForItemSettle(timeForItemSettle: Int) {
        this.timeForItemSettle = timeForItemSettle
    }

    fun setOffscreenItems(offscreenItems: Int) {
        this.offscreenItems = offscreenItems
        extraLayoutSpace = scrollToChangeCurrent * offscreenItems
        recyclerViewProxy.requestLayout()
    }

    fun setTransformClampItemCount(transformClampItemCount: Int) {
        this.transformClampItemCount = transformClampItemCount
        applyItemTransformToChildren()
    }

    fun setOrientation(orientation: DSVOrientation) {
        orientationHelper = orientation.createHelper()
        recyclerViewProxy.removeAllViews()
        recyclerViewProxy.requestLayout()
    }

    fun setShouldSlideOnFling(result: Boolean) {
        shouldSlideOnFling = result
    }

    fun setSlideOnFlingThreshold(threshold: Int) {
        flingThreshold = threshold
    }

    fun getCurrentPosition(): Int {
        return currentPosition
    }

    private fun ensureValidPosition(state: RecyclerView.State) {
        if (currentPosition == NO_POSITION || currentPosition >= state.itemCount) {
            //currentPosition might have been assigned in onRestoreInstanceState()
            //which can lead to a crash (position out of bounds) when data set
            //is not persisted across rotations
            currentPosition = 0
        }
    }

    private fun updateRecyclerDimensions(state: RecyclerView.State) {
        val dimensionsChanged = (!state.isMeasuring
                && (recyclerViewProxy.getWidth() != viewWidth
                || recyclerViewProxy.getHeight() != viewHeight))
        if (dimensionsChanged) {
            viewWidth = recyclerViewProxy.getWidth()
            viewHeight = recyclerViewProxy.getHeight()
            recyclerViewProxy.removeAllViews()
        }
        recyclerCenter[recyclerViewProxy.getWidth() / 2] = recyclerViewProxy.getHeight() / 2
    }

    private fun initChildDimensions(recycler: RecyclerView.Recycler) {
        val viewToMeasure =
            recyclerViewProxy.getMeasuredChildForAdapterPosition(0, recycler)
        val childViewWidth = recyclerViewProxy.getMeasuredWidthWithMargin(viewToMeasure)
        val childViewHeight = recyclerViewProxy.getMeasuredHeightWithMargin(viewToMeasure)
        childHalfWidth = childViewWidth / 2
        childHalfHeight = childViewHeight / 2
        scrollToChangeCurrent = orientationHelper.getDistanceToChangeCurrent(
            childViewWidth,
            childViewHeight
        )
        extraLayoutSpace = scrollToChangeCurrent * offscreenItems
        recyclerViewProxy.detachAndScrapView(viewToMeasure, recycler)
    }

    private fun fill(recycler: RecyclerView.Recycler) {
        cacheAndDetachAttachedViews()
        orientationHelper.setCurrentViewCenter(recyclerCenter, scrolled, currentViewCenter)
        val endBound = orientationHelper.getViewEnd(
            recyclerViewProxy.getWidth(),
            recyclerViewProxy.getHeight()
        )

        //Layout current
        if (isViewVisible(currentViewCenter, endBound)) {
            layoutView(recycler, currentPosition, currentViewCenter)
        }

        //Layout items before the current item
        layoutViews(recycler, Direction.START, endBound)

        //Layout items after the current item
        layoutViews(recycler, Direction.END, endBound)
        recycleDetachedViewsAndClearCache(recycler)
    }

    private fun layoutViews(
        recycler: RecyclerView.Recycler,
        direction: Direction,
        endBound: Int
    ) {
        val positionStep = direction.applyTo(1)

        //Predictive layout is required when we are doing smooth fast scroll towards pendingPosition
        var noPredictiveLayoutRequired =
            (pendingPosition == NO_POSITION
                    || !direction.sameAs(pendingPosition - currentPosition))
        viewCenterIterator[currentViewCenter.x] = currentViewCenter.y
        var pos = currentPosition + positionStep
        while (isInBounds(pos)) {
            if (pos == pendingPosition) {
                noPredictiveLayoutRequired = true
            }
            orientationHelper.shiftViewCenter(direction, scrollToChangeCurrent, viewCenterIterator)
            if (isViewVisible(viewCenterIterator, endBound)) {
                layoutView(recycler, pos, viewCenterIterator)
            } else if (noPredictiveLayoutRequired) {
                break
            }
            pos += positionStep
        }
    }

    private fun layoutView(
        recycler: RecyclerView.Recycler,
        position: Int,
        viewCenter: Point
    ) {
        if (position < 0) return
        var v = detachedCache[position]
        if (v == null) {
            v = recyclerViewProxy.getMeasuredChildForAdapterPosition(position, recycler)
            recyclerViewProxy.layoutDecoratedWithMargins(
                v,
                viewCenter.x - childHalfWidth, viewCenter.y - childHalfHeight,
                viewCenter.x + childHalfWidth, viewCenter.y + childHalfHeight
            )
        } else {
            recyclerViewProxy.attachView(v)
            detachedCache.remove(position)
        }
    }

    private fun cacheAndDetachAttachedViews() {
        detachedCache.clear()
        for (i in 0 until recyclerViewProxy.getChildCount()) {
            val child = recyclerViewProxy.getChildAt(i)
            detachedCache.put(recyclerViewProxy.getPosition(child!!), child)
        }
        for (i in 0 until detachedCache.size()) {
            recyclerViewProxy.detachView(detachedCache.valueAt(i))
        }
    }

    private fun recycleDetachedViewsAndClearCache(recycler: RecyclerView.Recycler) {
        for (i in 0 until detachedCache.size()) {
            val viewToRemove = detachedCache.valueAt(i)
            recyclerViewProxy.recycleView(viewToRemove, recycler)
        }
        detachedCache.clear()
    }

    private fun isInBounds(itemPosition: Int): Boolean {
        return itemPosition >= 0 && itemPosition < recyclerViewProxy.getItemCount()
    }

    private fun isViewVisible(
        viewCenter: Point,
        endBound: Int
    ): Boolean {
        return orientationHelper.isViewVisible(
            viewCenter, childHalfWidth, childHalfHeight,
            endBound, extraLayoutSpace
        )
    }

    private fun applyItemTransformToChildren() {
        itemTransformer?.let {
            val clampAfterDistance = scrollToChangeCurrent * transformClampItemCount
            for (i in 0 until recyclerViewProxy.getChildCount()) {
                val child = recyclerViewProxy.getChildAt(i) ?: return
                val position: Float = getCenterRelativePositionOf(child, clampAfterDistance)
                it.transformItem(child, position)
            }
        }
    }

    private fun getCenterRelativePositionOf(
        v: View,
        maxDistance: Int
    ): Float {
        val distanceFromCenter = orientationHelper.getDistanceFromCenter(
            recyclerCenter,
            getDecoratedLeft(v) + childHalfWidth,
            getDecoratedTop(v) + childHalfHeight
        )
        return min(max(-1f, distanceFromCenter / maxDistance), 1f)
    }

    private fun scrollBy(amount: Int, recycler: RecyclerView.Recycler): Int {
        if (recyclerViewProxy.getChildCount() == 0) {
            return 0
        }
        val direction = Direction.fromDelta(amount)
        val leftToScroll: Int = calculateAllowedScrollIn(direction)
        if (leftToScroll <= 0) {
            return 0
        }
        val delta =
            direction.applyTo(min(leftToScroll, abs(amount)))
        scrolled += delta
        if (pendingScroll != 0) {
            pendingScroll -= delta
        }
        orientationHelper.offsetChildren(-delta, recyclerViewProxy)
        if (orientationHelper.hasNewBecomeVisible(this)) {
            fill(recycler)
        }
        notifyScroll()
        applyItemTransformToChildren()
        return delta
    }

    private fun calculateAllowedScrollIn(direction: Direction): Int {
        if (pendingScroll != 0) {
            return abs(pendingScroll)
        }
        val allowedScroll: Int
        val isBoundReached: Boolean
        val isScrollDirectionAsBefore = direction.applyTo(scrolled) > 0
        if (direction == Direction.START && currentPosition == 0) {
            //We can scroll to the left when currentPosition == 0 only if we scrolled to the right before
            isBoundReached = scrolled == 0
            allowedScroll = if (isBoundReached) 0 else abs(scrolled)
        } else if (direction == Direction.END && currentPosition == recyclerViewProxy.getItemCount() - 1) {
            //We can scroll to the right when currentPosition == last only if we scrolled to the left before
            isBoundReached = scrolled == 0
            allowedScroll = if (isBoundReached) 0 else abs(scrolled)
        } else {
            isBoundReached = false
            allowedScroll =
                if (isScrollDirectionAsBefore) scrollToChangeCurrent - abs(scrolled) else scrollToChangeCurrent + abs(
                    scrolled
                )
        }
        scrollStateListener.onIsBoundReachedFlagChange(isBoundReached)
        return allowedScroll
    }

    private fun notifyScroll() {
        val amountToScroll =
            if (pendingPosition != NO_POSITION)
                abs(scrolled + pendingScroll)
            else
                scrollToChangeCurrent
        val position = -min(max(-1f, scrolled / amountToScroll.toFloat()), 1f)
        scrollStateListener.onScroll(position)
    }

    fun getFirstChild(): View? {
        return recyclerViewProxy.getChildAt(0)
    }

    fun getLastChild(): View? {
        return recyclerViewProxy.getChildAt(recyclerViewProxy.getChildCount() - 1)
    }

    private fun onNewPosition(position: Int) {
        if (currentPosition != position) {
            currentPosition = position
            dataSetChangeShiftedPosition = true
        }
    }

    private fun checkTargetPosition(
        state: RecyclerView.State,
        targetPosition: Int
    ) {
        require(!(targetPosition < 0 || targetPosition >= state.itemCount)) {
            String.format(
                Locale.US,
                "target position out of bounds: position=%d, itemCount=%d",
                targetPosition, state.itemCount
            )
        }
    }

    private fun startSmoothPendingScroll() {
        val scroller: LinearSmoothScroller =
            DiscreteLinearSmoothScroller(
                context
            )
        scroller.targetPosition = currentPosition
        recyclerViewProxy.startSmoothScroll(scroller)
    }

    private fun startSmoothPendingScroll(position: Int) {
        if (currentPosition == position) return
        pendingScroll = -scrolled
        val direction = Direction.fromDelta(position - currentPosition)
        val distanceToScroll =
            Math.abs(position - currentPosition) * scrollToChangeCurrent
        pendingScroll += direction.applyTo(distanceToScroll)
        pendingPosition = position
        startSmoothPendingScroll()
    }

    private fun onScrollEnd(): Boolean {
        if (pendingPosition != NO_POSITION) {
            currentPosition = pendingPosition
            pendingPosition = NO_POSITION
            scrolled = 0
        }
        val scrollDirection = Direction.fromDelta(scrolled)
        if (abs(scrolled) == scrollToChangeCurrent) {
            currentPosition += scrollDirection.applyTo(1)
            scrolled = 0
        }
        if (isAnotherItemCloserThanCurrent()) {
            pendingScroll = getHowMuchIsLeftToScroll(scrolled)
        } else {
            pendingScroll = -scrolled
        }
        return if (pendingScroll == 0) {
            true
        } else {
            startSmoothPendingScroll()
            false
        }
    }

    private fun getHowMuchIsLeftToScroll(dx: Int): Int {
        return Direction.fromDelta(dx).applyTo(scrollToChangeCurrent - Math.abs(scrolled))
    }

    private fun isAnotherItemCloserThanCurrent(): Boolean {
        return abs(scrolled) >= scrollToChangeCurrent * SCROLL_TO_SNAP_TO_ANOTHER_ITEM
    }

    private fun onDragStart() {
        //Here we need to:
        //1. Stop any pending scroll
        //2. Set currentPosition to position of the item that is closest to the center
        val isScrollingThroughMultiplePositions =
            Math.abs(scrolled) > scrollToChangeCurrent
        if (isScrollingThroughMultiplePositions) {
            val scrolledPositions = scrolled / scrollToChangeCurrent
            currentPosition += scrolledPositions
            scrolled -= scrolledPositions * scrollToChangeCurrent
        }
        if (isAnotherItemCloserThanCurrent()) {
            val direction = Direction.fromDelta(scrolled)
            currentPosition += direction.applyTo(1)
            scrolled = -getHowMuchIsLeftToScroll(scrolled)
        }
        pendingPosition = NO_POSITION
        pendingScroll = 0
    }

    private fun computeScrollRange(state: RecyclerView.State): Int {
        return if (itemCount == 0) {
            0
        } else {
            scrollToChangeCurrent * (itemCount - 1)
        }
    }

    private fun computeScrollExtent(state: RecyclerView.State): Int {
        return if (itemCount == 0) {
            0
        } else {
            (computeScrollRange(state) / itemCount.toFloat()).toInt()
        }
    }

    private fun computeScrollOffset(state: RecyclerView.State): Int {
        val scrollbarSize = computeScrollExtent(state)
        val offset =
            (scrolled / scrollToChangeCurrent.toFloat() * scrollbarSize).toInt()
        return currentPosition * scrollbarSize + offset
    }

    inner class DiscreteLinearSmoothScroller(context: Context?) : LinearSmoothScroller(context) {

        override fun calculateDxToMakeVisible(view: View?, snapPreference: Int): Int {
            return orientationHelper.getPendingDx(-pendingScroll)
        }

        override fun calculateDyToMakeVisible(view: View?, snapPreference: Int): Int {
            return orientationHelper.getPendingDy(-pendingScroll)
        }

        override fun calculateTimeForScrolling(dx: Int): Int {
            val dist = min(abs(dx), scrollToChangeCurrent).toFloat()
            return (max(
                0.01f,
                dist / scrollToChangeCurrent
            ) * timeForItemSettle).toInt()
        }

        override fun computeScrollVectorForPosition(targetPosition: Int): PointF {
            return PointF(
                orientationHelper.getPendingDx(pendingScroll).toFloat(),
                orientationHelper.getPendingDy(pendingScroll).toFloat()
            )
        }
    }

    interface ScrollStateListener {
        fun onIsBoundReachedFlagChange(isBoundReached: Boolean)
        fun onScrollStart()
        fun onScrollEnd()
        fun onScroll(currentViewPosition: Float)
        fun onCurrentViewFirstLayout()
        fun onDataSetChangeChangedPosition()
    }

    interface InitialPositionProvider {
        fun getInitialPosition(): Int
    }
}