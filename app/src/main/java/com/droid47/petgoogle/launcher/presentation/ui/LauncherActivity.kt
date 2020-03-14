package com.droid47.petgoogle.launcher.presentation.ui

import androidx.lifecycle.ViewModelProvider
import com.droid47.petgoogle.R
import com.droid47.petgoogle.base.extensions.viewModelProvider
import com.droid47.petgoogle.base.widgets.BaseBindingActivity
import com.droid47.petgoogle.databinding.ActivityLauncherBinding
import com.droid47.petgoogle.launcher.presentation.ui.viewmodels.LauncherViewModel
import javax.inject.Inject

class LauncherActivity : BaseBindingActivity<ActivityLauncherBinding, LauncherViewModel>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

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

    override fun onStart() {
        super.onStart()
        getViewModel().updateCollectionStatus()
    }
}