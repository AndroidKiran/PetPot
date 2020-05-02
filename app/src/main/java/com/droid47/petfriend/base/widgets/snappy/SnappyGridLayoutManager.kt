package com.droid47.petfriend.base.widgets.snappy

import android.content.Context
import android.util.AttributeSet
import android.view.animation.Interpolator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SnappyGridLayoutManager : GridLayoutManager, SnappyLayoutManager {

    private lateinit var builder: SnappySmoothScroller.Builder

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    constructor(context: Context?, spanCount: Int) : super(context, spanCount) {
        init()
    }

    constructor(
        context: Context?,
        spanCount: Int,
        orientation: Int,
        reverseLayout: Boolean
    ) : super(context, spanCount, orientation, reverseLayout) {
        init()
    }

    private fun init() {
        builder = SnappySmoothScroller.Builder()
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        val scroller = builder
            .setPosition(position)
            .setScrollVectorDetector(LinearLayoutScrollVectorDetector(this))
            .build(recyclerView.context)
        startSmoothScroll(scroller)
    }

    override fun setSnapType(snapType: SnapType) {
        builder.setSnapType(snapType)
    }

    override fun setSnapDuration(snapDuration: Int) {
        builder.setSnapDuration(snapDuration)
    }

    override fun setSnapInterpolator(snapInterpolator: Interpolator) {
        builder.setSnapInterpolator(snapInterpolator)
    }

    override fun setSnapPadding(snapPadding: Int) {
        builder.setSnapPadding(snapPadding)
    }

    override fun setSnapPaddingStart(snapPaddingStart: Int) {
        builder.setSnapPaddingStart(snapPaddingStart)
    }

    override fun setSnapPaddingEnd(snapPaddingEnd: Int) {
        builder.setSnapPaddingEnd(snapPaddingEnd)
    }

    override fun setSeekDuration(seekDuration: Int) {
        builder.setSeekDuration(seekDuration)
    }
}