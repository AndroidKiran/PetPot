package com.droid47.petgoogle.petDetails.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.droid47.petgoogle.base.extensions.loadListener
import com.droid47.petgoogle.base.widgets.BaseViewHolder
import com.droid47.petgoogle.databinding.ItemPetPhotoBinding
import com.droid47.petgoogle.search.data.models.search.PhotosItemEntity

class PetPhotoViewerAdapter(private val petPhotoViewerListener: PetPhotoViewerListener) :
    ListAdapter<PhotosItemEntity, BaseViewHolder>(UrlDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        PetPhotoViewHolder(
            ItemPetPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    object UrlDiff : DiffUtil.ItemCallback<PhotosItemEntity>() {
        override fun areItemsTheSame(
            oldItemEntity: PhotosItemEntity,
            newItemEntity: PhotosItemEntity
        ) =
            oldItemEntity == newItemEntity

        override fun areContentsTheSame(
            oldItemEntity: PhotosItemEntity,
            newItemEntity: PhotosItemEntity
        ) =
            oldItemEntity == newItemEntity
    }

    inner class PetPhotoViewHolder(private val binding: ItemPetPhotoBinding) :
        BaseViewHolder(binding.root) {

        override fun onBind(position: Int) {
            val photoItem = getItem(position)
            binding.apply {
                petPhotoUrl = photoItem.getPetFullPhoto()
                imageLoadListener = loadListener {
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