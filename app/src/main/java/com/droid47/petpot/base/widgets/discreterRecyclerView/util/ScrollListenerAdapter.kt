package com.droid47.petpot.base.widgets.discreterRecyclerView.util

import androidx.recyclerview.widget.RecyclerView
import com.droid47.petpot.base.widgets.discreterRecyclerView.DiscreteScrollView

class ScrollListenerAdapter(private val adaptee: DiscreteScrollView.ScrollListener) :
    DiscreteScrollView.ScrollStateChangeListener {

    override fun onScrollStart(currentItemHolder: RecyclerView.ViewHolder, adapterPosition: Int) {}

    override fun onScrollEnd(currentItemHolder: RecyclerView.ViewHolder, adapterPosition: Int) {}

    override fun onScroll(
        scrollPosition: Float,
        currentPosition: Int, newPosition: Int,
        currentHolder: RecyclerView.ViewHolder, newHolder: RecyclerView.ViewHolder
    ) {
        adaptee.onScroll(scrollPosition, currentPosition, newPosition, currentHolder, newHolder)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is ScrollListenerAdapter) {
            adaptee == (other as? ScrollListenerAdapter)?.adaptee
        } else {
            super.equals(other)
        }
    }

}
