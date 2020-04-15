package com.droid47.petfriend.base.widgets

import androidx.recyclerview.widget.DiffUtil


abstract class BaseDiffCallback<T>(private val oldItems: List<T>, private val newItems: List<T>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

    fun getOldItems(): List<T> {
        return oldItems
    }

    fun getNewItems(): List<T> {
        return newItems
    }
}