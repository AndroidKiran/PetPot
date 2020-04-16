package com.droid47.petfriend.aboutUs.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.droid47.petfriend.R
import com.droid47.petfriend.aboutUs.presentation.viewmodel.AboutUsViewModel
import com.droid47.petfriend.base.extensions.*
import com.droid47.petfriend.base.widgets.BaseBindingFragment
import com.droid47.petfriend.databinding.FragmentAboutUsBinding
import com.droid47.petfriend.home.presentation.HomeActivity
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel
import javax.inject.Inject

class AboutUsFragment :
    BaseBindingFragment<FragmentAboutUsBinding, AboutUsViewModel, HomeViewModel>(),
    View.OnClickListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val aboutUsViewModel: AboutUsViewModel by lazy {
        viewModelProvider<AboutUsViewModel>(factory)
    }

    private val homeViewModel: HomeViewModel by lazy {
        activityViewModelProvider<HomeViewModel>(requireActivity())
    }

    override fun getLayoutId(): Int = R.layout.fragment_about_us

    override fun getViewModel(): AboutUsViewModel = aboutUsViewModel

    override fun getParentViewModel(): HomeViewModel = homeViewModel

    override fun getFragmentNavId(): Int = R.id.navigation_about_us

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.lifecycleOwner = viewLifecycleOwner
            it.aboutUsViewModel = getViewModel()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as HomeActivity).homeComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    override fun onStart() {
        super.onStart()
        getViewDataBinding().lottieCelebration.playLottie()
    }

    override fun onStop() {
        getViewDataBinding().lottieCelebration.pauseLottie()
        super.onStop()
    }

    override fun onClick(view: View?) {
        when (view?.id ?: return) {
            R.id.fab -> requireContext().sendEmail(
                arrayOf(
                    getString(R.string.my_email)
                )
            )
        }
    }

    private fun setUpView() {
        getViewDataBinding().fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setImageResource(R.drawable.vc_mail)
            setOnClickListener(this@AboutUsFragment)
        }

        getViewDataBinding().bottomAppBar.apply {
            setNavigationIcon(R.drawable.vc_nav_menu)
            setOnMenuItemClickListener(menuClickListener)
            replaceMenu(R.menu.about_us_menu)
            setNavigationOnClickListener {
                getParentViewModel().eventLiveData.postValue(HomeViewModel.EVENT_TOGGLE_NAVIGATION)
            }
        }
    }

    private val menuClickListener = Toolbar.OnMenuItemClickListener {
        when (it?.itemId ?: return@OnMenuItemClickListener false) {
            R.id.menu_rate_app -> requireContext().rateMyApp()
            else -> throw IllegalStateException("No match menu id")
        }
        return@OnMenuItemClickListener false
    }

}