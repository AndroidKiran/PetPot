package com.droid47.petfriend.home.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.droid47.petfriend.R
import com.droid47.petfriend.app.PetApplication
import com.droid47.petfriend.base.extensions.viewModelProvider
import com.droid47.petfriend.base.livedata.NetworkConnectionLiveData
import com.droid47.petfriend.base.widgets.*
import com.droid47.petfriend.base.widgets.inAppUpdate.InAppUpdateManager
import com.droid47.petfriend.base.widgets.inAppUpdate.InAppUpdateManager.Companion.IN_APP_UPDATE_REQUEST_CODE
import com.droid47.petfriend.bookmark.presentation.ui.BookmarkFragmentDirections
import com.droid47.petfriend.databinding.ActivityHomeBinding
import com.droid47.petfriend.home.data.AppUpgradeEntity
import com.droid47.petfriend.home.presentation.di.HomeSubComponent
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel.Companion.EVENT_NAVIGATE_BACK
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel.Companion.EVENT_TOGGLE_NAVIGATION
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.workmanagers.notification.NotificationModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.install.model.AppUpdateType
import java.util.*
import javax.inject.Inject

class HomeActivity : BaseBindingActivity<ActivityHomeBinding, HomeViewModel>(),
    NavController.OnDestinationChangedListener, NavigationHost {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var homeComponent: HomeSubComponent

    private var currentNavId: Int = NAV_ID_NONE
    private lateinit var navController: NavController
    private var inAppUpdateManager: InAppUpdateManager? = null
    private var navHostFragment: NavHostFragment? = null
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
        homeComponent = (application as PetApplication).appComponent.homeComponent().create().also {
            it.inject(this@HomeActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViews()
        subscribeToLiveData()
        navigateTo(deepLinkBundle)
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
    }

    private fun setUpViews() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = findNavController(R.id.nav_host_fragment).apply {
            addOnDestinationChangedListener(this@HomeActivity)
        }
        bottomNavDrawer.getViewDataBinding()
            .navigationMenuView.setupWithNavController(navController)
    }

    override fun registerBottomAppbarWithNavigation(bottomAppBar: BottomAppBar) {
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        bottomAppBar.setupWithNavController(navController, appBarConfiguration)
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
        }
    }

    private val eventObserver = Observer<Int> {
        when (it ?: return@Observer) {
            EVENT_TOGGLE_NAVIGATION -> bottomNavDrawer.toggle()
            EVENT_NAVIGATE_BACK -> navController.navigateUp()
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
        inAppUpdateManager = InAppUpdateManager.Builder(
            appCompatActivity = this@HomeActivity,
            resumeUpdates = true,
            updateType = upgradeEntity.updateType
        ).also {
            if (AppUpdateType.FLEXIBLE == upgradeEntity.updateType) {
                it.setSnackbarMsg(getString(R.string.update_downloaded))
                    .setSnackBarAction(getString(R.string.restart).toUpperCase(Locale.US))
            }
        }.build()
        inAppUpdateManager?.checkForUpdate()

    }

    private fun getCurrentFragment(): BaseBindingBottomSheetDialogFragment<*, *, *>? {
        return navHostFragment
            ?.childFragmentManager
            ?.primaryNavigationFragment as? BaseBindingBottomSheetDialogFragment<*, *, *>
    }

    private fun navigateTo(bundle: Bundle) {
        when (bundle.getInt(
            NotificationModel.EXTRA_NAVIGATION_FRAGMENT_ID,
            R.id.navigation_search
        )) {
            R.id.navigation_search -> {
                if (navController.currentDestination?.id != R.id.navigation_search) {
                    navController.navigate(R.id.navigation_search)
                }
            }
            R.id.navigation_pet_details -> {
                val petEntity: PetEntity =
                    bundle.getParcelable(NotificationModel.EXTRA_PET_ENTITY) ?: return

                if (navController.currentDestination?.id != R.id.navigation_favorite) {
                    navController.navigate(R.id.navigation_favorite)
                    navigateToPetDetails(petEntity)
                } else {
                    navigateToPetDetails(petEntity)
                }
            }
        }
    }

    private fun navigateToPetDetails(petEntity: PetEntity) {
        if (navController.currentDestination?.id != R.id.navigation_pet_details) {
            navController.navigate(BookmarkFragmentDirections.toPetDetails(petEntity))
        }
    }

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