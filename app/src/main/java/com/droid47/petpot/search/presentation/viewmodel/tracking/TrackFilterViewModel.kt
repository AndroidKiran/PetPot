package com.droid47.petpot.search.presentation.viewmodel.tracking

interface TrackFilterViewModel {
    fun trackFilterChange(category: String)
    fun trackFilterItemSelected(selected: Boolean)
    fun trackFilterApplied()
    fun trackFilterReset()
    fun trackFilterClose()
}