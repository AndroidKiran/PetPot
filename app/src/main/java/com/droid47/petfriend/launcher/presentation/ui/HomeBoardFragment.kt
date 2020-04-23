package com.droid47.petfriend.launcher.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.activityViewModelProvider
import com.droid47.petfriend.base.extensions.viewModelProvider
import com.droid47.petfriend.base.widgets.BaseBindingBottomSheetDialogFragment
import com.droid47.petfriend.databinding.FragmentHomeBoardBinding
import com.droid47.petfriend.launcher.presentation.ui.HomeBoardFragmentDirections.Companion.toTnc
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.HomeBoardViewModel
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.HomeBoardViewModel.Companion.END_POSITION
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.HomeBoardViewModel.Companion.START_POSITION
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.LauncherViewModel
import javax.inject.Inject

private const val ITEM_COUNT = 3

class HomeBoardFragment :
    BaseBindingBottomSheetDialogFragment<FragmentHomeBoardBinding, HomeBoardViewModel, LauncherViewModel>(),
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            adapter = IntroFragmentAdapter(this@HomeBoardFragment)
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
            findNavController().navigate(toTnc())
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

    private inner class IntroFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = ITEM_COUNT

        override fun createFragment(position: Int): Fragment = IntroFragment()
    }
}