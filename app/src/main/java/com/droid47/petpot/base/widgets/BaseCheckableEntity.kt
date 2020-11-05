package com.droid47.petpot.base.widgets

import android.os.Parcelable

abstract class BaseCheckableEntity : Parcelable {
    abstract var name: String?
    abstract var selected: Boolean
    abstract var filterApplied: Boolean
}