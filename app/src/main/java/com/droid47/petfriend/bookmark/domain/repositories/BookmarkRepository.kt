package com.droid47.petfriend.bookmark.domain.repositories

import com.droid47.petfriend.search.data.models.search.PetEntity
import io.reactivex.Completable
import io.reactivex.Flowable

interface BookmarkRepository {

    fun fetchBookmarkList(): Flowable<List<PetEntity>>

    fun insertOrDeleteBookmark(petEntity: PetEntity): Completable

    fun deleteAllBookmark(): Completable

    fun getBookmarkStatus(id: Int): PetEntity?

    fun listenToUpdateFor(id: Int): Flowable<List<PetEntity>>

}