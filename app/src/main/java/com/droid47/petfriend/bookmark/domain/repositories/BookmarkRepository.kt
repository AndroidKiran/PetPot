package com.droid47.petfriend.bookmark.domain.repositories

import androidx.paging.DataSource
import com.droid47.petfriend.search.data.models.search.PetEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface BookmarkRepository {

    fun fetchBookmarkList(): DataSource.Factory<Int, PetEntity>

    fun insertOrDeleteBookmark(petEntity: PetEntity): Completable

    fun deleteAllBookmark(): Completable

    fun getBookmarkStatus(id: Int): PetEntity?

    fun listenToUpdateFor(id: Int): Flowable<List<PetEntity>>

    fun fetchBookmarkListSingle(): Single<List<PetEntity>>

}