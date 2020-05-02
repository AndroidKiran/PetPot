package com.droid47.petfriend.launcher.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProvider
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.activityViewModelProvider
import com.droid47.petfriend.base.extensions.themeInterpolator
import com.droid47.petfriend.base.extensions.viewModelProvider
import com.droid47.petfriend.base.widgets.BaseBindingFragment
import com.droid47.petfriend.databinding.FragmentTncBinding
import com.droid47.petfriend.launcher.presentation.ui.TnCFragmentDirections.Companion.toHome
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.LauncherViewModel
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.TnCViewModel
import com.droid47.petfriend.workmanagers.notification.NotificationModel.Companion.EXTRA_NAVIGATION_FRAGMENT_ID
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import javax.inject.Inject

class TnCFragment : BaseBindingFragment<FragmentTncBinding, TnCViewModel, LauncherViewModel>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val tnCViewModel: TnCViewModel by lazy(LazyThreadSafetyMode.NONE) {
        viewModelProvider<TnCViewModel>(factory)
    }

    private val launchViewModel: LauncherViewModel by lazy(LazyThreadSafetyMode.NONE) {
        requireActivity().activityViewModelProvider<LauncherViewModel>()
    }

    override fun getLayoutId(): Int = R.layout.fragment_tnc

    override fun getFragmentNavId(): Int = R.id.navigation_tnc

    override fun getViewModel(): TnCViewModel = tnCViewModel

    override fun getParentViewModel(): LauncherViewModel = launchViewModel

    override fun getSnackBarAnchorView(): View = getViewDataBinding().tncFab

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.lifecycleOwner = viewLifecycleOwner
            it.tncViewModel = getViewModel()
        }
    }

    override fun injectSubComponent() {
        (activity as LauncherActivity).launcherComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, closeOnBackPressed)
        closeOnBackPressed.isEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        prepareTransitions()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
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
        getParentViewModel().updateFirebaseCollectionStatus()
        val direction = toHome(Bundle().apply {
            putInt(EXTRA_NAVIGATION_FRAGMENT_ID, R.id.navigation_search)
        })
        getParentViewModel().homeNavigationLiveData.postValue(direction)
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

    private fun prepareTransitions() {
        postponeEnterTransition()

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.pet_motion_default_large).toLong()
            interpolator = requireContext().themeInterpolator(R.attr.motionInterpolatorPersistent)
            pathMotion = MaterialArcMotion()
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
        }

        sharedElementReturnTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.iv_logo
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
            interpolator = requireContext().themeInterpolator(R.attr.motionInterpolatorPersistent)
            pathMotion = MaterialArcMotion()
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
        }
    }
}