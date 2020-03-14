package com.droid47.petgoogle.search.data.models.type

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.droid47.petgoogle.BR
import com.droid47.petgoogle.search.data.models.type.PetTypeEntity.Companion.TABLE_NAME
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = TABLE_NAME)
@Parcelize
class PetTypeEntity : BaseObservable(), Parcelable {

    @field:SerializedName("name")
    @get:Bindable
    @PrimaryKey
    @ColumnInfo(name = COL_NAME, index = true)
    var name: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @field:SerializedName("coats")
    @get:Bindable
    @ColumnInfo(name = COL_COATS)
    var coats: List<String>? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.coats)
        }

    @field:SerializedName("genders")
    @get:Bindable
    @ColumnInfo(name = COL_GENDERS)
    var genders: List<String>? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.genders)
        }

    @field:SerializedName("colors")
    @get:Bindable
    @ColumnInfo(name = COL_COLORS)
    var colors: List<String>? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.colors)
        }

    @get:Bindable
    @ColumnInfo(name = COL_BREEDS)
    var breeds: List<String>? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.breeds)
        }

    @get:Bindable
    @ColumnInfo(name = COL_SELECTED, index = true)
    var selected: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

    @ColumnInfo(name = COL_ANIMAL_ORDER, index = true)
    var animalOrder: Int = 0
        @Bindable(value = ["name"])
        set(value) {
            field = value
            notifyPropertyChanged(BR.animalOrder)
        }
        get() = when (name.toLowerCase()) {
            "Dog".toLowerCase() -> 1
            "Cat".toLowerCase() -> 2
            "Rabbit".toLowerCase() -> 3
            "Small & Furry".toLowerCase() -> 4
            "Horse".toLowerCase() -> 5
            "Bird".toLowerCase() -> 6
            "Scales, Fins & Other".toLowerCase() -> 7
            "Barnyard".toLowerCase() -> 8
            else -> 9
        }

    @get:Bindable
    var size = listOf("small", "medium", "large", "xlarge")
        set(value) {
            field = value
            notifyPropertyChanged(BR.size)
        }

    @get:Bindable
    var age = listOf("baby", "young", "adult", "senior")
        set(value) {
            field = value
            notifyPropertyChanged(BR.age)
        }

    @get:Bindable
    var status = listOf("adoptable", "adopted", "found")
        set(value) {
            field = value
            notifyPropertyChanged(BR.status)
        }

    companion object {
        const val TABLE_NAME = "pet_type"
        const val COL_NAME = "name"
        const val COL_COATS = "coats"
        const val COL_BREEDS = " breed"
        const val COL_COLORS = "colors"
        const val COL_GENDERS = "genders"
        const val COL_SELECTED = "selected"
        const val COL_ANIMAL_ORDER = "animal_order"
    }

}