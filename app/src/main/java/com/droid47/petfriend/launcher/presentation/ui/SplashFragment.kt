package com.droid47.petfriend.launcher.presentation.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.droid47.petfriend.home.presentation.HomeActivity
import com.droid47.petfriend.launcher.domain.interactors.RefreshAuthTokenAndPetTypeUseCase
import com.droid47.petfriend.launcher.presentation.ui.SplashFragmentDirections.Companion.toIntro
import com.droid47.petfriend.launcher.presentation.ui.SplashFragmentDirections.Companion.toTnc
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.LauncherViewModel
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.SplashViewModel
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.SplashViewModel.Companion.TO_HOME
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.SplashViewModel.Companion.TO_INTRO
import com.droid47.petfriend.launcher.presentation.ui.viewmodels.SplashViewModel.Companion.TO_TNC
import com.droid47.petfriend.search.data.models.type.PetTypeEntity
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
        activityViewModelProvider<LauncherViewModel>(requireActivity())
    }

    override fun getViewModel(): SplashViewModel = splashViewModel

    override fun getParentViewModel(): LauncherViewModel = launcherViewModel

    override fun getLayoutId(): Int = R.layout.fragment_splash

    override fun getFragmentNavId(): Int = R.id.navigation_splash

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also { binding ->
            binding.lifecycleOwner = viewLifecycleOwner
            binding.splashViewModel = getViewModel()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as LauncherActivity).launcherComponent.inject(this)
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
        if (it is Failure && RefreshAuthTokenAndPetTypeUseCase.PLAY_SERVICE_ERROR == it.error.message) {
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
            findNavController().navigate(toIntro())
        }
    }

    private fun navigateToTnc() {
        if (findNavController().currentDestination?.id != R.id.navigation_tnc) {
            findNavController().navigate(toTnc())
        }
    }

    private fun navigateToHome() {
        HomeActivity.startActivity(requireActivity())
        requireActivity().finish()
    }
}