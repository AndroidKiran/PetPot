package com.droid47.petpot.search.data.models.search

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.droid47.petpot.BR
import com.droid47.petpot.base.extensions.isNotEmpty
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_BOOK_MARK_AT
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_BOOK_MARK_STATUS
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

abstract class PetEntity : BaseObservable(), Parcelable {
    abstract val gender: String?
    abstract val type: String?
    abstract val photos: List<PhotosItemEntity>?
    abstract val colorsEntity: ColorsEntity?
    abstract val breedEntity: BreedEntity?
    abstract val tags: List<String>?
    abstract val coat: String?
    abstract val environmentEntity: EnvironmentEntity?
    abstract val size: String?
    abstract val species: String?
    abstract val contactEntity: ContactEntity?
    abstract val name: String?
    abstract val attributesEntity: AttributesEntity?
    abstract val id: Int
    abstract val publishedAt: Date?
    abstract val statusModifiedAt: Date?
    abstract val age: String?
    abstract val status: String?
    abstract val desc: String?
    abstract val url: String?
    abstract val distance: Double
    abstract val videos: List<VideoItemEntity>?
    abstract var bookmarkStatus: Boolean
    abstract var bookmarkedAt: Long

    fun getPetPhoto(): String {
        val photoList = photos ?: emptyList()
        var picUrl = ""
        for (photo in photoList) {
            picUrl = photo.getPetMediumPhoto()
            if (picUrl.isNotEmpty()) {
                break
            }
        }
        return picUrl
    }

    fun getStatusChangedAtInLong(): Long {
        return statusModifiedAt?.time ?: 0L
    }

    fun getPublishedAtInLong(): Long {
        return publishedAt?.time ?: 0L
    }

    object TableInfo {
        const val TABLE_NAME_ = "favourite_pets"
        const val COL_GENDER = "gender"
        const val COL_TYPE = "type"
        const val COL_PHOTOS = "photos"
        const val COL_COLORS = "colors"
        const val COL_BREED = "breed"
        const val COL_TAGS = "tags"
        const val COL_COAT = "coat"
        const val COL_ENV = "environment"
        const val COL_SIZE = "size"
        const val COL_SPECIES = "species"
        const val COL_CONTACT = "contact"
        const val COL_NAME = "name"
        const val COL_ATTR = "attributes"
        const val COL_ID = "id"
        const val COL_PUBLISHED_AT = "published_at"
        const val COL_AGE = "age"
        const val COL_STATUS = "status"
        const val COL_DESCRIPTION = "description"
        const val COL_BOOK_MARK_AT = "bookmark_at"
        const val COL_BOOK_MARK_STATUS = "bookmark_status"
        const val COL_URL = "url"
        const val COL_DISTANCE = "distance"
        const val COL_VIDEOS = "videos"
        const val COL_STATUS_CHANGED_AT = "status_changed_at"
    }
}

@Entity(tableName = SearchPetEntity.TABLE_NAME)
@Parcelize
data class SearchPetEntity(
    @field:SerializedName("gender")
    @ColumnInfo(name = TableInfo.COL_GENDER)
    override val gender: String? = null,

    @field:SerializedName("type")
    @ColumnInfo(name = TableInfo.COL_TYPE, index = true)
    override val type: String? = null,

    @field:SerializedName("photos")
    @ColumnInfo(name = TableInfo.COL_PHOTOS)
    override val photos: List<PhotosItemEntity>? = null,

    @field:SerializedName("colors")
    @ColumnInfo(name = TableInfo.COL_COLORS)
    override val colorsEntity: ColorsEntity? = null,

    @field:SerializedName("breeds")
    @ColumnInfo(name = TableInfo.COL_BREED)
    override val breedEntity: BreedEntity? = null,

    @field:SerializedName("tags")
    @ColumnInfo(name = TableInfo.COL_TAGS)
    override val tags: List<String>? = null,

    @field:SerializedName("coat")
    @ColumnInfo(name = TableInfo.COL_COAT)
    override val coat: String? = null,

    @field:SerializedName("environment")
    @ColumnInfo(name = TableInfo.COL_ENV)
    override val environmentEntity: EnvironmentEntity? = null,

    @field:SerializedName("size")
    @ColumnInfo(name = TableInfo.COL_SIZE)
    override val size: String? = null,

    @field:SerializedName("species")
    @ColumnInfo(name = TableInfo.COL_SPECIES)
    override val species: String? = null,

    @field:SerializedName("contact")
    @ColumnInfo(name = TableInfo.COL_CONTACT)
    override val contactEntity: ContactEntity? = null,

    @field:SerializedName("name")
    @ColumnInfo(name = TableInfo.COL_NAME, index = true)
    override val name: String? = null,

    @field:SerializedName("attributes")
    @ColumnInfo(name = TableInfo.COL_ATTR)
    override val attributesEntity: AttributesEntity? = null,

    @field:SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = TableInfo.COL_ID, index = true)
    @get:Bindable
    override val id: Int = -1,

    @field:SerializedName("published_at")
    @ColumnInfo(name = TableInfo.COL_PUBLISHED_AT, index = true)
    override val publishedAt: Date? = null,

    @field:SerializedName("status_changed_at")
    @ColumnInfo(name = TableInfo.COL_STATUS_CHANGED_AT)
    override val statusModifiedAt: Date? = null,

    @field:SerializedName("age")
    @ColumnInfo(name = TableInfo.COL_AGE)
    override val age: String? = null,

    @field:SerializedName("status")
    @ColumnInfo(name = TableInfo.COL_STATUS)
    override val status: String? = null,

    @field:SerializedName("description")
    @ColumnInfo(name = TableInfo.COL_DESCRIPTION)
    override val desc: String? = null,

    @field:SerializedName("url")
    @ColumnInfo(name = TableInfo.COL_URL)
    override val url: String? = null,

    @field:SerializedName("distance")
    @ColumnInfo(name = TableInfo.COL_DISTANCE, index = true)
    override val distance: Double = 0.0,

    @field:SerializedName("videos")
    @ColumnInfo(name = TableInfo.COL_VIDEOS)
    override val videos: List<VideoItemEntity>? = null
) : PetEntity() {

    @get:Bindable
    @ColumnInfo(name = COL_BOOK_MARK_STATUS, index = true)
    @field:SerializedName("bookmark_status")
    override var bookmarkStatus: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.bookmarkStatus)
        }

    @ColumnInfo(name = COL_BOOK_MARK_AT, index = true)
    @field:SerializedName("bookmarked_at")
    override var bookmarkedAt: Long = System.currentTimeMillis()

    companion object {
        const val TABLE_NAME = "pets"
    }
}

