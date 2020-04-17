package com.droid47.petfriend.launcher.presentation.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
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
import javax.inject.Inject

class LauncherActivity : BaseBindingActivity<ActivityLauncherBinding, LauncherViewModel>() {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var launcherComponent: LauncherSubComponent
    private var navHostFragment: NavHostFragment? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        launcherComponent =
            (application as PetApplication).appComponent.launcherComponent().create()
        launcherComponent.inject(this)
        super.onCreate(savedInstanceState)
        setUpViews()
    }

    override fun onStart() {
        super.onStart()
        getViewModel().updateCollectionStatus()
    }

    private fun setUpViews() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    private fun getCurrentFragment(): BaseBindingFragment<*, *, *>? {
        return navHostFragment
            ?.childFragmentManager
            ?.primaryNavigationFragment as? BaseBindingFragment<*, *, *>
    }

    private fun subscribeToLiveData() {
        getViewModel().networkConnectionLiveData.run {
            removeObserver(networkConnectionObserver)
            observe(this@LauncherActivity, networkConnectionObserver)
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
}