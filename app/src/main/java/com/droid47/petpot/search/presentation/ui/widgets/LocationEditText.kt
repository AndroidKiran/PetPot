package com.droid47.petpot.search.presentation.ui.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.search.presentation.viewmodel.PetSpinnerAndLocationViewModel

class LocationEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var petSpinnerAndLocationViewModel: PetSpinnerAndLocationViewModel? = null

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        val newText = text?.toString() ?: ""
        if (newText != getSelectedLocation()) {
            petSpinnerAndLocationViewModel?.onLocationSelected(newText)
            getPreference()?.saveLocation(newText)
        }
    }


    fun setPetSpinnerViewModelAndLocation(petSpinnerAndLocationViewModel: PetSpinnerAndLocationViewModel) {
        this.petSpinnerAndLocationViewModel = petSpinnerAndLocationViewModel
    }

    private fun getSelectedLocation() = getPreference()?.getLocation() ?: ""

    private fun getSelectedPet() = getPreference()?.getSelectedPet()

    private fun getPreference(): LocalPreferencesRepository? =
        petSpinnerAndLocationViewModel?.localPreferenceDataSource
}