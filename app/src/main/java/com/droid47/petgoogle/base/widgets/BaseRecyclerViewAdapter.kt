package com.droid47.petgoogle.base.widgets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T, VH : BaseViewHolder> : RecyclerView.Adapter<VH>() {

    private val items = mutableListOf<T>()

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (items.size <= position) {
            return
        }
        holder.onBind(position)
    }


    fun isEmpty(): Boolean = itemCount == 0

    fun remove(item: T) {
        val position = items.indexOf(item)
        if (position > -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun addAll(items: List<T>?) {
        if (items == null) {
            throw IllegalArgumentException("Cannot add `null` items to the Recycler adapter")
        }
        this.items.addAll(items)
        notifyItemRangeInserted(this.items.size - items.size, items.size)
    }

    fun addToBeginning(item: T?) {
        if (item == null) {
            throw IllegalArgumentException("Cannot add null item to the Recycler adapter")
        }
        items.add(0, item)
        notifyItemInserted(0)
    }

    fun add(item: T?) {
        if (item == null) {
            throw IllegalArgumentException("Cannot add null item to the Recycler adapter")
        }
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun getItem(position: Int): T = items[position]

    fun getItems(): List<T> = items.toList()

    fun updateData(newItems: List<T>, diffCallback: DiffUtil.Callback) {
        val result = DiffUtil.calculateDiff(diffCallback, false)
        setData(newItems, false)
        result.dispatchUpdatesTo(this)
    }

    open fun updateData(newItems: List<T>) {
        setData(newItems, false)
    }

    fun onItemUpdated(updatedItem: T) {
        if (items.contains(updatedItem)) {
            val index = items.indexOf(updatedItem)
            items[index] = updatedItem
            notifyItemChanged(index)
        }
    }

    @Throws(IllegalArgumentException::class)
    fun setData(items: List<T>?, notifyChanges: Boolean) {
        if (items == null) {
            throw IllegalArgumentException("Cannot set `null` item to the Recycler adapter")
        }
        this.items.clear()
        this.items.addAll(items)
        if (notifyChanges) {
            notifyDataSetChanged()
        }
    }

    open fun setData(items: List<T>) {
        setData(items, true)
    }

    fun setItems(newItems: List<T>) {
        this.items.clear()
        this.items.addAll(newItems)
    }

    protected fun inflateWithBinding(
        @LayoutRes layout: Int,
        @Nullable parent: ViewGroup,
        attachToRoot: Boolean
    ): ViewDataBinding =
        DataBindingUtil.inflate(LayoutInflater.from(parent.context), layout, parent, attachToRoot)

    protected fun inflateWithBinding(
        @LayoutRes layout: Int,
        @Nullable parent: ViewGroup
    ): ViewDataBinding =
        DataBindingUtil.inflate(LayoutInflater.from(parent.context), layout, parent, false)
}