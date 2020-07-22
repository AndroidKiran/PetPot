package com.droid47.petfriend.petDetails.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.droid47.petfriend.base.extensions.loadListener
import com.droid47.petfriend.base.widgets.BaseViewHolder
import com.droid47.petfriend.databinding.ItemPetPhotoBinding
import com.droid47.petfriend.search.data.models.search.PhotosItemEntity

class PetPhotoViewerAdapter(private val petPhotoViewerListener: PetPhotoViewerListener) :
    ListAdapter<PhotosItemEntity, BaseViewHolder<PhotosItemEntity>>(UrlDiff) {
    private var modifiedAt: Long = 0L

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<PhotosItemEntity> =
        PetPhotoViewHolder(
            ItemPetPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )

    override fun onBindViewHolder(holder: BaseViewHolder<PhotosItemEntity>, position: Int) {
        holder.onBind(getItem(position))
    }

    object UrlDiff : DiffUtil.ItemCallback<PhotosItemEntity>() {
        override fun areItemsTheSame(
            oldItemEntity: PhotosItemEntity,
            newItemEntity: PhotosItemEntity
        ): Boolean = oldItemEntity == newItemEntity

        override fun areContentsTheSame(
            oldItemEntity: PhotosItemEntity,
            newItemEntity: PhotosItemEntity
        ): Boolean = oldItemEntity == newItemEntity
    }

    fun setModifiedAt(modifiedAt: Long) {
        this.modifiedAt = modifiedAt
    }

    inner class PetPhotoViewHolder(private val binding: ItemPetPhotoBinding) :
        BaseViewHolder<PhotosItemEntity>(binding.root) {

        override fun onBind(item: PhotosItemEntity) {
            val photoUrl = item.getPetFullPhoto()
            binding.apply {
                petPhotoUrl = photoUrl
                this.modifiedAt = modifiedAt
                imageLoadListener = loadListener(this.circularProgress) {
                    petPhotoViewerListener.onImageLoaded()
                }
            }.run {
                executePendingBindings()
            }
            binding.root.setOnClickListener {
                petPhotoViewerListener.onItemClickListener()
            }
        }
    }

    interface PetPhotoViewerListener {
        fun onImageLoaded()
        fun onItemClickListener()
    }
}