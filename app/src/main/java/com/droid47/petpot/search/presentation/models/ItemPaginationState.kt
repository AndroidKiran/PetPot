package com.droid47.petpot.search.presentation.models

import com.droid47.petpot.search.data.models.search.PaginationEntity

sealed class ItemPaginationState {
    abstract val paginationEntity: PaginationEntity
    abstract val loadedAllItems: Boolean
    abstract val totalCount: Int
}

data class DefaultState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : ItemPaginationState()

data class LoadingState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : ItemPaginationState()

data class PaginatingState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : ItemPaginationState()

data class ErrorState(
    val error: Throwable,
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : ItemPaginationState()

data class PaginationErrorState(
    val error: Throwable,
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : ItemPaginationState()

data class EmptyState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : ItemPaginationState()