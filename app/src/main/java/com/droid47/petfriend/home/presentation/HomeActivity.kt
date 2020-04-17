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
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.droid47.petfriend.R
import com.droid47.petfriend.app.PetApplication
import com.droid47.petfriend.base.extensions.viewModelProvider
import com.droid47.petfriend.base.livedata.NetworkConnectionLiveData
import com.droid47.petfriend.base.widgets.*
import com.droid47.petfriend.base.widgets.inAppUpdate.InAppUpdateManager
import com.droid47.petfriend.base.widgets.inAppUpdate.InAppUpdateManager.Companion.IN_APP_UPDATE_REQUEST_CODE
import com.droid47.petfriend.databinding.ActivityHomeBinding
import com.droid47.petfriend.home.data.UpgradeInfoEntity
import com.droid47.petfriend.home.presentation.di.HomeSubComponent
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel.Companion.EVENT_NAVIGATE_BACK
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel.Companion.EVENT_TOGGLE_NAVIGATION
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

    override fun onCreate(savedInstanceState: Bundle?) {
        homeComponent = (application as PetApplication).appComponent.homeComponent().create()
        homeComponent.inject(this@HomeActivity)
        super.onCreate(savedInstanceState)
        setUpViews()
        subscribeToLiveData()
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

    private val appUpgradeObserver = Observer<BaseStateModel<UpgradeInfoEntity>> {
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

    private fun triggerInAppUpdateManager(upgradeEntity: UpgradeInfoEntity) {
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

    private fun getCurrentFragment(): BaseBindingFragment<*, *, *>? {
        return navHostFragment
            ?.childFragmentManager
            ?.primaryNavigationFragment as? BaseBindingFragment<*, *, *>
    }

    companion object {
        private const val NAV_ID_NONE = -1

        @JvmStatic
        fun getIntent(activity: Activity) = Intent(activity, HomeActivity::class.java)

        @JvmStatic
        fun startActivity(activity: Activity) {
            activity.startActivity(getIntent(activity))
        }
    }

}