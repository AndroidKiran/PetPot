package com.droid47.petfriend.bookmark.data

import com.droid47.petfriend.bookmark.domain.repositories.BookmarkRepository
import com.droid47.petfriend.search.data.datasource.PetDao
import com.droid47.petfriend.search.data.models.search.PetEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

class BookmarkRepo @Inject constructor(private val petDao: PetDao) : BookmarkRepository {

    override fun fetchBookmarkList(): Flowable<List<PetEntity>> = petDao.getBookmarkPetList()

    override fun insertOrDeleteBookmark(petEntity: PetEntity) =
        Completable.create { emitter ->
            try {
                petDao.insertOrDelete(petEntity)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun deleteAllBookmark(): Completable =
        Completable.create { emitter ->
            try {
                petDao.deleteAll()
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun getBookmarkStatus(id: Int): PetEntity? = petDao.getPetById(id)

    override fun listenToUpdateFor(id: Int): Flowable<List<PetEntity>> =
        petDao.listenToUpdateFor(id)

}