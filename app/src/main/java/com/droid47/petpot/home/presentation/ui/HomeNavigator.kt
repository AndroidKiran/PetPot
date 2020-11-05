package com.droid47.petpot.home.presentation.ui

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigator
import com.droid47.petpot.R
import com.droid47.petpot.base.navigator.INavigator
import com.droid47.petpot.search.presentation.ui.BookmarkFragmentDirections
import com.droid47.petpot.search.presentation.ui.SearchFragmentDirections
import javax.inject.Inject

class HomeNavigator @Inject constructor() : INavigator {

    private lateinit var navController: NavController

    fun inject(navController: NavController) {
        this.navController = navController
    }

    fun toSearchFromHome() {
        navController.navigate(R.id.navigation_search)
    }

    fun toFavouriteFromHome() {
        navController.navigate(R.id.navigation_favorite)
    }

    fun toSettingsFromHome() {
        navController.navigate(R.id.navigation_settings)
    }

    fun toAboutUsFromHome() {
        navController.navigate(R.id.navigation_about_us)
    }

    fun toPetDetailsFromSearch(petId: Int, extras: Navigator.Extras? = null) {
        if (extras == null) {
            navController.navigate(SearchFragmentDirections.toPetDetails(petId))
        } else {
            navController.navigate(SearchFragmentDirections.toPetDetails(petId), extras)
        }
    }

    fun toPetDetailsFromFavorite(petId: Int, extras: Navigator.Extras? = null) {
        if (extras == null) {
            navController.navigate(BookmarkFragmentDirections.toPetDetails(petId))
        } else {
            navController.navigate(BookmarkFragmentDirections.toPetDetails(petId), extras)
        }
    }

    fun toOrganizationFromSearch(extras: Navigator.Extras? = null) {
        if (extras == null) {
            navController.navigate(SearchFragmentDirections.toOrganizations())
        } else {
            navController.navigate(SearchFragmentDirections.toOrganizations(), extras)
        }
    }

    fun toLauncherFromHome(deepLinkBundle: Bundle, extras: Navigator.Extras?) {
        if (extras == null) {
            navController.navigate(SettingFragmentDirections.toLauncher(deepLinkBundle))
        } else {
            navController.navigate(SettingFragmentDirections.toLauncher(deepLinkBundle), extras)
        }
    }
}