package com.droid47.petpot.aboutUs.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.droid47.petpot.R
import com.droid47.petpot.aboutUs.presentation.viewmodel.AboutUsViewModel
import com.droid47.petpot.base.extensions.*
import com.droid47.petpot.base.firebase.AnalyticsScreens
import com.droid47.petpot.base.widgets.BaseBindingFragment
import com.droid47.petpot.databinding.FragmentAboutUsBinding
import com.droid47.petpot.home.presentation.ui.HomeActivity
import com.droid47.petpot.home.presentation.viewmodels.HomeViewModel
import com.google.android.material.transition.MaterialElevationScale
import javax.inject.Inject

private const val PRIVACY_URL = "https://sites.google.com/view/petpot-privacy-policies/"

class AboutUsFragment :
    BaseBindingFragment<FragmentAboutUsBinding, AboutUsViewModel, HomeViewModel>(),
    View.OnClickListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val aboutUsViewModel: AboutUsViewModel by lazy {
        viewModelProvider<AboutUsViewModel>(factory)
    }

    private val homeViewModel: HomeViewModel by lazy {
        requireActivity().activityViewModelProvider<HomeViewModel>()
    }

    override fun getLayoutId(): Int = R.layout.fragment_about_us

    override fun getViewModel(): AboutUsViewModel = aboutUsViewModel

    override fun getParentViewModel(): HomeViewModel = homeViewModel

    override fun getFragmentNavId(): Int = R.id.navigation_about_us

    override fun getSnackBarAnchorView() = getViewDataBinding().fab

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.lifecycleOwner = viewLifecycleOwner
            it.aboutUsViewModel = getViewModel()
        }
    }

    override fun injectSubComponent() {
        (activity as HomeActivity).homeComponent.inject(this)
    }

    override fun getClassName(): String = AboutUsFragment::class.java.simpleName

    override fun getScreenName(): String = AnalyticsScreens.ABOUT_SCREEN

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        initTransition()
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    override fun onStart() {
        super.onStart()
        getViewDataBinding().lottieCelebration.playLottie()
    }

    override fun onResume() {
        super.onResume()
        trackFragment(getViewModel().fireBaseManager)
    }

    override fun onStop() {
        getViewDataBinding().lottieCelebration.pauseLottie()
        super.onStop()
    }

    override fun onClick(view: View?) {
        val context = context ?: return
        when (view?.id ?: return) {
            R.id.fab -> {
                getViewModel().trackRateOnPlayStore()
                context.rateMyApp()
            }
        }
    }

    private fun initTransition() {
        enterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
        }

        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.pet_motion_duration_small).toLong()
        }

        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
        }
    }

    private fun setUpView() {
        getViewDataBinding().fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setImageResource(R.drawable.vc_star)
            setOnClickListener(this@AboutUsFragment)
        }

        getViewDataBinding().bottomAppBar.apply {
            setOnMenuItemClickListener(menuClickListener)
            replaceMenu(R.menu.about_us_menu)
            setNavigationOnClickListener {
                getParentViewModel().eventLiveData.postValue(HomeViewModel.EVENT_TOGGLE_NAVIGATION)
            }
        }

        getViewDataBinding().btnNavSearch.setOnClickListener {
            getParentViewModel().eventLiveData.postValue(HomeViewModel.EVENT_TOGGLE_NAVIGATION)
        }
    }

    private val menuClickListener = Toolbar.OnMenuItemClickListener {
        val context = context ?: return@OnMenuItemClickListener false
        when (it?.itemId ?: return@OnMenuItemClickListener false) {
            R.id.menu_email -> {
                getViewModel().trackWriteToMail()
                context.sendEmail(
                    arrayOf(
                        getString(R.string.my_email)
                    )
                )
            }

            R.id.privacy_policy -> requireContext().openUrlInBrowser(PRIVACY_URL)

            else -> throw IllegalStateException("No match menu id")
        }
        return@OnMenuItemClickListener false
    }

}