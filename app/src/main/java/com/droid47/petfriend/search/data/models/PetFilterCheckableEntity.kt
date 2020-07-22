package com.droid47.petfriend.search.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.droid47.petfriend.base.widgets.BaseCheckableEntity
import com.droid47.petfriend.search.data.models.PetFilterCheckableEntity.Companion.TABLE_NAME
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = TABLE_NAME)
@Parcelize
data class PetFilterCheckableEntity(
    @ColumnInfo(name = COL_NAME)
    @field:SerializedName(COL_NAME)
    override var name: String? = null,

    @ColumnInfo(name = COL_TYPE, index = true)
    @field:SerializedName(COL_TYPE)
    val type: String? = null,

    @ColumnInfo(name = COL_SELECTED, index = true)
    @field:SerializedName(COL_SELECTED)
    override var selected: Boolean = false,

    @ColumnInfo(name = COL_FILTER_APPLIED, index = true)
    @field:SerializedName(COL_FILTER_APPLIED)
    override var filterApplied: Boolean = false

) : BaseCheckableEntity() {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_ID, index = true)
    @field:SerializedName(COL_ID)
    var id: Long = 0


    companion object {
        const val TABLE_NAME = "filter_items"
        const val COL_NAME = "name"
        const val COL_ID = "_id"
        const val COL_TYPE = "type"
        const val COL_SELECTED = "selected"
        const val COL_FILTER_APPLIED = "filter_applied"
    }
}