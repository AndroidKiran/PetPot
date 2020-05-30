package com.droid47.petfriend.search.presentation.ui.widgets

import android.app.Application
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.applyBracketFilter
import com.droid47.petfriend.base.extensions.isNotEmpty
import com.droid47.petfriend.base.extensions.stringSentenceCase
import com.droid47.petfriend.search.data.models.search.AddressEntity
import com.droid47.petfriend.search.data.models.search.AttributesEntity
import com.droid47.petfriend.search.data.models.search.BreedEntity
import com.droid47.petfriend.search.data.models.search.EnvironmentEntity
import java.util.*

fun BreedEntity.bindBreed(application: Application?): String {
    val context = application ?: return ""
    val stringBuilder = StringBuilder()
    if (unknown == true) {
        stringBuilder.append(context.getString(R.string.un_known))
    } else {
        stringBuilder.append(primary)
        if (secondary.isNotEmpty()) {
            stringBuilder.append(" & ${secondary}")
        }
        if (mixed == true) {
            stringBuilder.append(" ${context.getString(R.string.mix)}")
        }
    }
    return stringBuilder.toString().stringSentenceCase()
}

fun AddressEntity.bindAddress(application: Application?): String {
    val context = application ?: return ""
    val stringBuilder = StringBuilder()
    if (this.address1.isNotEmpty()) {
        stringBuilder.append("${this.address1},\n")
    } else if (this.address2.isNotEmpty()) {
        stringBuilder.append("${this.address2},\n")
    }

    if (this.city.isNotEmpty()) {
        stringBuilder.append("${context.getString(R.string.city)} ${this.city?.toUpperCase(Locale.US)},\n")
    }

    if (this.state.isNotEmpty()) {
        stringBuilder.append("${context.getString(R.string.state)} ${this.state?.toUpperCase(Locale.US)},\n")
    }

    if (this.postcode.isNotEmpty()) {
        stringBuilder.append("${context.getString(R.string.post_code)} ${this.postcode},\n")
    }

    if (this.country.isNotEmpty()) {
        stringBuilder.append(
            "${context.getString(R.string.country)} ${this.country?.toUpperCase(
                Locale.US
            )}"
        )
    }
    return stringBuilder.toString()
}

fun AddressEntity.getAddress(): String {
    val stringBuilder = StringBuilder()
    if (this.address1.isNotEmpty()) {
        stringBuilder.append("${this.address1},")
    } else if (this.address2.isNotEmpty()) {
        stringBuilder.append("${this.address2},")
    }

    if (this.city.isNotEmpty()) {
        stringBuilder.append("${this.city?.toUpperCase(Locale.US)},")
    }

    if (this.state.isNotEmpty()) {
        stringBuilder.append("${this.state?.toUpperCase(Locale.US)},")
    }

    if (this.postcode.isNotEmpty()) {
        stringBuilder.append("${this.postcode},")
    }

    if (this.country.isNotEmpty()) {
        stringBuilder.append("${this.country?.toUpperCase(Locale.US)}")
    }
    return stringBuilder.toString()
}


fun List<String>.bindCharacteristics(): String {
    if (isNotEmpty()) {
        return toString().applyBracketFilter()
    }
    return ""
}

fun AttributesEntity.bindAttributes(application: Application?): String {
    val context = application ?: return ""
    val stringBuilder = StringBuilder()
    if (this.shotsCurrent == true) {
        stringBuilder.append(context.getString(R.string.vacci_upto_date))
    }

    if (this.spayedNeutered == true) {
        if (stringBuilder.isNotEmpty()) {
            stringBuilder.append(", ")
        }
        stringBuilder.append(context.getString(R.string.spayed_neutered))
    }

    if (this.declawed == true) {
        if (stringBuilder.isNotEmpty()) {
            stringBuilder.append(", ")
        }
        stringBuilder.append(context.getString(R.string.de_clawed))
    }
    return stringBuilder.toString().trim()
}

fun EnvironmentEntity?.bindEnvironment(
    application: Application?,
    _type: String?
): String {
    val environment = this ?: return ""
    val context = application ?: return ""
    val stringBuilder = StringBuilder()
    if (environment.dogs == true) {
        if (_type?.contentEquals("dog") == true) {
            stringBuilder.append(context.getString(R.string.other_dogs))
        } else {
            stringBuilder.append(context.getString(R.string.dogs))
        }
    }

    if (environment.cats == true) {
        if (stringBuilder.isNotEmpty()) {
            stringBuilder.append(", ")
        }
        if (_type?.contentEquals("cat") == true) {
            stringBuilder.append(context.getString(R.string.other_cats))
        } else {
            stringBuilder.append(context.getString(R.string.cats))
        }
    }

    if (environment.children == true) {
        if (stringBuilder.isNotEmpty()) {
            stringBuilder.append(", ")
        }
        stringBuilder.append(context.getString(R.string.children))
    }
    return stringBuilder.toString().trim()
}