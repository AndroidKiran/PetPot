package com.droid47.petpot.search.presentation.ui.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.droid47.petpot.BR
import com.droid47.petpot.R
import com.droid47.petpot.base.extensions.getScreenWidth
import com.droid47.petpot.base.extensions.themeColor
import com.droid47.petpot.base.extensions.updateWidth
import com.droid47.petpot.base.widgets.BaseViewHolder
import com.droid47.petpot.base.widgets.components.CheckableImageButton
import com.droid47.petpot.databinding.ItemPetBinding
import com.droid47.petpot.databinding.ItemSimilarPetBinding
import com.droid47.petpot.search.data.models.search.FavouritePetEntity
import com.droid47.petpot.search.data.models.search.SearchPetEntity
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import javax.inject.Inject

class PagedSearchListPetAdapter @Inject constructor(
    private val context: Context,
    private val type: AdapterType = AdapterType.Search,
    private val onItemClickListener: OnItemClickListener
) : PagedListAdapter<SearchPetEntity, BaseViewHolder<SearchPetEntity>>(SearchDiff) {

    private var recyclerView: RecyclerView? = null
    private val screenWidth = context.getScreenWidth()
    private val diffWidth = context.resources.getDimensionPixelSize(R.dimen.grid_12)
    private val imgWidth = context.resources.getDimensionPixelSize(R.dimen.small_card_height)

    init {
        setHasStableIds(true)
    }

    private val bookMarkShapeDrawable: MaterialShapeDrawable by lazy(LazyThreadSafetyMode.NONE) {
        MaterialShapeDrawable(
            context,
            null,
            R.attr.materialCardViewStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                context.themeColor(R.attr.colorSecondary)
            )
            elevation = context.resources.getDimension(R.dimen.plane_08)
            shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_NEVER
            initializeElevationOverlay(context)
            shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                .setBottomLeftCorner(
                    CornerFamily.ROUNDED,
                    context.resources.getDimension(R.dimen.pet_small_component_corner_radius)
                ).setBottomRightCorner(
                    CornerFamily.ROUNDED,
                    context.resources.getDimension(R.dimen.zero)
                ).setTopLeftCorner(
                    CornerFamily.ROUNDED,
                    context.resources.getDimension(R.dimen.zero)
                ).setTopRightCorner(
                    CornerFamily.ROUNDED,
                    context.resources.getDimension(R.dimen.zero)
                ).build()
        }
    }

    private val bookMarkRoundedShapeDrawable: MaterialShapeDrawable by lazy(LazyThreadSafetyMode.NONE) {
        MaterialShapeDrawable(
            context,
            null,
            R.attr.materialCardViewStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                context.themeColor(R.attr.colorSecondary)
            )
            elevation = context.resources.getDimension(R.dimen.plane_08)
            shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_NEVER
            initializeElevationOverlay(context)
            shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                .setAllCorners(
                    CornerFamily.ROUNDED,
                    context.resources.getDimension(R.dimen.pet_large_component_corner_radius)
                ).build()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<SearchPetEntity> =
        when (type) {
            AdapterType.Similar -> SimilarViewHolder(
                ItemSimilarPetBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> SearchViewHolder(
                ItemPetBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    override fun getItemCount(): Int = currentList?.size ?: 0

    override fun getItemId(position: Int): Long = getItem(position)?.id?.toLong() ?: -1L

    override fun onBindViewHolder(holder: BaseViewHolder<SearchPetEntity>, position: Int) {
        holder.onBind(getItem(position)?:return)
    }

    object SearchDiff : DiffUtil.ItemCallback<SearchPetEntity>() {
        override fun areItemsTheSame(oldItem: SearchPetEntity, newItem: SearchPetEntity): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SearchPetEntity, newItem: SearchPetEntity): Boolean =
             oldItem == newItem
    }

    open inner class SearchViewHolder(private val itemBinding: ItemPetBinding) :
        BaseViewHolder<SearchPetEntity>(itemBinding.root) {

        override fun onBind(item: SearchPetEntity) {
            itemBinding.cibStar.background = bookMarkShapeDrawable
            itemBinding.run {
                setVariable(BR.pet, item)
                executePendingBindings()
            }

            itemBinding.root.setOnClickListener { view ->
                if (view == null) return@setOnClickListener
                onItemClickListener.onItemClick(item, view)
            }

            itemBinding.cibStar.setOnClickListener {
                val checkableImageButton = (it as CheckableImageButton)
//                onItemClickListener.onBookMarkClick(item.apply {
//                    bookmarkStatus = !checkableImageButton.isChecked
//                })
            }
        }
    }

    private inner class SimilarViewHolder(private val itemBinding: ItemSimilarPetBinding) :
        BaseViewHolder<SearchPetEntity>(itemBinding.root) {

        override fun onBind(item: SearchPetEntity) {
            itemBinding.ivPetPic.updateWidth(imgWidth)
            itemBinding.cvPetInfo.updateWidth(screenWidth.minus(diffWidth))

            itemBinding.run {
                setVariable(BR.pet, item)
                executePendingBindings()
            }

            itemBinding.root.setOnClickListener {
                onItemClickListener.onItemClick(item, itemBinding.cslPetInfo)
            }
        }
    }

    interface OnItemClickListener {
        fun onBookMarkClick(petEntity: FavouritePetEntity)
        fun onItemClick(petEntity: SearchPetEntity, view: View)
    }

    sealed class AdapterType {
        object Search : AdapterType()
        object Similar : AdapterType()
    }
}