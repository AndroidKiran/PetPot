package com.droid47.petgoogle.launcher.presentation.ui

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.droid47.petgoogle.R
import com.droid47.petgoogle.base.extensions.activityViewModelProvider
import com.droid47.petgoogle.base.extensions.parentFragmentViewModelProvider
import com.droid47.petgoogle.base.widgets.BaseBindingFragment
import com.droid47.petgoogle.databinding.FragmentIntroBinding
import com.droid47.petgoogle.launcher.presentation.ui.viewmodels.HomeBoardViewModel
import com.droid47.petgoogle.launcher.presentation.ui.viewmodels.LauncherViewModel
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