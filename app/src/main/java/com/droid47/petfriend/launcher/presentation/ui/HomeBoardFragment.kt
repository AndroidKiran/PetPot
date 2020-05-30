package com.droid47.petfriend.launcher.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.viewpager2.widget.ViewPager2
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.activityViewModelProvider
import com.droid47.petfriend.base.extensions.setParallaxTransformation
import com.droid47.petfriend.base.extensions.themeInterpolator
import com.droid47.petfriend.base.extensions.viewModelProvider
import com.droid47.petfriend.base.widgets.BaseBindingFragment
import com.droid47.petfriend.databinding.FragmentHomeBoardBinding
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.HomeBoardViewModel
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.HomeBoardViewModel.Companion.END_POSITION
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.HomeBoardViewModel.Companion.START_POSITION
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.LauncherViewModel
import com.droid47.petfriend.launcher.presentation.ui.widgets.OnBoardingPagerAdapter
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import javax.inject.Inject

class HomeBoardFragment :
    BaseBindingFragment<FragmentHomeBoardBinding, HomeBoardViewModel, LauncherViewModel>(),
    View.OnClickListener {

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

    override fun getSnackBarAnchorView(): View = getViewDataBinding().bottomIntro

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
        getViewDataBinding().viewPager.registerOnPageChangeCallback(onPageChangeCallback)
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
                if (!goToNext()) {
                    navigateToTnC()
                }
            }
        }
    }

    private fun setUpView() {
        with(getViewDataBinding().btnPrev) {
            setOnClickListener(this@HomeBoardFragment)
        }

        with(getViewDataBinding().btnNext) {
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
        getViewModel().localPreferencesRepository.saveOnBoardingState()
        val extras = FragmentNavigatorExtras(
            getViewDataBinding().cdlHomeBoard to getViewDataBinding().cdlHomeBoard.transitionName
        )
        getParentViewModel().launcherNavigator.toTncFromIntro(extras)
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            getViewModel().positionLiveData.postValue(position)
        }
    }

    private val closeOnBackPressed = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            activity?.finish()
        }
    }

    private fun prepareTransitions() {
        val context = context ?: return
        postponeEnterTransition()
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.cdl_home_board
            duration = resources.getInteger(R.integer.pet_motion_default_large).toLong()
            interpolator = context.themeInterpolator(R.attr.motionInterpolatorPersistent)
            pathMotion = MaterialArcMotion()
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
        }

        sharedElementReturnTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.iv_logo
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
            interpolator = context.themeInterpolator(R.attr.motionInterpolatorPersistent)
            pathMotion = MaterialArcMotion()
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
        }
    }
}