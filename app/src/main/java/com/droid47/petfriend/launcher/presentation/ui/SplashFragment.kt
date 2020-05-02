package com.droid47.petfriend.launcher.presentation.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.activityViewModelProvider
import com.droid47.petfriend.base.extensions.pauseLottie
import com.droid47.petfriend.base.extensions.playLottie
import com.droid47.petfriend.base.extensions.viewModelProvider
import com.droid47.petfriend.base.widgets.BaseBindingFragment
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.databinding.FragmentSplashBinding
import com.droid47.petfriend.launcher.domain.interactors.SyncPetTypeUseCase
import com.droid47.petfriend.launcher.presentation.ui.SplashFragmentDirections.Companion.toHome
import com.droid47.petfriend.launcher.presentation.ui.SplashFragmentDirections.Companion.toIntro
import com.droid47.petfriend.launcher.presentation.ui.SplashFragmentDirections.Companion.toTnc
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.LauncherViewModel
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.SplashViewModel
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.SplashViewModel.Companion.TO_HOME
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.SplashViewModel.Companion.TO_INTRO
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.SplashViewModel.Companion.TO_TNC
import com.droid47.petfriend.search.data.models.type.PetTypeEntity
import com.droid47.petfriend.workmanagers.notification.NotificationModel
import com.google.android.gms.common.GoogleApiAvailability
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        postponeEnterTransition()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    override fun onStart() {
        super.onStart()
        getViewDataBinding().ivLogo.playLottie()
    }

    override fun onStop() {
        getViewDataBinding().ivLogo.pauseLottie()
        super.onStop()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscribeToLiveData()
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
        if (findNavController().currentDestination?.id != R.id.navigation_home_board) {
            val extras = FragmentNavigatorExtras(
                getViewDataBinding().ivLogo to getViewDataBinding().ivLogo.transitionName
            )
            findNavController().navigate(toIntro(), extras)
        }
    }

    private fun navigateToTnc() {
        if (findNavController().currentDestination?.id != R.id.navigation_tnc) {
            val extras = FragmentNavigatorExtras(
                getViewDataBinding().ivLogo to getViewDataBinding().ivLogo.transitionName
            )
            findNavController().navigate(toTnc(), extras)
        }
    }

    private fun navigateToHome() {
        val tncStatus = getViewModel().getTncStatus()
        if (tncStatus) {
            val direction = toHome(arguments ?: Bundle().apply {
                putInt(NotificationModel.EXTRA_NAVIGATION_FRAGMENT_ID, R.id.navigation_search)
            })
            getParentViewModel().homeNavigationLiveData.postValue(direction)
        } else {
            navigateToTnc()
        }
    }
}