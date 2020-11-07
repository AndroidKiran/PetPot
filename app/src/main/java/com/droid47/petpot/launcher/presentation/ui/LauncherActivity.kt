package com.droid47.petpot.launcher.presentation.ui

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.droid47.petpot.R
import com.droid47.petpot.app.PetApplication
import com.droid47.petpot.base.extensions.themeInterpolator
import com.droid47.petpot.base.extensions.viewModelProvider
import com.droid47.petpot.base.firebase.AnalyticsScreens
import com.droid47.petpot.base.livedata.NetworkConnectionLiveData
import com.droid47.petpot.base.widgets.BaseBindingActivity
import com.droid47.petpot.base.widgets.BaseBindingFragment
import com.droid47.petpot.databinding.ActivityLauncherBinding
import com.droid47.petpot.launcher.presentation.di.LauncherSubComponent
import com.droid47.petpot.launcher.presentation.ui.viewmodels.LauncherViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import javax.inject.Inject

class LauncherActivity : BaseBindingActivity<ActivityLauncherBinding, LauncherViewModel>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var launcherComponent: LauncherSubComponent
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

//    private val arg:LauncherActivityArgs by navArgs()
//    private val deepLinkBundle: Bundle? by lazy(LazyThreadSafetyMode.NONE) { arg?.deepLinkBundle }

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

    override fun getClassName(): String = LauncherActivity::class.java.simpleName

    override fun getScreenName(): String = AnalyticsScreens.LAUNCHER_SCREEN

    override fun onCreate(savedInstanceState: Bundle?) {
        initTransitions()
        super.onCreate(savedInstanceState)
        setUpViews()
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
        findViewById<View>(android.R.id.content).transitionName =
            getString(R.string.activity_transition)
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
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

    private fun setUpViews() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = findNavController(R.id.nav_host_fragment)
        getViewModel().launcherNavigator.inject(navController)
    }

    private fun getCurrentFragment(): BaseBindingFragment<*, *, *>? {
        return navHostFragment.childFragmentManager.primaryNavigationFragment as? BaseBindingFragment<*, *, *>
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
}