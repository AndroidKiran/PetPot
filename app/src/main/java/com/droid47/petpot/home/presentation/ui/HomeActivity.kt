package com.droid47.petpot.home.presentation.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.droid47.petpot.R
import com.droid47.petpot.app.PetApplication
import com.droid47.petpot.base.extensions.themeInterpolator
import com.droid47.petpot.base.extensions.viewModelProvider
import com.droid47.petpot.base.firebase.AnalyticsScreens
import com.droid47.petpot.base.firebase.CrashlyticsExt
import com.droid47.petpot.base.livedata.NetworkConnectionLiveData
import com.droid47.petpot.base.widgets.*
import com.droid47.petpot.base.widgets.inAppUpdate.InAppUpdateManager
import com.droid47.petpot.base.widgets.inAppUpdate.InAppUpdateManager.Companion.IN_APP_UPDATE_REQUEST_CODE
import com.droid47.petpot.databinding.ActivityHomeBinding
import com.droid47.petpot.home.data.AppUpgradeEntity
import com.droid47.petpot.home.presentation.di.HomeActivityComponent
import com.droid47.petpot.home.presentation.viewmodels.HomeViewModel
import com.droid47.petpot.home.presentation.viewmodels.HomeViewModel.Companion.EVENT_NAVIGATE_BACK
import com.droid47.petpot.home.presentation.viewmodels.HomeViewModel.Companion.EVENT_TOGGLE_NAVIGATION
import com.droid47.petpot.search.data.models.search.PetEntity
import com.droid47.petpot.workmanagers.notification.NotificationModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.google.android.play.core.install.model.AppUpdateType
import java.util.*
import javax.inject.Inject


