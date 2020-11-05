package com.droid47.petpot.launcher.presentation.ui

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigator
import com.droid47.petpot.R
import com.droid47.petpot.base.navigator.INavigator
import javax.inject.Inject

class LauncherNavigator @Inject constructor() : INavigator {

    private lateinit var navController: NavController

    fun inject(navController: NavController) {
        this.navController = navController
    }

    fun toSplash(bundle: Bundle?) {
        navController.navigate(R.id.navigation_splash, bundle)
    }

    fun toIntroFromSplash(extras: Navigator.Extras?) {
        if (extras == null) {
            navController.navigate(SplashFragmentDirections.toIntro())
        } else {
            navController.navigate(SplashFragmentDirections.toIntro(), extras)
        }
    }

    fun toTncFromSplash(extras: Navigator.Extras?) {
        if (extras == null) {
            navController.navigate(SplashFragmentDirections.toTnc())
        } else {
            navController.navigate(SplashFragmentDirections.toTnc(), extras)
        }
    }

    fun toHomeFromSplash(deepLinkBundle: Bundle, extras: Navigator.Extras?) {
        if (extras == null) {
            navController.navigate(SplashFragmentDirections.toHome(deepLinkBundle))
        } else {
            navController.navigate(SplashFragmentDirections.toHome(deepLinkBundle), extras)
        }
    }

    fun toHomeFromTnc(deepLinkBundle: Bundle, extras: Navigator.Extras?) {
        if (extras == null) {
            navController.navigate(TnCFragmentDirections.toHome(deepLinkBundle))
        } else {
            navController.navigate(TnCFragmentDirections.toHome(deepLinkBundle), extras)
        }
    }

    fun toTncFromIntro(extras: Navigator.Extras?) {
        if (extras == null) {
            navController.navigate(HomeBoardFragmentDirections.toTnc())
        } else {
            navController.navigate(HomeBoardFragmentDirections.toTnc(), extras)
        }
    }

}