package com.droid47.petfriend.launcher.presentation.ui

import android.os.Bundle
import android.view.Window
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.droid47.petfriend.R
import com.droid47.petfriend.app.PetApplication
import com.droid47.petfriend.base.extensions.viewModelProvider
import com.droid47.petfriend.base.livedata.NetworkConnectionLiveData
import com.droid47.petfriend.base.widgets.BaseBindingActivity
import com.droid47.petfriend.base.widgets.BaseBindingFragment
import com.droid47.petfriend.databinding.ActivityLauncherBinding
import com.droid47.petfriend.launcher.presentation.di.LauncherSubComponent
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.LauncherViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransformSharedElementCallback
import javax.inject.Inject

class LauncherActivity : BaseBindingActivity<ActivityLauncherBinding, LauncherViewModel>() {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var launcherComponent: LauncherSubComponent
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    private val launcherViewModel: LauncherViewModel by lazy(LazyThreadSafetyMode.NONE) {
        viewModelProvider<LauncherViewModel>(viewModelFactory)
    }

    override fun getViewModel(): LauncherViewModel = launcherViewModel

    override fun getLayoutId(): Int = R.layout.activity_launcher

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also { binding ->
            binding.lifecycleOwner = this@LauncherActivity
            binding.launcherViewModel = getViewModel()
        }
    }

    override fun injectComponent() {
        launcherComponent =
            (application as PetApplication).appComponent.launcherComponent().create().also {
                it.inject(this@LauncherActivity)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initTransitions()
        super.onCreate(savedInstanceState)
        setUpViews()
        subscribeToLiveData()
    }

    override fun onStart() {
        super.onStart()
        getViewModel().updateFirebaseCollectionStatus()
    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }

    private fun initTransitions() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
    }

    private fun setUpViews() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = findNavController(R.id.nav_host_fragment)
        getViewModel().launcherNavigator.inject(navController)
    }

    private fun getCurrentFragment(): BaseBindingFragment<*, *, *>? {
        return navHostFragment.childFragmentManager.primaryNavigationFragment as? BaseBindingFragment<*, *, *>
    }

    private fun subscribeToLiveData() {
        getViewModel().homeNavigationLiveData.run {
            removeObserver(homeNavigationObserver)
            observe(this@LauncherActivity, homeNavigationObserver)
        }
    }

    private val networkConnectionObserver = Observer<NetworkConnectionLiveData.NetworkState> {
        val netWorkState = it ?: return@Observer
        val anchorView =
            getCurrentFragment()?.getSnackBarAnchorView() ?: getViewDataBinding().cdlLauncher
        when (netWorkState) {
            is NetworkConnectionLiveData.NetworkState.Connected ->
                Snackbar.make(
                    getViewDataBinding().cdlLauncher,
                    getString(R.string.active_connection),
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(anchorView)
                    .show()

            is NetworkConnectionLiveData.NetworkState.ConnectionLost,
            is NetworkConnectionLiveData.NetworkState.DisConnected ->
                Snackbar.make(
                    getViewDataBinding().cdlLauncher,
                    getString(R.string.non_active_connection),
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(anchorView)
                    .show()
        }
    }

    private val homeNavigationObserver = Observer<Bundle> {
        val bundle = it ?: return@Observer
        val extras = FragmentNavigatorExtras(
            getViewDataBinding().cdlLauncher to getViewDataBinding().cdlLauncher.transitionName
        )
        getViewModel().launcherNavigator.toHomeFromSplash(bundle, extras)
        finishAfterTransition()
    }
}