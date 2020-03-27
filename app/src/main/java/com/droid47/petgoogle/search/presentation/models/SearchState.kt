package com.droid47.petgoogle.search.presentation.models

import com.droid47.petgoogle.search.data.models.search.PaginationEntity
import com.droid47.petgoogle.search.data.models.search.PetEntity

sealed class SearchState {
    abstract val paginationEntity: PaginationEntity
    abstract val loadedAllItems: Boolean
    abstract val data: List<PetEntity>
}

class DefaultState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val data: List<PetEntity>
) : SearchState()

class LoadingState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val data: List<PetEntity>
) : SearchState()

class PaginatingState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val data: List<PetEntity>
) : SearchState()

class ErrorState(
    val error: Throwable,
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val data: List<PetEntity>
) : SearchState()

class PaginationErrorState(
    val error: Throwable,
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val data: List<PetEntity>
) : SearchState()


class EmptyState(
    override val paginationEntity: PaginationEntity,
    override val loadedAllItems: Boolean,
    override val data: List<PetEntity>
) : SearchState()