package com.droid47.petfriend.search.presentation.ui.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.droid47.petfriend.BR
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.getScreenWidth
import com.droid47.petfriend.base.extensions.themeColor
import com.droid47.petfriend.base.extensions.updateWidth
import com.droid47.petfriend.base.widgets.BaseViewHolder
import com.droid47.petfriend.base.widgets.components.CheckableImageButton
import com.droid47.petfriend.base.widgets.snappy.SnappyGridLayoutManager
import com.droid47.petfriend.databinding.ItemBookMarkBinding
import com.droid47.petfriend.databinding.ItemPetBinding
import com.droid47.petfriend.databinding.ItemSimilarPetBinding
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import javax.inject.Inject

class PagedListPetAdapter @Inject constructor(
    private val context: Context,
    private val type: AdapterType = AdapterType.Search,
    private val onItemClickListener: OnItemClickListener
) : PagedListAdapter<PetEntity, BaseViewHolder<PetEntity>>(SearchDiff) {

    private var recyclerView: RecyclerView? = null
    private val screenWidth = context.getScreenWidth()
    private val diffWidth = context.resources.getDimensionPixelSize(R.dimen.grid_12)
    private val imgWidth = context.resources.getDimensionPixelSize(R.dimen.small_card_height)

//    init {
//        setHasStableIds(true)
//    }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PetEntity> =
        when (type) {
            AdapterType.Favorite -> BookmarkViewHolder(
                ItemBookMarkBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

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

    override fun onBindViewHolder(holder: BaseViewHolder<PetEntity>, position: Int) {
        holder.onBind(getItem(position)?:return)
    }

    object SearchDiff : DiffUtil.ItemCallback<PetEntity>() {
        override fun areItemsTheSame(oldItem: PetEntity, newItem: PetEntity): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PetEntity, newItem: PetEntity): Boolean =
             oldItem == newItem
    }

    open inner class SearchViewHolder(private val itemBinding: ItemPetBinding) :
        BaseViewHolder<PetEntity>(itemBinding.root) {

        override fun onBind(item: PetEntity) {
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
                onItemClickListener.onBookMarkClick(item.apply {
                    bookmarkStatus = !checkableImageButton.isChecked
                })
            }
        }
    }

    private inner class BookmarkViewHolder(private val itemBinding: ItemBookMarkBinding) :
        BaseViewHolder<PetEntity>(itemBinding.root) {

        override fun onBind(item: PetEntity) {
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

    private inner class SimilarViewHolder(private val itemBinding: ItemSimilarPetBinding) :
        BaseViewHolder<PetEntity>(itemBinding.root) {

        override fun onBind(item: PetEntity) {
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
        fun onBookMarkClick(petEntity: PetEntity)
        fun onItemClick(petEntity: PetEntity, view: View)
    }

    sealed class AdapterType {
        object Search : AdapterType()
        object Favorite : AdapterType()
        object Similar : AdapterType()
    }
}