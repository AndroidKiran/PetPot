package com.droid47.petfriend.search.presentation.models

import com.droid47.petfriend.search.data.models.search.PaginationEntity
import com.droid47.petfriend.search.data.models.search.PetEntity

sealed class SearchState {
    abstract val paginationEntity: PaginationEntity
    abstract val loadedAllItems: Boolean
    abstract val totalCount: Int
}

class DefaultState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : SearchState()

class LoadingState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : SearchState()

class PaginatingState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : SearchState()

class ErrorState(
    val error: Throwable,
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : SearchState()

class PaginationErrorState(
    val error: Throwable,
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : SearchState()

class EmptyState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val totalCount: Int
) : SearchState()