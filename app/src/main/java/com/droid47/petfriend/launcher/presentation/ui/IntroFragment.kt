package com.droid47.petfriend.launcher.presentation.ui

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.activityViewModelProvider
import com.droid47.petfriend.base.extensions.parentFragmentViewModelProvider
import com.droid47.petfriend.base.widgets.BaseBindingFragment
import com.droid47.petfriend.databinding.FragmentIntroBinding
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.HomeBoardViewModel
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.LauncherViewModel
import javax.inject.Inject

class IntroFragment :
    BaseBindingFragment<FragmentIntroBinding, HomeBoardViewModel, LauncherViewModel>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val homeBoardViewModel: HomeBoardViewModel by lazy {
        parentFragmentViewModelProvider<HomeBoardViewModel>(requireParentFragment())
    }

    private val launcherViewModel: LauncherViewModel by lazy {
        activityViewModelProvider<LauncherViewModel>(requireActivity())
    }

    override fun getLayoutId(): Int = R.layout.fragment_intro

    override fun getFragmentNavId(): Int = R.id.navigation_home_board

    override fun getSnackBarAnchorView(): View = getViewDataBinding().cslIntroScreen

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.lifecycleOwner = viewLifecycleOwner
            it.homeBoardViewModel = getViewModel()
            it.executePendingBindings()
        }
    }

    override fun getViewModel(): HomeBoardViewModel = homeBoardViewModel

    override fun getParentViewModel(): LauncherViewModel = launcherViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as LauncherActivity).launcherComponent.inject(this)
    }

}