package com.droid47.petgoogle.search.presentation.widgets

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.droid47.petgoogle.BR
import com.droid47.petgoogle.base.widgets.BaseViewHolder
import com.droid47.petgoogle.databinding.ItemFilterChipBinding
import com.droid47.petgoogle.databinding.ItemSelectedFilterBinding
import com.droid47.petgoogle.search.data.models.FilterItemEntity
import com.google.android.material.chip.Chip
import javax.inject.Inject

class FilterAdapter @Inject constructor(
    private val type: String,
    private val onItemCheckListener: OnItemCheckListener
) : ListAdapter<FilterItemEntity, BaseViewHolder>(FilterDiff), Filterable {

    private var filterItems = emptyList<FilterItemEntity>()

    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when (type) {
            CATEGORY_FILTER -> CategoryFilterViewHolder(
                ItemFilterChipBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            ALL_FILTER -> SelectedFilterViewHolder(
                ItemSelectedFilterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalStateException("Invalid type")
        }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getFilter(): Filter = filterListener

    fun submitFilterList(list: List<FilterItemEntity>, modifyOriginal: Boolean) {
        if (modifyOriginal) {
            filterItems = list
        }
        super.submitList(list.toMutableList())
    }

    override fun onCurrentListChanged(
        previousList: MutableList<FilterItemEntity>,
        currentList: MutableList<FilterItemEntity>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        if (type == ALL_FILTER) {
            for (curFilterItem in currentList) {
                if (!previousList.contains(curFilterItem)) {
                    val index = filterItems.indexOf(curFilterItem)
                    recyclerView?.scrollToPosition(index)
                    return
                }
            }
        }
    }

    object FilterDiff : DiffUtil.ItemCallback<FilterItemEntity>() {
        override fun areItemsTheSame(
            oldItemEntity: FilterItemEntity,
            newItemEntity: FilterItemEntity
        ): Boolean =
            oldItemEntity.id == newItemEntity.id

        override fun areContentsTheSame(
            oldItemEntity: FilterItemEntity,
            newItemEntity: FilterItemEntity
        ): Boolean =
            oldItemEntity == newItemEntity
    }

    private val filterListener = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterString = constraint.toString().trim().toLowerCase()
            val filterList = mutableListOf<FilterItemEntity>()
            filterItems.forEach { filterItem ->
                val name = filterItem.name.trim().toLowerCase()
                if (name.contains(filterString)) {
                    filterList.add(filterItem)
                }
            }

            return FilterResults().apply {
                values = filterList.toList()
                count = filterList.size
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val resultList = results?.values as? List<FilterItemEntity>
            submitFilterList(
                if (resultList == null || resultList.isEmpty()) {
                    emptyList()
                } else {
                    resultList.toMutableList()
                },
                false
            )
        }
    }

    private inner class CategoryFilterViewHolder(private val itemBinding: ItemFilterChipBinding) :
        BaseViewHolder(itemBinding.root) {

        override fun onBind(position: Int) {
            val item = getItem(position)
            itemBinding.also {
                it.setVariable(BR.filterItem, item)
                it.executePendingBindings()
            }

            itemBinding.chip.setOnClickListener { view ->
                val chipView = view as Chip
                onItemCheckListener.onItemCheck(
                    item.apply {
                        this.selected = chipView.isChecked
                    }
                )
            }
        }
    }

    private inner class SelectedFilterViewHolder(private val itemBinding: ItemSelectedFilterBinding) :
        BaseViewHolder(itemBinding.root) {

        override fun onBind(position: Int) {
            val item = getItem(position)
            itemBinding.also {
                it.setVariable(BR.filterItem, item)
                it.executePendingBindings()
            }

            itemBinding.chip.setOnCloseIconClickListener { view ->
                onItemCheckListener.onItemCheck(
                    item.apply {
                        this.selected = false
                    }
                )
            }
        }
    }


    interface OnItemCheckListener {
        fun onItemCheck(filterItemEntity: FilterItemEntity)
    }

    companion object {
        const val CATEGORY_FILTER = "category_filter"
        const val ALL_FILTER = "all_filter"
    }
}