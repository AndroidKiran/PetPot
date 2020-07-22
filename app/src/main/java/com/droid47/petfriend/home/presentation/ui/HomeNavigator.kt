package com.droid47.petfriend.home.presentation.ui

import androidx.navigation.NavController
import androidx.navigation.Navigator
import com.droid47.petfriend.R
import com.droid47.petfriend.base.navigator.INavigator
import com.droid47.petfriend.search.presentation.ui.BookmarkFragmentDirections
import com.droid47.petfriend.search.presentation.ui.SearchFragmentDirections
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
}