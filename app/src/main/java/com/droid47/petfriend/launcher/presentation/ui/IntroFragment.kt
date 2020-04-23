package com.droid47.petfriend.launcher.presentation.ui

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.activityViewModelProvider
import com.droid47.petfriend.base.extensions.parentFragmentViewModelProvider
import com.droid47.petfriend.base.widgets.BaseBindingBottomSheetDialogFragment
import com.droid47.petfriend.databinding.FragmentIntroBinding
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.HomeBoardViewModel
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.LauncherViewModel
import javax.inject.Inject

class IntroFragment :
    BaseBindingBottomSheetDialogFragment<FragmentIntroBinding, HomeBoardViewModel, LauncherViewModel>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val homeBoardViewModel: HomeBoardViewModel by lazy {
        requireParentFragment().parentFragmentViewModelProvider<HomeBoardViewModel>()
    }

    private val launcherViewModel: LauncherViewModel by lazy {
        requireActivity().activityViewModelProvider<LauncherViewModel>()
    }

    override fun getLayoutId(): Int = R.layout.fragment_intro

    override fun getFragmentNavId(): Int = R.id.navigation_home_board

    override fun getSnackBarAnchorView(): View = getViewDataBinding().cslIntroScreen

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.lifecycleOwner = viewLifecycleOwner
            it.homeBoardViewModel = getViewModel()
        }
    }

    override fun getViewModel(): HomeBoardViewModel = homeBoardViewModel

    override fun getParentViewModel(): LauncherViewModel = launcherViewModel

    override fun injectSubComponent() {
        (activity as LauncherActivity).launcherComponent.inject(this)
    }

}