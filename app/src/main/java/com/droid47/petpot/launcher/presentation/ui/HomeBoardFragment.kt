package com.droid47.petpot.launcher.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.viewpager2.widget.ViewPager2
import com.droid47.petpot.R
import com.droid47.petpot.base.extensions.activityViewModelProvider
import com.droid47.petpot.base.extensions.setParallaxTransformation
import com.droid47.petpot.base.extensions.themeInterpolator
import com.droid47.petpot.base.extensions.viewModelProvider
import com.droid47.petpot.base.firebase.AnalyticsScreens
import com.droid47.petpot.base.widgets.BaseBindingFragment
import com.droid47.petpot.databinding.FragmentHomeBoardBinding
import com.droid47.petpot.launcher.presentation.ui.models.OnBoardingPage
import com.droid47.petpot.launcher.presentation.ui.viewmodels.HomeBoardViewModel
import com.droid47.petpot.launcher.presentation.ui.viewmodels.HomeBoardViewModel.Companion.END_POSITION
import com.droid47.petpot.launcher.presentation.ui.viewmodels.HomeBoardViewModel.Companion.START_POSITION
import com.droid47.petpot.launcher.presentation.ui.viewmodels.LauncherViewModel
import com.droid47.petpot.launcher.presentation.ui.widgets.OnBoardingPagerAdapter
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import javax.inject.Inject

class HomeBoardFragment :
    BaseBindingFragment<FragmentHomeBoardBinding, HomeBoardViewModel, LauncherViewModel>(),
    View.OnClickListener {

    private val numberOfPages by lazy { OnBoardingPage.values().size }

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val homeBoardViewModel: HomeBoardViewModel by lazy(LazyThreadSafetyMode.NONE) {
        viewModelProvider<HomeBoardViewModel>(factory)
    }

    private val launchViewModel: LauncherViewModel by lazy(LazyThreadSafetyMode.NONE) {
        requireActivity().activityViewModelProvider<LauncherViewModel>()
    }

    override fun getLayoutId(): Int = R.layout.fragment_home_board

    override fun getFragmentNavId(): Int = R.id.navigation_home_board

    override fun getSnackBarAnchorView(): View = getViewDataBinding().indicator

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.lifecycleOwner = viewLifecycleOwner
            it.homeBoardViewModel = getViewModel()
        }
    }

    override fun getViewModel(): HomeBoardViewModel = homeBoardViewModel

    override fun getParentViewModel(): LauncherViewModel = launchViewModel

    override fun injectSubComponent() {
        (activity as LauncherActivity).launcherComponent.inject(this)
    }

    override fun getClassName(): String = HomeBoardFragment::class.java.simpleName

    override fun getScreenName(): String = AnalyticsScreens.ON_BOARDING_SCREEN

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
        getViewDataBinding().viewPager.registerOnPageChangeCallback(onPageChangeCallback)
    }

    override fun onResume() {
        super.onResume()
        trackFragment(getViewModel().firebaseManager)
    }

    override fun onStop() {
        getViewDataBinding().viewPager.unregisterOnPageChangeCallback(onPageChangeCallback)
        super.onStop()
    }

    override fun onClick(view: View?) {
        when (view?.id ?: return) {
            R.id.btn_prev -> {
                goToPrevious()
            }

            R.id.btn_next -> {
                goToNext()
            }

            R.id.btn_start -> {
                navigateToTnC()
            }
        }
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
        with(getViewDataBinding().btnPrev) {
            setOnClickListener(this@HomeBoardFragment)
        }

        with(getViewDataBinding().btnNext) {
            setOnClickListener(this@HomeBoardFragment)
        }

        with(getViewDataBinding().btnStart) {
            setOnClickListener(this@HomeBoardFragment)
        }

        with(getViewDataBinding().viewPager) {
            adapter = OnBoardingPagerAdapter()
            getViewDataBinding().indicator.attachToViewPager(this)
            setPageTransformer { page, position ->
                page.setParallaxTransformation(position)
            }
        }
    }

    private fun goToPrevious(): Boolean {
        val currentItem = getViewDataBinding().viewPager.currentItem
        if (currentItem == START_POSITION) return false
        getViewDataBinding().viewPager.currentItem = currentItem.minus(1)
        return true
    }

    private fun goToNext(): Boolean {
        val currentItem = getViewDataBinding().viewPager.currentItem
        if (currentItem >= END_POSITION) return false
        getViewDataBinding().viewPager.currentItem = currentItem.plus(1)
        return true
    }

    private fun navigateToTnC() {
        getViewModel().run {
            saveOnBoardingState()
            trackGetStartedAction()
        }
        val extras = FragmentNavigatorExtras(
            getViewDataBinding().cdlHomeBoard to getViewDataBinding().cdlHomeBoard.transitionName
        )
        getParentViewModel().launcherNavigator.toTncFromIntro(extras)
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            getViewModel().run {
                positionLiveData.postValue(position)
                trackPagerPosition(position)
            }
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            if (numberOfPages > 1) {
                val newProgress = (position + positionOffset) / (numberOfPages - 1)
                getViewDataBinding().cdlHomeBoard.progress = newProgress
            }
        }
    }

    private val closeOnBackPressed = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            activity?.finish()
        }
    }

    private fun prepareTransitions() {
        val context = context ?: return
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.cdl_home_board
            duration = resources.getInteger(R.integer.pet_motion_default_large).toLong()
            interpolator = context.themeInterpolator(R.attr.motionInterpolatorPersistent)
            setPathMotion(MaterialArcMotion())
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
        }

//        sharedElementReturnTransition = MaterialContainerTransform().apply {
//            drawingViewId = R.id.iv_logo
//            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
//            interpolator = context.themeInterpolator(R.attr.motionInterpolatorPersistent)
//            setPathMotion(MaterialArcMotion())
//            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
//        }

        postponeEnterTransition()

    }
}