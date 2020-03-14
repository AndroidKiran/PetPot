package com.droid47.petgoogle.search.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.droid47.petgoogle.search.data.models.FilterItemEntity.Companion.TABLE_NAME
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

@Entity(tableName = TABLE_NAME)
@Parcelize
data class FilterItemEntity(
    @ColumnInfo(name = COL_NAME)
    @field:SerializedName(COL_NAME)
    val name: String,

    @ColumnInfo(name = COL_TYPE)
    @field:SerializedName(COL_TYPE)
    val type: String,

    @ColumnInfo(name = COL_SELECTED)
    @field:SerializedName(COL_SELECTED)
    var selected: Boolean
): Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_ID, index = true)
    var id: Long = 0

    companion object {
        const val TABLE_NAME = "filter_items"
        const val COL_NAME = "name"
        const val COL_ID = "_id"
        const val COL_TYPE = "type"
        const val COL_SELECTED = "selected"
    }
}