@Entity(tableName = FavouritePetEntity.TABLE_NAME)
@Parcelize
data class FavouritePetEntity(
    @field:SerializedName("gender")
    @ColumnInfo(name = TableInfo.COL_GENDER)
    override val gender: String? = null,

    @field:SerializedName("type")
    @ColumnInfo(name = TableInfo.COL_TYPE, index = true)
    override val type: String? = null,

    @field:SerializedName("photos")
    @ColumnInfo(name = TableInfo.COL_PHOTOS)
    override val photos: List<PhotosItemEntity>? = null,

    @field:SerializedName("colors")
    @ColumnInfo(name = TableInfo.COL_COLORS)
    override val colorsEntity: ColorsEntity? = null,

    @field:SerializedName("breeds")
    @ColumnInfo(name = TableInfo.COL_BREED)
    override val breedEntity: BreedEntity? = null,

    @field:SerializedName("tags")
    @ColumnInfo(name = TableInfo.COL_TAGS)
    override val tags: List<String>? = null,

    @field:SerializedName("coat")
    @ColumnInfo(name = TableInfo.COL_COAT)
    override val coat: String? = null,

    @field:SerializedName("environment")
    @ColumnInfo(name = TableInfo.COL_ENV)
    override val environmentEntity: EnvironmentEntity? = null,

    @field:SerializedName("size")
    @ColumnInfo(name = TableInfo.COL_SIZE)
    override val size: String? = null,

    @field:SerializedName("species")
    @ColumnInfo(name = TableInfo.COL_SPECIES)
    override val species: String? = null,

    @field:SerializedName("contact")
    @ColumnInfo(name = TableInfo.COL_CONTACT)
    override val contactEntity: ContactEntity? = null,

    @field:SerializedName("name")
    @ColumnInfo(name = TableInfo.COL_NAME, index = true)
    override val name: String? = null,

    @field:SerializedName("attributes")
    @ColumnInfo(name = TableInfo.COL_ATTR)
    override val attributesEntity: AttributesEntity? = null,

    @field:SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = TableInfo.COL_ID, index = true)
    @get:Bindable
    override val id: Int = -1,

    @field:SerializedName("published_at")
    @ColumnInfo(name = TableInfo.COL_PUBLISHED_AT, index = true)
    override val publishedAt: Date? = null,

    @field:SerializedName("status_changed_at")
    @ColumnInfo(name = TableInfo.COL_STATUS_CHANGED_AT)
    override val statusModifiedAt: Date? = null,

    @field:SerializedName("age")
    @ColumnInfo(name = TableInfo.COL_AGE)
    override val age: String? = null,

    @field:SerializedName("status")
    @ColumnInfo(name = TableInfo.COL_STATUS)
    override val status: String? = null,

    @field:SerializedName("description")
    @ColumnInfo(name = TableInfo.COL_DESCRIPTION)
    override val desc: String? = null,

    @field:SerializedName("url")
    @ColumnInfo(name = TableInfo.COL_URL)
    override val url: String? = null,

    @field:SerializedName("distance")
    @ColumnInfo(name = TableInfo.COL_DISTANCE, index = true)
    override val distance: Double = 0.0,

    @field:SerializedName("videos")
    @ColumnInfo(name = TableInfo.COL_VIDEOS)
    override val videos: List<VideoItemEntity>? = null
) : PetEntity() {

    @get:Bindable
    @ColumnInfo(name = COL_BOOK_MARK_STATUS, index = true)
    @field:SerializedName("bookmark_status")
    override var bookmarkStatus: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.bookmarkStatus)
        }

    @ColumnInfo(name = COL_BOOK_MARK_AT, index = true)
    @field:SerializedName("bookmarked_at")
    override var bookmarkedAt: Long = System.currentTimeMillis()

    companion object {
        const val TABLE_NAME = "favourite_pets"
    }
}