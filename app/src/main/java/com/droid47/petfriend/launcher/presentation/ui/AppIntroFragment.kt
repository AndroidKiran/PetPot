package com.droid47.petfriend.launcher.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.activityViewModelProvider
import com.droid47.petfriend.base.extensions.parentFragmentViewModelProvider
import com.droid47.petfriend.base.widgets.BaseBindingFragment
import com.droid47.petfriend.databinding.FragmentAppIntroBinding
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.HomeBoardViewModel
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.LauncherViewModel
import javax.inject.Inject

class AppIntroFragment :
    BaseBindingFragment<FragmentAppIntroBinding, HomeBoardViewModel, LauncherViewModel>() {

    private var pageNum: Int = 0
    private val introTitleArray by lazy(LazyThreadSafetyMode.NONE) {
        requireContext().resources.getStringArray(R.array.introTitleArray)
    }
    private val introDescArray by lazy(LazyThreadSafetyMode.NONE) {
        requireContext().resources.getStringArray(R.array.introDescriptionArray)
    }
    private val introImageArray by lazy(LazyThreadSafetyMode.NONE) {
        requireContext().resources.obtainTypedArray(R.array.introImageArray)
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val homeBoardViewModel: HomeBoardViewModel by lazy {
        requireParentFragment().parentFragmentViewModelProvider<HomeBoardViewModel>()
    }

    private val launcherViewModel: LauncherViewModel by lazy {
        requireActivity().activityViewModelProvider<LauncherViewModel>()
    }

    override fun getLayoutId(): Int = R.layout.fragment_app_intro

    override fun getFragmentNavId(): Int = R.id.navigation_home_board

    override fun getSnackBarAnchorView(): View = getViewDataBinding().cslIntroScreen

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.lifecycleOwner = viewLifecycleOwner
            it.homeBoardViewModel = getViewModel()
            it.title = introTitleArray[pageNum]
            it.subTitle = introDescArray[pageNum]
            it.imageRes = introImageArray.getDrawable(pageNum)
        }
    }

    override fun getViewModel(): HomeBoardViewModel = homeBoardViewModel

    override fun getParentViewModel(): LauncherViewModel = launcherViewModel

    override fun injectSubComponent() {
        (activity as LauncherActivity).launcherComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleArguments()
    }

    private fun handleArguments() {
        pageNum = arguments?.getInt(EXTRA_POSITION) ?: 0
    }

    companion object {
        private const val EXTRA_POSITION = "position"
        fun instance(position: Int): AppIntroFragment = AppIntroFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_POSITION, position)
            }
        }
    }
}