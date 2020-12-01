package com.droid47.petpot.search.presentation.ui.widgets

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.droid47.petpot.base.widgets.BaseCheckableEntity
import com.droid47.petpot.base.widgets.BaseViewHolder
import com.droid47.petpot.databinding.ItemPetFilterChipBinding
import com.droid47.petpot.databinding.ItemSelectedFilterChipBinding
import com.droid47.petpot.search.data.models.PetFilterCheckableEntity
import java.util.*
import javax.inject.Inject

class FilterAdapter @Inject constructor(
    private val type: String,
    private val baseCheckableEntityListener: (BaseCheckableEntity) -> Unit
) : ListAdapter<BaseCheckableEntity, BaseViewHolder<BaseCheckableEntity>>(FilterDiff), Filterable {

    private var filterItems = emptyList<BaseCheckableEntity>()

    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<BaseCheckableEntity> =
        when (type) {
            CATEGORY_FILTER -> CategoryFilterViewHolder(
                ItemPetFilterChipBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            SELECTED_FILTER -> SelectedPetFilterViewHolder(
                ItemSelectedFilterChipBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalStateException("Invalid type")
        } as BaseViewHolder<BaseCheckableEntity>

    override fun onViewRecycled(holder: BaseViewHolder<BaseCheckableEntity>) {
        holder.onUnbind()
        super.onViewRecycled(holder)
    }


    override fun onBindViewHolder(holder: BaseViewHolder<BaseCheckableEntity>, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun getFilter(): Filter = filterListener

    fun submitFilterList(list: List<BaseCheckableEntity>, modifyOriginal: Boolean) {
        if (modifyOriginal) {
            filterItems = list
        }
        super.submitList(list.toMutableList())
    }

    object FilterDiff : DiffUtil.ItemCallback<BaseCheckableEntity>() {
        override fun areItemsTheSame(
            oldCheckableEntityPet: BaseCheckableEntity,
            newCheckableEntityPet: BaseCheckableEntity
        ): Boolean = when {
            oldCheckableEntityPet is PetFilterCheckableEntity && newCheckableEntityPet is PetFilterCheckableEntity -> oldCheckableEntityPet.id == newCheckableEntityPet.id
            else -> oldCheckableEntityPet == newCheckableEntityPet
        }

        override fun areContentsTheSame(
            oldCheckableEntityPet: BaseCheckableEntity,
            newCheckableEntityPet: BaseCheckableEntity
        ): Boolean = when {
            oldCheckableEntityPet is PetFilterCheckableEntity && newCheckableEntityPet is PetFilterCheckableEntity ->
                oldCheckableEntityPet.selected == newCheckableEntityPet.selected && oldCheckableEntityPet.filterApplied == newCheckableEntityPet.filterApplied
            else -> oldCheckableEntityPet.selected == newCheckableEntityPet.selected && oldCheckableEntityPet.filterApplied == newCheckableEntityPet.filterApplied
        }
    }

    private val filterListener = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterString = constraint.toString().trim().toLowerCase(Locale.US)
            val filterList = mutableListOf<BaseCheckableEntity>()
            filterItems.forEach { filterItem ->
                val name = filterItem.name?.trim()?.toLowerCase(Locale.US) ?: ""
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
            val resultList = results?.values as? List<BaseCheckableEntity>
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

    private inner class CategoryFilterViewHolder(private val itemBinding: ItemPetFilterChipBinding) :
        BaseViewHolder<PetFilterCheckableEntity>(itemBinding.root) {

        override fun onBind(item: PetFilterCheckableEntity) {
            itemBinding.run {
                filterItem = item
                executePendingBindings()
            }

            itemBinding.chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (!buttonView.isPressed) return@setOnCheckedChangeListener
                baseCheckableEntityListener.invoke(item.apply {
                    this.selected = isChecked
                })
            }
        }

        override fun onUnbind() {
            itemBinding.run {
                filterItem = null
                executePendingBindings()
            }
        }
    }

    private inner class SelectedPetFilterViewHolder(private val itemBinding: ItemSelectedFilterChipBinding) :
        BaseViewHolder<BaseCheckableEntity>(itemBinding.root) {

        override fun onBind(item: BaseCheckableEntity) {
            itemBinding.run {
                filterItem = item
                executePendingBindings()
            }

            itemBinding.chip.setOnCloseIconClickListener { view ->
                baseCheckableEntityListener.invoke(item.apply {
                    this.selected = false
                })
            }
        }

        override fun onUnbind() {
            itemBinding.run {
                filterItem = null
                executePendingBindings()
            }
        }
    }

    companion object {
        const val CATEGORY_FILTER = "category_filter"
        const val SELECTED_FILTER = "all_filter"
    }
}