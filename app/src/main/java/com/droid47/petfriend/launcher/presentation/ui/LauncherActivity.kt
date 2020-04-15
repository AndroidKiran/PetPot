package com.droid47.petfriend.launcher.presentation.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.droid47.petfriend.R
import com.droid47.petfriend.app.PetApplication
import com.droid47.petfriend.base.extensions.viewModelProvider
import com.droid47.petfriend.base.widgets.BaseBindingActivity
import com.droid47.petfriend.databinding.ActivityLauncherBinding
import com.droid47.petfriend.launcher.presentation.di.LauncherSubComponent
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.LauncherViewModel
import javax.inject.Inject

class LauncherActivity : BaseBindingActivity<ActivityLauncherBinding, LauncherViewModel>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var launcherComponent: LauncherSubComponent

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
    }

    override fun onStart() {
        super.onStart()
        getViewModel().updateCollectionStatus()
    }
}