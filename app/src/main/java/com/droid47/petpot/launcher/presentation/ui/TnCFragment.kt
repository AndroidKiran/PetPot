package com.droid47.petpot.launcher.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.droid47.petpot.R
import com.droid47.petpot.base.extensions.activityViewModelProvider
import com.droid47.petpot.base.extensions.themeInterpolator
import com.droid47.petpot.base.extensions.viewModelProvider
import com.droid47.petpot.base.firebase.AnalyticsScreens
import com.droid47.petpot.base.widgets.BaseBindingFragment
import com.droid47.petpot.base.widgets.components.PetWebView
import com.droid47.petpot.databinding.FragmentTncBinding
import com.droid47.petpot.launcher.presentation.ui.viewmodels.LauncherViewModel
import com.droid47.petpot.launcher.presentation.ui.viewmodels.TnCViewModel
import com.droid47.petpot.workmanagers.notification.NotificationModel.Companion.EXTRA_NAVIGATION_FRAGMENT_ID
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
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

    override fun getClassName(): String = TnCFragment::class.java.simpleName

    override fun getScreenName(): String = AnalyticsScreens.TNC_SCREEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTransition()
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

    override fun onResume() {
        super.onResume()
        trackFragment(getViewModel().firebaseManager)
    }

    override fun onStop() {
        getViewDataBinding().tncFab.hide()
        super.onStop()
    }

    private fun initTransition() {
        enterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.pet_motion_default_large).toLong()
        }

        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
        }
    }

    private fun setUpView() {
        with(getViewDataBinding().tncFab) {
            setOnClickListener {
                updateConsent()
                navigateToHome()
            }
        }

        with(getViewDataBinding().webView) {
            this.scrollListener = this@TnCFragment.scrollListener
        }
    }

    private fun updateConsent() {
        getViewModel().updateTnCStatus()
        getParentViewModel().updateFirebaseCollectionStatus()
    }

    private fun navigateToHome() {
        getViewModel().trackTncToHome()
        val bundle = Bundle().apply {
            putInt(EXTRA_NAVIGATION_FRAGMENT_ID, R.id.navigation_search)
        }
        val extras = FragmentNavigatorExtras(
            getViewDataBinding().cdlTnc to getString(R.string.activity_transition)
        )
        getParentViewModel().launcherNavigator.toHomeFromTnc(bundle, extras)
        requireActivity().finishAfterTransition()
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
        val context = context ?: return
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.cdl_tnc
            duration = resources.getInteger(R.integer.pet_motion_default_large).toLong()
            interpolator = context.themeInterpolator(R.attr.motionInterpolatorPersistent)
            setPathMotion(MaterialArcMotion())
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
        }

//        sharedElementReturnTransition = MaterialContainerTransform().apply {
//            drawingViewId = R.id.cdl_home_board
//            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
//            interpolator = context.themeInterpolator(R.attr.motionInterpolatorPersistent)
//            setPathMotion(MaterialArcMotion())
//            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
//        }

        postponeEnterTransition()

    }

    private val scrollListener = object : PetWebView.WebViewScrollListener {
        override fun onScroll(
            scrollX: Int,
            scrollY: Int,
            oldScrollX: Int,
            oldScrollY: Int
        ) {
            if(!getViewDataBinding().webView.canScrollVertically(scrollY)) {
                getViewModel().run {
                    trackAcceptBtnState("Visible")
                    acceptStateLiveData.postValue(true)
                }
            }
        }

        override fun onScrollDown() {
//            getViewModel().trackConsentScroll("Down")
        }

        override fun onScrollUp() {
            getViewModel().run {
//                getViewModel().trackConsentScroll("up")
                acceptStateLiveData.postValue(false)
            }
        }
    }
}