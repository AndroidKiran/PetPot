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
import com.droid47.petpot.base.extensions.themeColor
import com.droid47.petpot.base.widgets.BaseViewHolder
import com.droid47.petpot.databinding.ItemBookMarkBinding
import com.droid47.petpot.search.data.models.search.FavouritePetEntity
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import javax.inject.Inject

class PagedFavouriteListPetAdapter @Inject constructor(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener
) : PagedListAdapter<FavouritePetEntity, BaseViewHolder<FavouritePetEntity>>(SearchDiff) {

    private var recyclerView: RecyclerView? = null

    init {
        setHasStableIds(true)
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<FavouritePetEntity> =
        BookmarkViewHolder(
            ItemBookMarkBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = currentList?.size ?: 0

    override fun getItemId(position: Int): Long = getItem(position)?.id?.toLong() ?: -1L

    override fun onBindViewHolder(holder: BaseViewHolder<FavouritePetEntity>, position: Int) {
        holder.onBind(getItem(position) ?: return)
    }

    object SearchDiff : DiffUtil.ItemCallback<FavouritePetEntity>() {
        override fun areItemsTheSame(
            oldItem: FavouritePetEntity,
            newItem: FavouritePetEntity
        ): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: FavouritePetEntity,
            newItem: FavouritePetEntity
        ): Boolean =
            oldItem == newItem
    }


    private inner class BookmarkViewHolder(private val itemBinding: ItemBookMarkBinding) :
        BaseViewHolder<FavouritePetEntity>(itemBinding.root) {

        override fun onBind(item: FavouritePetEntity) {
            itemBinding.apply {
                setVariable(BR.pet, item)
                executePendingBindings()
            }

            itemBinding.cibStar.let { checkableBtn ->
                checkableBtn.isChecked = item.bookmarkStatus
                checkableBtn.background = bookMarkRoundedShapeDrawable
                checkableBtn.setOnClickListener {
                    checkableBtn.isChecked = !item.bookmarkStatus
                    onItemClickListener.onBookMarkClick(item.apply {
                        bookmarkStatus = !item.bookmarkStatus
                    })
                }
            }

            itemBinding.root.setOnClickListener {
                onItemClickListener.onItemClick(item, it)
            }
        }
    }

    interface OnItemClickListener {
        fun onBookMarkClick(petEntity: FavouritePetEntity)
        fun onItemClick(petEntity: FavouritePetEntity, view: View)
    }
}