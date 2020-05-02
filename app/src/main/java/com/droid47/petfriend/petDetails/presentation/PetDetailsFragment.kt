package com.droid47.petfriend.petDetails.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.droid47.petfriend.R
import com.droid47.petfriend.base.bindingConfig.ContentLoadingConfiguration
import com.droid47.petfriend.base.bindingConfig.ErrorViewConfiguration
import com.droid47.petfriend.base.extensions.*
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.widgets.*
import com.droid47.petfriend.base.widgets.components.AppBarStateChangeListener
import com.droid47.petfriend.base.widgets.components.ZoomInPageTransformer
import com.droid47.petfriend.databinding.FragmentPetDetailsBinding
import com.droid47.petfriend.home.presentation.HomeActivity
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel
import com.droid47.petfriend.petDetails.presentation.adapter.PetPhotoViewerAdapter
import com.droid47.petfriend.petDetails.presentation.viewmodels.PetDetailsViewModel
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.data.models.search.PhotosItemEntity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import javax.inject.Inject

class PetDetailsFragment :
    BaseBindingFragment<FragmentPetDetailsBinding, PetDetailsViewModel, HomeViewModel>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val args: PetDetailsFragmentArgs by navArgs()
    private val petId: Int by lazy(LazyThreadSafetyMode.NONE) { args.petId }

    private val loadingState = ContentLoadingConfiguration()
    private val errorState = ErrorViewConfiguration()
    private var backDropHeight: Int = 500
    private var screenHeight: Int = 300

    private val similarPetsFragment: SimilarPetsFragment by lazy(LazyThreadSafetyMode.NONE) {
        childFragmentManager.findFragmentById(R.id.similar_pets) as SimilarPetsFragment
    }

    private val animalDetailsViewModel: PetDetailsViewModel by lazy(LazyThreadSafetyMode.NONE) {
        viewModelProvider<PetDetailsViewModel>(factory)
    }

    private val homeViewModel: HomeViewModel by lazy(LazyThreadSafetyMode.NONE) {
        requireActivity().activityViewModelProvider<HomeViewModel>()
    }

    override fun getLayoutId(): Int = R.layout.fragment_pet_details

    override fun getViewModel(): PetDetailsViewModel = animalDetailsViewModel

    override fun getParentViewModel(): HomeViewModel = homeViewModel

    override fun getSnackBarAnchorView(): View = getViewDataBinding().fab

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.lifecycleOwner = viewLifecycleOwner
            it.petDetailsViewModel = getViewModel()
            it.loadingStateConfig = loadingState
            it.errorStateConfig = errorState
        }
    }

    override fun getFragmentNavId(): Int = R.id.navigation_pet_details

    override fun injectSubComponent() {
        (activity as HomeActivity).homeComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenHeight = requireContext().getScreenHeight()
        backDropHeight = screenHeight - resources.getDimensionPixelOffset(R.dimen.grid_14)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        prepareTransitions()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        similarPetsFragment.subscribeToSubListSelection(navigationObserver)
        setUpView()
        setPetPhotoAdapter()
        handleNavArgs(petId, petId)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscribeToLiveData()
    }

    private fun handleNavArgs(petId: Int, transitionId: Int) {
        with(getViewModel()) {
            this.petId.postValue(petId)
            this.transitionId.postValue(transitionId)
        }
    }

    private fun setUpView() {
        getViewDataBinding().appbar.apply {
            updateHeight(backDropHeight)
            addOnOffsetChangedListener(appBarOffsetChangedListener)
        }

        getViewDataBinding().fab.apply {
            setImageResource(R.drawable.vc_favorite)
            setOnClickListener {
                hide()
                getViewModel().run {
                    openingAnimationRequired = false
                    onBookMarkClick(
                        getViewModel().petLiveData.value?.data ?: return@setOnClickListener
                    )
                }

            }
        }

        getViewDataBinding().bottomAppBar.apply {
            setNavigationIcon(R.drawable.vc_close)
            setOnMenuItemClickListener(menuClickListener)
            replaceMenu(R.menu.pet_details_menu)
            setNavigationOnClickListener {
                getParentViewModel().eventLiveData.postValue(HomeViewModel.EVENT_NAVIGATE_BACK)
            }
        }

        getViewDataBinding().includePetDetails.btnShowOnMap.setOnClickListener {
            requireActivity().showLocationInMap(
                getViewModel().getAddress() ?: return@setOnClickListener
            )
        }
    }

    private fun setPetPhotoAdapter() {
        if (getPetPhotoAdapter() != null) return
        with(getViewDataBinding().viewPager) {
            adapter = PetPhotoViewerAdapter(petPhotoViewerListener)
            getViewDataBinding().indicator.attachToViewPager(this)
            setPageTransformer(ZoomInPageTransformer())
        }
    }

    private fun getPetPhotoAdapter() =
        getViewDataBinding().viewPager.adapter as? PetPhotoViewerAdapter


    private fun subscribeToLiveData() {
        getViewModel().petLiveData.run {
            removeObserver(petDetailsObserver)
            observe(viewLifecycleOwner, petDetailsObserver)
        }

        getViewModel().phoneNumLiveData.run {
            removeObserver(phoneObserver)
            observe(viewLifecycleOwner, phoneObserver)
        }

        getViewModel().emailLiveData.run {
            removeObserver(emailObserver)
            observe(viewLifecycleOwner, emailObserver)
        }

        getViewModel().urlLiveData.run {
            removeObserver(shareUrlObserver)
            observe(viewLifecycleOwner, shareUrlObserver)
        }
    }

    private val petDetailsObserver = Observer<BaseStateModel<PetEntity>> {
        val baseStateModel = it ?: return@Observer
        when (baseStateModel) {
            is Failure -> {
                updateErrorState(baseStateModel.error)
                hideFab()
                getViewDataBinding().appbar.setExpanded(true, false)
            }

            is Loading -> {
                hideFab()
            }

            is Success -> {
                showFab()
                val photoList = if (baseStateModel.data.photos.isNullOrEmpty()) {
                    listOf(PhotosItemEntity(full = ""))
                } else {
                    baseStateModel.data.photos
                }
                getPetPhotoAdapter()?.submitList(photoList) {
                    if (!getViewModel().openingAnimationRequired) return@submitList
                    startPostponedEnterTransition()
                    getViewDataBinding().appbar.postDelayed({
                        getViewDataBinding().appbar.setExpanded(true, true)
                    }, 400L)
                }
            }
        }
    }

    private val phoneObserver = Observer<String> { phoneNum ->
        val phoneMenuItem =
            getViewDataBinding().bottomAppBar.menu?.findItem(R.id.menu_phone) ?: return@Observer
        phoneMenuItem.isVisible = phoneNum.isNotEmpty()
    }

    private val emailObserver = Observer<String> { email ->
        val emailMenuItem =
            getViewDataBinding().bottomAppBar.menu?.findItem(R.id.menu_email) ?: return@Observer
        emailMenuItem.isVisible = email.isNotEmpty()
    }

    private val shareUrlObserver = Observer<String> { url ->
        val shareMenuItem =
            getViewDataBinding().bottomAppBar.menu?.findItem(R.id.menu_share) ?: return@Observer
        shareMenuItem.isVisible = url.isNotEmpty()
    }

    private val appBarOffsetChangedListener = object : AppBarStateChangeListener(State.EXPANDED) {
        override fun onStateChange(appBarLayout: AppBarLayout, state: State) {
        }

        override fun onOffsetChange(appBarLayout: AppBarLayout, offset: Int) {
        }
    }

    private fun updateErrorState(throwable: Throwable) {
        val errorTipple = throwable.getErrorRequestMessage(requireContext())
        errorState.apply {
            this.errorScreenDrawable = ContextCompat.getDrawable(
                requireContext(), R.drawable.ic_launcher_background
            )
            this.errorScreenText = errorTipple.first
            this.errorScreenTextSubTitle = errorTipple.second
            this.errorBtnText = getString(R.string.retry)
            this.errorRetryClickListener = View.OnClickListener {
            }
        }
    }

    private fun prepareTransitions() {
        postponeEnterTransition()
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.pet_motion_default_large).toLong()
            interpolator = requireContext().themeInterpolator(R.attr.motionInterpolatorPersistent)
            pathMotion = MaterialArcMotion()
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
        }

        sharedElementReturnTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.rv_pets
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
            interpolator = requireContext().themeInterpolator(R.attr.motionInterpolatorPersistent)
            pathMotion = MaterialArcMotion()
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
        }
    }

    private val menuClickListener = Toolbar.OnMenuItemClickListener {
        when (it?.itemId ?: return@OnMenuItemClickListener false) {
            R.id.menu_phone -> {
                val phoneNum = try {
                    getViewModel().phoneNumLiveData.value?.replace(", ", ",")
                        ?.split(",")?.last()
                } catch (exception: Exception) {
                    CrashlyticsExt.logHandledException(exception)
                    null
                } ?: return@OnMenuItemClickListener false

                requireContext().openDialer(phoneNum)
            }
            R.id.menu_email -> requireContext().sendEmail(
                arrayOf(
                    getViewModel().emailLiveData.value ?: ""
                )
            )

            R.id.menu_share -> requireContext().share(
                getViewModel().urlLiveData.value,
                getString(R.string.check_cute_one),
                getString(R.string.share_with)
            )
        }
        true
    }

    private fun hideFab() {
        getViewDataBinding().fab.hide()
    }

    private fun showFab() {
        getViewDataBinding().fab.show()
    }

    private val petPhotoViewerListener = object : PetPhotoViewerAdapter.PetPhotoViewerListener {

        override fun onImageLoaded() {
        }

        override fun onItemClickListener() {
        }
    }

    private val navigationObserver = Observer<Pair<PetEntity, View>> {
        val pair = it ?: return@Observer
        getViewModel().openingAnimationRequired = true
        handleNavArgs(pair.first.id, petId)
    }

}