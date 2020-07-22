package com.droid47.petfriend.base.widgets.discreterRecyclerView

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.droid47.petfriend.R
import com.droid47.petfriend.base.widgets.discreterRecyclerView.transform.DiscreteScrollItemTransformer
import com.droid47.petfriend.base.widgets.discreterRecyclerView.util.ScrollListenerAdapter

private val DEFAULT_ORIENTATION: Int = DSVOrientation.HORIZONTAL.ordinal

class DiscreteScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var onItemChangedListeners = mutableListOf<OnItemChangedListener>()
    private var scrollStateChangeListeners = mutableListOf<ScrollStateChangeListener>()
    private var isOverScrollEnabled: Boolean = false

    init {
        viewInit(attrs)
    }

    private fun viewInit(attrs: AttributeSet?) {
        onItemChangedListeners.clear()
        scrollStateChangeListeners.clear()
        var orientation: Int = DEFAULT_ORIENTATION
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DiscreteScrollView, 0, 0
        ).apply {
            try {
                orientation = getInt(
                    R.styleable.DiscreteScrollView_dsv_orientation,
                    DEFAULT_ORIENTATION
                )
            } finally {
                recycle()
            }
        }
        isOverScrollEnabled = overScrollMode != OVER_SCROLL_NEVER
        layoutManager = DiscreteScrollLayoutManager(
            context, ScrollChangeListener(),
            DSVOrientation.values()[orientation]
        )
    }

    private fun getDiscreteScrollLayoutManager(): DiscreteScrollLayoutManager? {
        return layoutManager as? DiscreteScrollLayoutManager
    }

    private inner class ScrollChangeListener : DiscreteScrollLayoutManager.ScrollStateListener {

        override fun onIsBoundReachedFlagChange(isBoundReached: Boolean) {
            if (isOverScrollEnabled) {
                overScrollMode = if (isBoundReached) OVER_SCROLL_ALWAYS else OVER_SCROLL_NEVER
            }
        }

        override fun onScrollStart() {
            if (scrollStateChangeListeners.isEmpty()) {
                return
            }
            val current: Int = getDiscreteScrollLayoutManager()?.getCurrentPosition() ?: return
            val holder: ViewHolder = getViewHolder(current) ?: return
            notifyScrollStart(holder, current)
        }

        override fun onScrollEnd() {
            if (onItemChangedListeners.isEmpty() && scrollStateChangeListeners.isEmpty()) {
                return
            }
            val current: Int = getDiscreteScrollLayoutManager()?.getCurrentPosition() ?: return
            val holder: ViewHolder = getViewHolder(current) ?: return
            notifyScrollEnd(holder, current)
            notifyCurrentItemChanged(holder, current)
        }

        override fun onScroll(currentViewPosition: Float) {
            if (scrollStateChangeListeners.isEmpty()) {
                return
            }
            val currentIndex: Int = getCurrentItem()
            val newIndex: Int = getDiscreteScrollLayoutManager()?.getNextPosition() ?: return
            val currentHolder = getViewHolder(currentIndex) ?: return
            val newHolder = getViewHolder(newIndex) ?: return
            if (currentIndex != newIndex) {
                notifyScroll(
                    currentViewPosition,
                    currentIndex, newIndex,
                    currentHolder,
                    newHolder
                )
            }
        }

        override fun onCurrentViewFirstLayout() {
            post { notifyCurrentItemChanged() }
        }

        override fun onDataSetChangeChangedPosition() {
            notifyCurrentItemChanged()
        }
    }

    fun getViewHolder(position: Int): ViewHolder? {
        val view = getDiscreteScrollLayoutManager()?.findViewByPosition(position) ?: return null
        return getChildViewHolder(view)
    }

    fun getCurrentItem(): Int {
        return getDiscreteScrollLayoutManager()?.getCurrentPosition() ?: 0
    }

    fun setItemTransformer(transformer: DiscreteScrollItemTransformer) {
        getDiscreteScrollLayoutManager()?.setItemTransformer(transformer)
    }

    fun setItemTransitionTimeMillis(@androidx.annotation.IntRange(from = 10) millis: Int) {
        getDiscreteScrollLayoutManager()?.setTimeForItemSettle(millis)
    }

    fun setSlideOnFling(result: Boolean) {
        getDiscreteScrollLayoutManager()?.setShouldSlideOnFling(result)
    }

    fun setSlideOnFlingThreshold(threshold: Int) {
        getDiscreteScrollLayoutManager()?.setSlideOnFlingThreshold(threshold)
    }

    fun setOrientation(orientation: DSVOrientation) {
        getDiscreteScrollLayoutManager()?.setOrientation(orientation)
    }

    fun setOffscreenItems(items: Int) {
        getDiscreteScrollLayoutManager()?.setOffscreenItems(items)
    }

    fun setClampTransformProgressAfter(@androidx.annotation.IntRange(from = 1) itemCount: Int) {
        require(itemCount > 1) { "must be >= 1" }
        getDiscreteScrollLayoutManager()?.setTransformClampItemCount(itemCount)
    }

    fun setOverScrollEnabled(overScrollEnabled: Boolean) {
        isOverScrollEnabled = overScrollEnabled
        overScrollMode = OVER_SCROLL_NEVER
    }

    fun addScrollStateChangeListener(scrollStateChangeListener: ScrollStateChangeListener) {
        scrollStateChangeListeners.add(scrollStateChangeListener)
    }

    fun addScrollListener(scrollListener: ScrollListener) {
        addScrollStateChangeListener(ScrollListenerAdapter(scrollListener))
    }

    fun addOnItemChangedListener(onItemChangedListener: OnItemChangedListener) {
        onItemChangedListeners.add(onItemChangedListener)
    }

    fun removeScrollStateChangeListener(scrollStateChangeListener: ScrollStateChangeListener) {
        scrollStateChangeListeners.remove(scrollStateChangeListener)
    }

    fun removeScrollListener(scrollListener: ScrollListener) {
        removeScrollStateChangeListener(ScrollListenerAdapter(scrollListener))
    }

    fun removeItemChangedListener(onItemChangedListener: OnItemChangedListener) {
        onItemChangedListeners.remove(onItemChangedListener)
    }

    private fun notifyScrollStart(holder: ViewHolder, current: Int) {
        for (listener in scrollStateChangeListeners) {
            listener.onScrollStart(holder, current)
        }
    }

    private fun notifyScrollEnd(holder: ViewHolder, current: Int) {
        for (listener in scrollStateChangeListeners) {
            listener.onScrollEnd(holder, current)
        }
    }

    private fun notifyScroll(
        position: Float,
        currentIndex: Int, newIndex: Int,
        currentHolder: ViewHolder, newHolder: ViewHolder
    ) {
        for (listener in scrollStateChangeListeners) {
            listener.onScroll(
                position, currentIndex, newIndex,
                currentHolder,
                newHolder
            )
        }
    }

    private fun notifyCurrentItemChanged(holder: ViewHolder, current: Int) {
        for (listener in onItemChangedListeners) {
            listener.onCurrentItemChanged(holder, current)
        }
    }

    private fun notifyCurrentItemChanged() {
        if (onItemChangedListeners.isEmpty()) {
            return
        }
        val current: Int = getDiscreteScrollLayoutManager()?.getCurrentPosition() ?: return
        val currentHolder = getViewHolder(current) ?: return
        notifyCurrentItemChanged(currentHolder, current)
    }

    interface ScrollStateChangeListener {
        fun onScrollStart(currentItemHolder: ViewHolder, adapterPosition: Int)
        fun onScrollEnd(currentItemHolder: ViewHolder, adapterPosition: Int)
        fun onScroll(
            scrollPosition: Float,
            currentPosition: Int,
            newPosition: Int,
            currentHolder: ViewHolder,
            newHolder: ViewHolder
        )
    }

    interface ScrollListener {
        fun onScroll(
            scrollPosition: Float,
            currentPosition: Int, newPosition: Int,
            currentHolder: ViewHolder,
            newCurrent: ViewHolder
        )
    }

    interface OnItemChangedListener {
        /*
         * This method will be also triggered when view appears on the screen for the first time.
         * If data set is empty, viewHolder will be null and adapterPosition will be NO_POSITION
         */
        fun onCurrentItemChanged(viewHolder: ViewHolder, adapterPosition: Int)
    }
}