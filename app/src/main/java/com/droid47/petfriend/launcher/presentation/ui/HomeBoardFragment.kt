package com.droid47.petfriend.launcher.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.activityViewModelProvider
import com.droid47.petfriend.base.extensions.themeInterpolator
import com.droid47.petfriend.base.extensions.viewModelProvider
import com.droid47.petfriend.base.widgets.BaseBindingFragment
import com.droid47.petfriend.databinding.FragmentHomeBoardBinding
import com.droid47.petfriend.launcher.presentation.ui.HomeBoardFragmentDirections.Companion.toTnc
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.HomeBoardViewModel
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.HomeBoardViewModel.Companion.END_POSITION
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.HomeBoardViewModel.Companion.START_POSITION
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.LauncherViewModel
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.fragment_app_intro.view.*
import javax.inject.Inject

private const val ITEM_COUNT = 3

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
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1
            adapter = IntroFragmentAdapter(this@HomeBoardFragment)
            setPageTransformer { page, position ->

                page.tv_title?.apply {
                    translationY = 100 * position
                    alpha = 1 - position
                }

                page.iv_bak_drop?.apply {
                    translationY = 100 * position * 2f
                    alpha = 1 - position
                }

                page.tv_sub_title?.apply {
                    translationY = - 100 * position
                    alpha = 1 - position
                }
            }
        }

        getViewDataBinding().indicator.attachToViewPager(getViewDataBinding().viewPager)
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
        if (findNavController().currentDestination?.id != R.id.navigation_tnc) {
            getViewModel().localPreferencesRepository.saveOnBoardingState()
            val extras = FragmentNavigatorExtras(
                getViewDataBinding().cdlHomeBoard to getViewDataBinding().cdlHomeBoard.transitionName
            )
            findNavController().navigate(toTnc(), extras)
        }
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

    private inner class IntroFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = ITEM_COUNT

        override fun createFragment(position: Int): Fragment = AppIntroFragment.instance(position)
    }
}