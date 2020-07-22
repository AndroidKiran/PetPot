package com.droid47.petfriend.organization.data.models

import android.os.Parcelable
import com.droid47.petfriend.search.data.models.search.PaginationEntity
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrganizationResponseEntity(
    @SerializedName("organizations")
    var organizationEntities: List<OrganizationCheckableEntity>? = null,
    @SerializedName("pagination")
    var paginationEntity: PaginationEntity? = null
) : Parcelable