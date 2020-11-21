package com.droid47.petpot.launcher.presentation.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.droid47.petpot.R
import com.droid47.petpot.base.extensions.activityViewModelProvider
import com.droid47.petpot.base.extensions.viewModelProvider
import com.droid47.petpot.base.firebase.AnalyticsScreens
import com.droid47.petpot.base.widgets.BaseBindingFragment
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.databinding.FragmentSplashBinding
import com.droid47.petpot.launcher.domain.interactors.SyncPetTypeUseCase
import com.droid47.petpot.launcher.presentation.ui.viewmodels.LauncherViewModel
import com.droid47.petpot.launcher.presentation.ui.viewmodels.SplashViewModel
import com.droid47.petpot.launcher.presentation.ui.viewmodels.SplashViewModel.Companion.TO_HOME
import com.droid47.petpot.launcher.presentation.ui.viewmodels.SplashViewModel.Companion.TO_INTRO
import com.droid47.petpot.launcher.presentation.ui.viewmodels.SplashViewModel.Companion.TO_TNC
import com.droid47.petpot.search.data.models.type.PetTypeEntity
import com.droid47.petpot.workmanagers.notification.NotificationModel
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.transition.MaterialElevationScale
import javax.inject.Inject


private const val PLAY_SERVICES_RESOLUTION_REQUEST = 2404
private const val PLAY_SERVICES_RESOLUTION_RESULT = 2403

class SplashFragment :
    BaseBindingFragment<FragmentSplashBinding, SplashViewModel, LauncherViewModel>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var errorDialog: Dialog? = null

    private val splashViewModel: SplashViewModel by lazy(LazyThreadSafetyMode.NONE) {
        viewModelProvider<SplashViewModel>(viewModelFactory)
    }

    private val launcherViewModel: LauncherViewModel by lazy(LazyThreadSafetyMode.NONE) {
        requireActivity().activityViewModelProvider<LauncherViewModel>()
    }

    override fun getViewModel(): SplashViewModel = splashViewModel

    override fun getParentViewModel(): LauncherViewModel = launcherViewModel

    override fun getLayoutId(): Int = R.layout.fragment_splash

    override fun getFragmentNavId(): Int = R.id.navigation_splash

    override fun getSnackBarAnchorView(): View = getViewDataBinding().btnRetry

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also { binding ->
            binding.lifecycleOwner = viewLifecycleOwner
            binding.splashViewModel = getViewModel()
        }
    }

    override fun injectSubComponent() {
        (activity as LauncherActivity).launcherComponent.inject(this)
    }

    override fun getClassName(): String = SplashFragment::class.java.simpleName

    override fun getScreenName(): String = AnalyticsScreens.SPLASH_SCREEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    override fun onResume() {
        super.onResume()
        trackFragment(getViewModel().firebaseManager)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscribeToLiveData()
    }

    private fun initTransition() {
        enterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
        }

        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.pet_motion_duration_small).toLong()
        }
    }

    private fun subscribeToLiveData() {
        getViewModel().navigationEvent.run {
            removeObserver(navigationObserver)
            observe(viewLifecycleOwner, navigationObserver)
        }

        getViewModel().resultEvent.run {
            removeObserver(resultObserver)
            observe(viewLifecycleOwner, resultObserver)
        }
    }

    private val navigationObserver = Observer<String> {
        when (it ?: return@Observer) {
            TO_INTRO -> navigateToIntro()
            TO_TNC -> navigateToTnc()
            TO_HOME -> navigateToHome()
        }
    }

    private val resultObserver = Observer<BaseStateModel<List<PetTypeEntity>>> {
        if (it is Failure && SyncPetTypeUseCase.PLAY_SERVICE_ERROR == it.error.message) {
            showPlayServiceErrorDialog()
        }
    }

    private fun showPlayServiceErrorDialog() {
        if (errorDialog == null) {
            errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(
                requireActivity(),
                PLAY_SERVICES_RESOLUTION_RESULT,
                PLAY_SERVICES_RESOLUTION_REQUEST
            ).also {
                it?.setCancelable(false)
            }
        }

        if (errorDialog?.isShowing == false) {
            errorDialog?.show()
        }
    }

    private fun navigateToIntro() {
        getViewModel().trackSplashToOnBoarding()
        val extras = FragmentNavigatorExtras(
            getViewDataBinding().ivLogo to getViewDataBinding().ivLogo.transitionName
        )
        getParentViewModel().launcherNavigator.toIntroFromSplash(extras)
    }

    private fun navigateToTnc() {
        getViewModel().trackSplashToTnc()
        val extras = FragmentNavigatorExtras(
            getViewDataBinding().ivLogo to getViewDataBinding().ivLogo.transitionName
        )
        getParentViewModel().launcherNavigator.toTncFromSplash(extras)
    }

    private fun toHome(bundle: Bundle) {
        getViewModel().trackSplashToHome()
        val extras = FragmentNavigatorExtras(
            getViewDataBinding().llMain to getString(R.string.activity_transition)
        )
        getParentViewModel().launcherNavigator.toHomeFromSplash(bundle, extras)
        requireActivity().finishAfterTransition()
    }

    private fun navigateToHome() {
        val tncStatus = getViewModel().getTncStatus()
        if (tncStatus) {
            val bundle = arguments ?: Bundle().apply {
                putInt(NotificationModel.EXTRA_NAVIGATION_FRAGMENT_ID, R.id.navigation_search)
            }
            toHome(bundle)
        } else {
            navigateToTnc()
        }
    }
}