package com.droid47.petpot.search.presentation.ui.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSpinner
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.search.presentation.viewmodel.PetSpinnerAndLocationViewModel

class PetSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.imageButtonStyle
) : AppCompatSpinner(context, attrs, defStyleAttr) {

    private var petSpinnerAndLocationViewModel: PetSpinnerAndLocationViewModel? = null

    override fun setSelection(position: Int) {
        if (getPreference()?.getSelectedPetPosition() != position) {
            getPreference()?.saveSelectPetPosition(position)
            super.setSelection(position)
        }
    }

    override fun getSelectedItemPosition(): Int =
        getPreference()?.getSelectedPetPosition() ?: 0

    fun setPetSpinnerViewModelAndLocation(petSpinnerAndLocationViewModel: PetSpinnerAndLocationViewModel) {
        this.petSpinnerAndLocationViewModel = petSpinnerAndLocationViewModel
    }

    fun onPetSelected(item: String) {
        if(item != getSelectedPet()) {
            petSpinnerAndLocationViewModel?.onPetSelected(item)
            getPreference()?.saveSelectedPet(item)
        } else {
//            petSpinnerAndLocationViewModel?.onExistingPetSelected(item)
        }
    }

    private fun getSelectedPet() = getPreference()?.getSelectedPet()

    private fun getPreference(): LocalPreferencesRepository? =
        petSpinnerAndLocationViewModel?.localPreferenceDataSource

}