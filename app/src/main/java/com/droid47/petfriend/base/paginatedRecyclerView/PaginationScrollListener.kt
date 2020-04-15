package com.droid47.petfriend.base.paginatedRecyclerView

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private val TAG: String = "PaginationScrollListener"

abstract class PaginationScrollListener :
    RecyclerView.OnScrollListener() {
    protected abstract fun loadMoreItems()
    abstract fun isLastPage(): Boolean
    abstract fun isLoading(): Boolean

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val reachedBottomOfScreen = isRecyclerViewScrollAtBottom(recyclerView)
        if (allowLoadMore() && reachedBottomOfScreen && dy > 0) {
            recyclerView.post { loadMoreItems() }
        }
    }

    private fun allowLoadMore() = !isLoading() && !isLastPage()

    private fun isRecyclerViewScrollAtBottom(recyclerView: RecyclerView): Boolean {
        val layoutManager = recyclerView.layoutManager as GridLayoutManager?
        val adapter = recyclerView.adapter
        if (layoutManager == null || adapter == null)
            return false

        val totalItemCount = adapter.itemCount
        val totalVisibleItem =
            layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition()
        val lastCompletelyVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
        // Check last visible item is last item in the list and list is scrollable
        return lastCompletelyVisibleItem == totalItemCount - 1 && totalItemCount != totalVisibleItem
    }
}