package com.droid47.petfriend.base.widgets.discreterRecyclerView

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewProxy(private val layoutManager: RecyclerView.LayoutManager) {

    fun attachView(view: View) {
        layoutManager.attachView(view)
    }

    fun detachView(view: View) {
        layoutManager.detachView(view)
    }

    fun detachAndScrapView(view: View, recycler: RecyclerView.Recycler) {
        layoutManager.detachAndScrapView(view, recycler)
    }

    fun detachAndScrapAttachedViews(recycler: RecyclerView.Recycler) {
        layoutManager.detachAndScrapAttachedViews(recycler)
    }

    fun recycleView(view: View, recycler: RecyclerView.Recycler) {
        recycler.recycleView(view)
    }

    fun removeAndRecycleAllViews(recycler: RecyclerView.Recycler) {
        layoutManager.removeAndRecycleAllViews(recycler)
    }

    fun getChildCount(): Int {
        return layoutManager.childCount
    }

    fun getItemCount(): Int {
        return layoutManager.itemCount
    }

    fun getWidth(): Int {
        return layoutManager.width
    }

    fun getHeight(): Int {
        return layoutManager.height
    }

    fun getMeasuredChildForAdapterPosition(position: Int, recycler: RecyclerView.Recycler): View {
        val view: View = recycler.getViewForPosition(position)
        layoutManager.addView(view)
        layoutManager.measureChildWithMargins(view, 0, 0)
        return view
    }

    fun layoutDecoratedWithMargins(
        v: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        layoutManager.layoutDecoratedWithMargins(v, left, top, right, bottom)
    }

    fun getChildAt(index: Int): View? {
        return layoutManager.getChildAt(index)
    }

    fun getPosition(view: View): Int {
        return layoutManager.getPosition(view)
    }

    fun getMeasuredWidthWithMargin(child: View): Int {
        val lp = child.layoutParams as MarginLayoutParams
        return layoutManager.getDecoratedMeasuredWidth(child) + lp.leftMargin + lp.rightMargin
    }

    fun getMeasuredHeightWithMargin(child: View): Int {
        val lp = child.layoutParams as MarginLayoutParams
        return layoutManager.getDecoratedMeasuredHeight(child) + lp.topMargin + lp.bottomMargin
    }

    fun offsetChildrenHorizontal(amount: Int) {
        layoutManager.offsetChildrenHorizontal(amount)
    }

    fun offsetChildrenVertical(amount: Int) {
        layoutManager.offsetChildrenVertical(amount)
    }

    fun requestLayout() {
        layoutManager.requestLayout()
    }

    fun startSmoothScroll(smoothScroller: RecyclerView.SmoothScroller) {
        layoutManager.startSmoothScroll(smoothScroller)
    }

    fun removeAllViews() {
        layoutManager.removeAllViews()
    }
}
