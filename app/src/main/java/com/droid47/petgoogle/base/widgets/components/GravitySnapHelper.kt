package com.droid47.petgoogle.base.widgets.components

import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class GravitySnapHelper @JvmOverloads constructor(
    gravity: Int,
    enableSnapLastItem: Boolean = false,
    snapListener: SnapListener? = null
) : LinearSnapHelper() {
    private val delegate: GravityDelegate =
        GravityDelegate(gravity, enableSnapLastItem, snapListener)

    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        delegate.attachToRecyclerView(recyclerView)
        super.attachToRecyclerView(recyclerView)
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        return delegate.calculateDistanceToFinalSnap(layoutManager, targetView)
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        return delegate.findSnapView(layoutManager)
    }

    fun enableLastItemSnap(snap: Boolean) {
        delegate.enableLastItemSnap(snap)
    }

    interface SnapListener {
        fun onSnap(var1: Int)
    }

}
