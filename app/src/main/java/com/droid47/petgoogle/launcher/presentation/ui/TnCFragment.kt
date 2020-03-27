package com.droid47.petgoogle.launcher.presentation.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.droid47.petgoogle.R
import com.droid47.petgoogle.base.extensions.activityViewModelProvider
import com.droid47.petgoogle.base.extensions.viewModelProvider
import com.droid47.petgoogle.base.widgets.BaseBindingFragment
import com.droid47.petgoogle.databinding.FragmentTncBinding
import com.droid47.petgoogle.home.presentation.HomeActivity
import com.droid47.petgoogle.launcher.presentation.ui.viewmodels.LauncherViewModel
import com.droid47.petgoogle.launcher.presentation.ui.viewmodels.TnCViewModel
import javax.inject.Inject

class TnCFragment : BaseBindingFragment<FragmentTncBinding, TnCViewModel, LauncherViewModel>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val tnCViewModel: TnCViewModel by lazy(LazyThreadSafetyMode.NONE) {
        viewModelProvider<TnCViewModel>(factory)
    }

    private val launchViewModel: LauncherViewModel by lazy(LazyThreadSafetyMode.NONE) {
        activityViewModelProvider<LauncherViewModel>(requireActivity())
    }

    override fun getLayoutId(): Int = R.layout.fragment_tnc

    override fun getFragmentNavId(): Int = R.id.navigation_tnc

    override fun getViewModel(): TnCViewModel = tnCViewModel

    override fun getParentViewModel(): LauncherViewModel = launchViewModel

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.lifecycleOwner = viewLifecycleOwner
            it.tncViewModel = getViewModel()
            it.executePendingBindings()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as LauncherActivity).launcherComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, closeOnBackPressed)
        closeOnBackPressed.isEnabled = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    override fun onStart() {
        super.onStart()
        getViewDataBinding().tncFab.show()
    }

    override fun onStop() {
        getViewDataBinding().tncFab.hide()
        super.onStop()
    }

    private fun setUpView() {
        with(getViewDataBinding().tncFab) {
            setOnClickListener {
                navigateToHome()
            }
        }
    }

    private fun navigateToHome() {
        getViewModel().updateTnCStatus()
        getParentViewModel().updateCollectionStatus()
        HomeActivity.startActivity(requireActivity())
        requireActivity().finish()
    }

    private val closeOnBackPressed = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (getViewDataBinding().webView.canGoBack()) {
                getViewDataBinding().webView.goBack()
            } else {
                activity?.finish()
            }
        }
    }
}