class HomeActivity : BaseBindingActivity<ActivityHomeBinding, HomeViewModel>(),
    NavController.OnDestinationChangedListener, NavigationHost {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var homeComponent: HomeActivityComponent
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private var currentNavId: Int = NAV_ID_NONE
    private var inAppUpdateManager: InAppUpdateManager? = null

    private val arg by navArgs<HomeActivityArgs>()
    private val deepLinkBundle: Bundle by lazy(LazyThreadSafetyMode.NONE) { arg.deepLinkBundle }

    private val bottomNavDrawer: BottomNavDrawerFragment by lazy(LazyThreadSafetyMode.NONE) {
        supportFragmentManager.findFragmentById(R.id.bottom_nav_drawer) as BottomNavDrawerFragment
    }

    private val homeViewModel: HomeViewModel by lazy {
        viewModelProvider<HomeViewModel>(factory)
    }

    override fun getViewModel(): HomeViewModel = homeViewModel

    override fun getLayoutId(): Int = R.layout.activity_home

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.lifecycleOwner = this@HomeActivity
            it.homeViewModel = getViewModel()
        }
    }

    override fun injectComponent() {
        homeComponent = (application as PetApplication).provideHomeComponent().also {
            it.inject(this@HomeActivity)
        }
    }

    override fun getClassName(): String = HomeActivity::class.java.simpleName

    override fun getScreenName(): String = AnalyticsScreens.HOME_SCREEN

    override fun onCreate(savedInstanceState: Bundle?) {
        initTransitions()
        super.onCreate(savedInstanceState)
        setUpViews()
        subscribeToLiveData()
        savedInstanceState?.let {
            currentNavId =  it.getInt(NotificationModel.EXTRA_NAVIGATION_FRAGMENT_ID,
                R.id.navigation_home)
            deepLinkBundle.putInt( NotificationModel.EXTRA_NAVIGATION_FRAGMENT_ID, currentNavId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IN_APP_UPDATE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                // If the update is cancelled by the user,
                // you can request to start the update again.
                inAppUpdateManager?.checkForUpdate()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        currentNavId = destination.id
//        getCurrentFragment()?.apply {
//            exitTransition = MaterialFadeThrough().apply {
//                duration = resources.getInteger(R.integer.pet_motion_default_large).toLong()
//            }
//
//            reenterTransition = MaterialElevationScale(true).apply {
//                duration = resources.getInteger(R.integer.pet_motion_default_large).toLong()
//            }
//        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(NotificationModel.EXTRA_NAVIGATION_FRAGMENT_ID, currentNavId)
        super.onSaveInstanceState(outState)
    }

    private fun setUpViews() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = findNavController(R.id.nav_host_fragment).apply {
            addOnDestinationChangedListener(this@HomeActivity)
        }
        bottomNavDrawer.getViewDataBinding()
            .navigationMenuView.setupWithNavController(navController)
        getViewModel().homeNavigator.inject(navController)
    }

    override fun registerBottomAppbarWithNavigation(bottomAppBar: BottomAppBar) {
        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        bottomAppBar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun initTransitions() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        findViewById<View>(android.R.id.content).transitionName =
            getString(R.string.activity_transition)
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
            interpolator = themeInterpolator(R.attr.motionInterpolatorPersistent)
            pathMotion = MaterialArcMotion()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = resources.getInteger(R.integer.pet_motion_duration_small).toLong()
            interpolator = themeInterpolator(R.attr.motionInterpolatorPersistent)
            pathMotion = MaterialArcMotion()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }
        window.sharedElementExitTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = resources.getInteger(R.integer.pet_motion_duration_small).toLong()
            interpolator = themeInterpolator(R.attr.motionInterpolatorPersistent)
            pathMotion = MaterialArcMotion()
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
        }
    }

    private fun subscribeToLiveData() {
        getViewModel().upgradeStatusLiveData.run {
            removeObserver(appUpgradeObserver)
            observe(this@HomeActivity, appUpgradeObserver)
        }

        getViewModel().eventLiveData.run {
            removeObserver(eventObserver)
            observe(this@HomeActivity, eventObserver)
        }

        getViewModel().networkConnectionLiveData.run {
            removeObserver(networkConnectionObserver)
            observe(this@HomeActivity, networkConnectionObserver)
        }

        inAppUpdateManager?.appUpdateManagerLiveData.run {

        }
    }

    private val appUpgradeObserver = Observer<BaseStateModel<AppUpgradeEntity>> {
        val statusModel = it ?: return@Observer
        when (statusModel) {
            is Success -> {
                triggerInAppUpdateManager(statusModel.data)
            }
            is Failure -> {
                val erro = statusModel.error
            }
            is Loading -> TODO()
            is Empty -> TODO()
        }
    }

    private val eventObserver = Observer<Long> {
        when (it ?: return@Observer) {
            EVENT_TOGGLE_NAVIGATION -> bottomNavDrawer.toggle()
            EVENT_NAVIGATE_BACK -> navController.popBackStack()
        }
    }

    private val networkConnectionObserver = Observer<NetworkConnectionLiveData.NetworkState> {
        val netWorkState = it ?: return@Observer
        val anchorView =
            getCurrentFragment()?.getSnackBarAnchorView() ?: getViewDataBinding().cdlHome
        when (netWorkState) {
            is NetworkConnectionLiveData.NetworkState.Connected ->
                Snackbar.make(
                    getViewDataBinding().cdlHome,
                    getString(R.string.active_connection),
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(anchorView)
                    .show()

            is NetworkConnectionLiveData.NetworkState.ConnectionLost,
            is NetworkConnectionLiveData.NetworkState.DisConnected ->
                Snackbar.make(
                    getViewDataBinding().cdlHome,
                    getString(R.string.non_active_connection),
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(anchorView)
                    .show()
        }
    }

    private fun triggerInAppUpdateManager(upgradeEntity: AppUpgradeEntity) {
        InAppUpdateManager.Builder(
            appCompatActivity = this@HomeActivity,
            resumeUpdates = true,
            updateType = upgradeEntity.updateType,
            snackBarMessage = if (AppUpdateType.FLEXIBLE == upgradeEntity.updateType) {
                getString(R.string.update_downloaded)
            } else {
                ""
            },
            snackBarAction = if (AppUpdateType.FLEXIBLE == upgradeEntity.updateType) {
                getString(R.string.restart).toUpperCase(Locale.US)
            } else {
                ""
            }
        ).build().run {
            checkForUpdate()
        }
    }

    private fun getCurrentFragment(): BaseBindingFragment<*, *, *>? =
        navHostFragment.childFragmentManager
            .primaryNavigationFragment as? BaseBindingFragment<*, *, *>

    private fun navigateTo(bundle: Bundle) {
        when (bundle.getInt(
            NotificationModel.EXTRA_NAVIGATION_FRAGMENT_ID,
            R.id.navigation_search
        )) {
            R.id.navigation_search -> {
                if (navController.currentDestination?.id != R.id.navigation_search) {
                    getViewModel().homeNavigator.toSearchFromHome()
                }
            }

            R.id.navigation_settings -> {
                if (navController.currentDestination?.id != R.id.navigation_settings) {
                    getViewModel().homeNavigator.toSettingsFromHome()
                }
            }

            R.id.navigation_about_us -> {
                if (navController.currentDestination?.id != R.id.navigation_about_us) {
                    getViewModel().homeNavigator.toAboutUsFromHome()
                }
            }

            R.id.navigation_pet_details -> {
                val petEntity: PetEntity =
                    bundle.getParcelable(NotificationModel.EXTRA_PET_ENTITY) ?: return

                when (getCurrentFragment()?.getFragmentNavId()) {
                    R.id.navigation_favorite -> getViewModel().homeNavigator.toPetDetailsFromFavorite(
                        petEntity.id
                    )
                    R.id.navigation_search -> getViewModel().homeNavigator.toPetDetailsFromSearch(
                        petEntity.id
                    )
                    else -> getViewModel().homeNavigator.toFavouriteFromHome()

                }
            }

            R.id.navigation_favorite -> {
                if (navController.currentDestination?.id != R.id.navigation_favorite) {
                    getViewModel().homeNavigator.toFavouriteFromHome()
                }
            }
        }
    }

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//
//        val currentNightMode = resources.configuration.uiMode
//        & Configuration.UI_MODE_NIGHT_MASK
//        switch (currentNightMode) {
//            case Configuration.UI_MODE_NIGHT_NO:
//            // Night mode is not active, we're in day time
//            case Configuration.UI_MODE_NIGHT_YES:
//            // Night mode is active, we're at night!
//            case Configuration.UI_MODE_NIGHT_UNDEFINED:
//            // We don't know what mode we're in, assume notnight
//        }
//    }

    companion object {
        private const val NAV_ID_NONE = -1
        val TAG = HomeActivity::class.java.simpleName

        @JvmStatic
        fun getIntent(activity: Activity) = Intent(activity, HomeActivity::class.java)

        @JvmStatic
        fun startActivity(activity: Activity) {
            activity.startActivity(getIntent(activity))
        }
    }
}