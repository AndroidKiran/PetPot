package com.droid47.petfriend.organization.data.models

import android.os.Parcelable
import android.text.TextUtils
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.droid47.petfriend.base.extensions.isNotEmpty
import com.droid47.petfriend.base.widgets.BaseCheckableEntity
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_ADOPTION
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_DISTANCE
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_EMAIL
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_FILTER_APPLIED
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_ID
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_MISSION_STATEMENT
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_NAME
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_PHONE
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_PHOTOS
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_SELECTED
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_SHARE_URL
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_SOCIAL_MEDIA
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_WEBSITE
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity.TableInfo.TABLE_NAME
import com.droid47.petfriend.search.data.models.search.AddressEntity
import com.droid47.petfriend.search.data.models.search.PhotosItemEntity
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = TABLE_NAME)
data class OrganizationCheckableEntity(
    @Embedded
    @field:SerializedName("address")
    var address: AddressEntity? = null,
    @ColumnInfo(name = COL_ADOPTION)
    @field:SerializedName("adoption")
    var adoptionEntity: AdoptionEntity? = null,
    @ColumnInfo(name = COL_DISTANCE)
    @field:SerializedName("distance")
    var distance: Double? = null,
    @ColumnInfo(name = COL_EMAIL)
    @field:SerializedName("email")
    var email: String? = null,
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = COL_ID, index = true)
    @field:SerializedName("id")
    val id: String,
    @ColumnInfo(name = COL_MISSION_STATEMENT)
    @field:SerializedName("mission_statement")
    var missionStatement: String? = null,
    @ColumnInfo(name = COL_NAME)
    @field:SerializedName("name")
    override var name: String? = null,
    @ColumnInfo(name = COL_PHONE)
    @field:SerializedName("phone")
    var phone: String? = null,
    @ColumnInfo(name = COL_PHOTOS)
    @field:SerializedName("photos")
    var photos: List<PhotosItemEntity>? = null,
    @ColumnInfo(name = COL_SOCIAL_MEDIA)
    @field:SerializedName("social_media")
    var socialMediaEntity: SocialMediaEntity? = null,
    @ColumnInfo(name = COL_SHARE_URL)
    @field:SerializedName("url")
    var shareUrl: String? = null,
    @ColumnInfo(name = COL_WEBSITE)
    @field:SerializedName("website")
    var website: String? = null,
    @ColumnInfo(name = COL_SELECTED)
    override var selected: Boolean = false,
    @ColumnInfo(name = COL_FILTER_APPLIED)
    override var filterApplied: Boolean = false
) : BaseCheckableEntity() {

    @Parcelize
    data class AdoptionEntity(
        @field:SerializedName("policy")
        var policy: String? = null,
        @field:SerializedName("url")
        var url: String? = null
    ) : Parcelable

    @Parcelize
    data class SocialMediaEntity(
        @field:SerializedName("facebook")
        var facebook: String? = null,
        @field:SerializedName("instagram")
        var instagram: String? = null,
        @field:SerializedName("pinterest")
        var pinterest: String? = null,
        @field:SerializedName("twitter")
        var twitter: String? = null,
        @field:SerializedName("youtube")
        var youtube: String? = null
    ) : Parcelable

    fun isSocialMediaAvailable(): Boolean {
        return !TextUtils.isEmpty(socialMediaEntity?.facebook)
                || !TextUtils.isEmpty(socialMediaEntity?.instagram)
                || !TextUtils.isEmpty(socialMediaEntity?.pinterest)
                || !TextUtils.isEmpty(socialMediaEntity?.twitter)
                || !TextUtils.isEmpty(socialMediaEntity?.youtube)
    }

    fun getFullAddress(): String {
        return StringBuilder().append(
            when {
                address?.address1?.isNotEmpty() ?: false -> address?.address1?.plus(",") ?: ""
                address?.address2?.isNotEmpty() ?: false -> address?.address2?.plus(",") ?: ""
                else -> ""
            }
        )
            .append(address?.city?.plus(",") ?: "")
            .append(address?.state?.plus(",") ?: "")
            .append(address?.country?.plus(",") ?: "")
            .append(address?.postcode?.plus(",") ?: "")
            .toString()
    }

    object TableInfo {
        const val TABLE_NAME = "organizations"
        const val COL_ADDRESS = "address"
        const val COL_CITY = "city"
        const val COL_STATE = "state"
        const val COL_ADOPTION = "adoption"
        const val COL_DISTANCE = "distance"
        const val COL_EMAIL = "email"
        const val COL_ID = "id"
        const val COL_MISSION_STATEMENT = "mission_statement"
        const val COL_NAME = "name"
        const val COL_PHONE = "phone"
        const val COL_PHOTOS = "photos"
        const val COL_SOCIAL_MEDIA = "social_media"
        const val COL_SHARE_URL = "share_url"
        const val COL_WEBSITE = "website"
        const val COL_SELECTED = "selected"
        const val COL_FILTER_APPLIED = "filter_applied"
        const val COL_LAT_LONG = "lat_long"
    }
}
