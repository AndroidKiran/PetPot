package com.droid47.petgoogle.search.presentation.widgets

import android.app.Application
import com.droid47.petgoogle.R
import com.droid47.petgoogle.base.extensions.AppUtils
import com.droid47.petgoogle.base.extensions.isNotEmpty
import com.droid47.petgoogle.base.extensions.stringSentenceCase
import com.droid47.petgoogle.search.data.models.search.AddressEntity
import com.droid47.petgoogle.search.data.models.search.AttributesEntity
import com.droid47.petgoogle.search.data.models.search.BreedEntity
import com.droid47.petgoogle.search.data.models.search.EnvironmentEntity
import java.util.*

object PetInfoUtil {

    @JvmStatic
    fun bindBreed(application: Application?, _breedEntity: BreedEntity?): String {
        val breed = _breedEntity ?: return ""
        val context = application ?: return ""
        val stringBuilder = StringBuilder()
        if (breed.unknown == true) {
            stringBuilder.append(context.getString(R.string.un_known))
        } else {
            stringBuilder.append(breed.primary)
            if (breed.secondary.isNotEmpty()) {
                stringBuilder.append(" & ${breed.secondary}")
            }
            if (breed.mixed == true) {
                stringBuilder.append(" ${context.getString(R.string.mix)}")
            }
        }
        return stringBuilder.toString().stringSentenceCase()
    }

    @JvmStatic
    fun bindAddress(_addressEntity: AddressEntity?): String {
        val address = _addressEntity ?: return ""
        val stringBuilder = StringBuilder()
        if (address.address1.isNotEmpty()) {
            stringBuilder.append("${address.address1}\n")
        } else if (address.address2.isNotEmpty()) {
            stringBuilder.append("${address.address2}\n")
        }

        if (address.state.isNotEmpty()) {
            stringBuilder.append("State: ${address.state?.toUpperCase(Locale.US)}\n")
        }

        if (address.country.isNotEmpty()) {
            stringBuilder.append("Country: ${address.country?.toUpperCase(Locale.US)}\n")
        }

        if (address.postcode.isNotEmpty()) {
            stringBuilder.append("Post code: ${address.postcode}\n")
        }
        return stringBuilder.toString()
    }

    @JvmStatic
    fun bindCharacteristics(_tags: List<String>?): String {
        val tags = _tags ?: return ""
        if (tags.isNotEmpty()) {
            return AppUtils.applyBracketFilter(tags.toString())
        }
        return ""
    }

    @JvmStatic
    fun bindAttributes(application: Application?, _attributesEntity: AttributesEntity?): String {
        val attributes = _attributesEntity ?: return ""
        val context = application ?: return ""
        val stringBuilder = StringBuilder()
        if (attributes.shotsCurrent == true) {
            stringBuilder.append(context.getString(R.string.vacci_upto_date))
        }

        if (attributes.spayedNeutered == true) {
            if (stringBuilder.isNotEmpty()) {
                stringBuilder.append(", ")
            }
            stringBuilder.append(context.getString(R.string.spayed_neutered))
        }

        if (attributes.declawed == true) {
            if (stringBuilder.isNotEmpty()) {
                stringBuilder.append(", ")
            }
            stringBuilder.append(context.getString(R.string.de_clawed))
        }
        return stringBuilder.toString().trim()
    }

    @JvmStatic
    fun bindEnvironment(
        application: Application?,
        _environmentEntity: EnvironmentEntity?,
        _type: String?
    ): String {
        val environment = _environmentEntity ?: return ""
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
}