package com.droid47.petpot.base.firebase

import android.app.Activity

interface IFirebaseManager {

    fun setCollectionEnabled()

    fun setCollectionDisabled()

    fun sendScreenView(screenName: String, className: String, activity: Activity)

    fun logUiEvent(itemId: String, action: String)
}

object AnalyticsScreens {
    const val SEARCH_SCREEN = "SearchScreen"
    const val FILTER_SCREEN = "FilterScreen"
    const val FAVOURITE_PETS_SCREEN = "FavouritePetsScreen"
    const val SIMILAR_PETS_SCREEN = "SimilarPetsScreen"
    const val PET_DETAILS_SCREEN = "PetDetailsScreen"
    const val ORGANIZATIONS_SCREEN = "OrgnaizationsScreen"
    const val TNC_SCREEN = "TnCScreen"
    const val SPLASH_SCREEN = "SplashScreen"
    const val LAUNCHER_SCREEN = "LauncherScreen"
    const val ON_BOARDING_SCREEN = "OnBoardingScreen"
    const val HOME_SCREEN = "HomeScreen"
    const val NAVIGATION_SCREEN = "NavigationScreen"
    const val ABOUT_SCREEN = "AboutScreen"
}

object AnalyticsAction {
    const val BOOKMARK_TO_DETAILS = "Bookmark To Details"
    const val CLICK = "ClickAction"
    const val SWIPE = "SwipeAction"
    const val AUTO = "AutoAction"
    const val SCROLL = "ScrollAction"
    const val ENTRY = "EnterAction"

    // Launcher Transitions
    const val ON_BOARDING_TO_TNC_TRANSITION = "On boarding To Tnc Transition"
    const val SPLASH_TO_ON_BOARDING = "Splash To On Boarding Transition"
    const val SPLASH_TO_HOME = "Splash To Home Transition"
    const val SPLASH_TO_TNC = "Splash To Tnc Transition"
    const val TNC_TO_HOME = "Tnc To Home"

    // home Transitions
    const val SEARCH_TO_DETAILS_TRANSITION = "Search To Details Transition"
    const val SEARCH_TO_FILTER_TRANSITION = "Search To Filter Transition"
    const val PET_DETAILS_TO_MAP = "Pet Details To Map"
}