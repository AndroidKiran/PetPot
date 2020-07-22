package com.droid47.petfriend.search.presentation.models

import com.droid47.petfriend.search.data.models.search.PaginationEntity

sealed class ItemPaginationState {
    abstract val paginationEntity: PaginationEntity
    abstract val loadedAllItems: Boolean
    abstract val totalCount: Int
}

class DefaultState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : ItemPaginationState()

class LoadingState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : ItemPaginationState()

class PaginatingState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : ItemPaginationState()

class ErrorState(
    val error: Throwable,
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : ItemPaginationState()

class PaginationErrorState(
    val error: Throwable,
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : ItemPaginationState()

class EmptyState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : ItemPaginationState()