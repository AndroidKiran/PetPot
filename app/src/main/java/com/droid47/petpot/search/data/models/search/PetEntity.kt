package com.droid47.petpot.search.data.models.search

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.droid47.petpot.BR
import com.droid47.petpot.base.extensions.isNotEmpty
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_AGE
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_ATTR
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_BOOK_MARK_AT
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_BOOK_MARK_STATUS
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_BREED
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_COAT
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_COLORS
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_CONTACT
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_DESCRIPTION
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_DISTANCE
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_ENV
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_GENDER
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_ID
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_NAME
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_PHOTOS
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_PUBLISHED_AT
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_SIZE
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_SPECIES
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_STATUS
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_TAGS
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_TYPE
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_URL
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.COL_VIDEOS
import com.droid47.petpot.search.data.models.search.PetEntity.TableInfo.TABLE_NAME
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.OffsetDateTime

@Entity(tableName = TABLE_NAME)
@Parcelize
data class PetEntity(
    @field:SerializedName("gender")
    @ColumnInfo(name = COL_GENDER)
    var gender: String? = null,

    @field:SerializedName("type")
    @ColumnInfo(name = COL_TYPE, index = true)
    var type: String? = null,

    @field:SerializedName("photos")
    @ColumnInfo(name = COL_PHOTOS)
    var photos: List<PhotosItemEntity>? = null,

    @field:SerializedName("colors")
    @ColumnInfo(name = COL_COLORS)
    var colorsEntity: ColorsEntity? = null,

    @field:SerializedName("breeds")
    @ColumnInfo(name = COL_BREED)
    var breedEntity: BreedEntity? = null,

    @field:SerializedName("tags")
    @ColumnInfo(name = COL_TAGS)
    var tags: List<String>? = null,

    @field:SerializedName("coat")
    @ColumnInfo(name = COL_COAT)
    var coat: String? = null,

    @field:SerializedName("environment")
    @ColumnInfo(name = COL_ENV)
    var environmentEntity: EnvironmentEntity? = null,

    @field:SerializedName("size")
    @ColumnInfo(name = COL_SIZE)
    var size: String? = null,

    @field:SerializedName("species")
    @ColumnInfo(name = COL_SPECIES)
    var species: String? = null,

    @field:SerializedName("contact")
    @ColumnInfo(name = COL_CONTACT)
    var contactEntity: ContactEntity? = null,

    @field:SerializedName("name")
    @ColumnInfo(name = COL_NAME, index = true)
    var name: String? = null,

    @field:SerializedName("attributes")
    @ColumnInfo(name = COL_ATTR)
    var attributesEntity: AttributesEntity? = null,

    @field:SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = COL_ID, index = true)
    @get:Bindable
    var id: Int = -1,

    @field:SerializedName("published_at")
    @ColumnInfo(name = COL_PUBLISHED_AT, index = true)
    var publishedAt: OffsetDateTime? = null,

    @field:SerializedName("age")
    @ColumnInfo(name = COL_AGE)
    var age: String? = null,

    @field:SerializedName("status")
    @ColumnInfo(name = COL_STATUS)
    var status: String? = null,

    @field:SerializedName("description")
    @ColumnInfo(name = COL_DESCRIPTION)
    var desc: String? = null,

    @field:SerializedName("url")
    @ColumnInfo(name = COL_URL)
    var url: String? = null,

    @field:SerializedName("distance")
    @ColumnInfo(name = COL_DISTANCE, index = true)
    var distance: Double = 0.0,

    @field:SerializedName("videos")
    @ColumnInfo(name = COL_VIDEOS)
    var videos: List<VideoItemEntity>? = null
) : BaseObservable(), Parcelable {

    @get:Bindable
    @ColumnInfo(name = COL_BOOK_MARK_STATUS, index = true)
    @field:SerializedName("bookmark_status")
    var bookmarkStatus: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.bookmarkStatus)
        }

    @ColumnInfo(name = COL_BOOK_MARK_AT, index = true)
    @field:SerializedName("bookmarked_at")
    var bookmarkedAt: Long = System.currentTimeMillis()

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

    fun getPublishedAtInLong(): Long {
        return publishedAt?.toInstant()?.toEpochMilli() ?: 0L
    }

    object TableInfo {
        const val TABLE_NAME = "pets"
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