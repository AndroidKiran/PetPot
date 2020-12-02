package com.droid47.petpot.petDetails.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.droid47.petpot.R
import com.droid47.petpot.base.bindingConfig.ContentLoadingConfiguration
import com.droid47.petpot.base.bindingConfig.ErrorViewConfiguration
import com.droid47.petpot.base.extensions.*
import com.droid47.petpot.base.firebase.AnalyticsScreens
import com.droid47.petpot.base.firebase.CrashlyticsExt
import com.droid47.petpot.base.widgets.*
import com.droid47.petpot.base.widgets.anim.ResizeAnimation
import com.droid47.petpot.base.widgets.components.AppBarStateChangeListener
import com.droid47.petpot.databinding.FragmentPetDetailsBinding
import com.droid47.petpot.home.presentation.ui.HomeActivity
import com.droid47.petpot.home.presentation.viewmodels.HomeViewModel
import com.droid47.petpot.petDetails.presentation.adapter.PetPhotoViewerAdapter
import com.droid47.petpot.petDetails.presentation.viewmodels.PetDetailsViewModel
import com.droid47.petpot.search.data.models.search.PetEntity
import com.droid47.petpot.search.data.models.search.PhotosItemEntity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import javax.inject.Inject

class PetDetailsFragment :
    BaseBindingFragment<FragmentPetDetailsBinding, PetDetailsViewModel, HomeViewModel>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private var resizeAnimation: ResizeAnimation? = null
    private val args: PetDetailsFragmentArgs by navArgs()
    private val petId: Int by lazy(LazyThreadSafetyMode.NONE) { args.petId }

    private val loadingState = ContentLoadingConfiguration()
    private val errorState = ErrorViewConfiguration()
    private var backDropHeight: Int = 300
    private var screenHeight: Int = 800

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

    override fun getClassName(): String = PetDetailsFragment::class.java.simpleName

    override fun getScreenName(): String = AnalyticsScreens.PET_DETAILS_SCREEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenHeight = requireContext().getScreenHeight()
        backDropHeight = screenHeight - resources.getDimensionPixelOffset(R.dimen.grid_14)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareTransitions()
        similarPetsFragment.subscribeToSubListSelection(navigationObserver)
        setUpView()
        setPetPhotoAdapter()
        handleNavArgs(petId, petId)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscribeToLiveData()
    }

    override fun onStart() {
        super.onStart()
        getViewDataBinding().viewPager.registerOnPageChangeCallback(onPageChangeCallback)
    }

    override fun onResume() {
        super.onResume()
        trackFragment(getViewModel().firebaseManager)
    }

    override fun onStop() {
        resizeAnimation?.cancel()
        getViewDataBinding().viewPager.unregisterOnPageChangeCallback(onPageChangeCallback)
        super.onStop()
    }

    private fun handleNavArgs(petId: Int, transitionId: Int) {
        with(getViewModel()) {
            this.petId.postValue(petId)
            this.transitionId.postValue(transitionId)
        }
    }

    private fun setUpView() {

        getViewDataBinding().fab.apply {
            setImageResource(R.drawable.vc_favorite)
            setOnClickListener {
                hide()
                getViewModel().run {
                    resizeAnimationRequired = false
                    onBookMarkClick(
                        getViewModel().petLiveData.value?.data ?: return@setOnClickListener
                    )
                }
            }
        }

        getViewDataBinding().bottomAppBar.apply {
            setOnMenuItemClickListener(menuClickListener)
            replaceMenu(R.menu.pet_details_menu)
            setNavigationOnClickListener {
                getParentViewModel().eventLiveData.postValue(HomeViewModel.EVENT_NAVIGATE_BACK)
            }
        }

        getViewDataBinding().btnNavSearch.setOnClickListener {
            getParentViewModel().eventLiveData.postValue(HomeViewModel.EVENT_NAVIGATE_BACK)
        }

        getViewDataBinding().includePetDetails.btnShowOnMap.setOnClickListener {
            getViewModel().trackDetailsToMap()
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
            setPageTransformer { page, position ->
                page.setParallaxTransformation(position)
            }
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
                val petEntity = baseStateModel.data
                val photoList = if (petEntity.photos.isNullOrEmpty()) {
                    listOf(PhotosItemEntity(full = ""))
                } else {
                    baseStateModel.data.photos
                }
                getPetPhotoAdapter()?.setModifiedAt(petEntity.getPublishedAtInLong())
                getPetPhotoAdapter()?.submitList(photoList) {
                    startPostponedEnterTransition()
                    showFab()
                    if (!getViewModel().resizeAnimationRequired) return@submitList
                    getViewModel().resizeAnimationRequired = false
                    animateContentView()
                }
            }
            is Empty -> TODO()
        }
    }

    private fun animateContentView() {
        val context = context ?: return
        resizeAnimation = ResizeAnimation(getViewDataBinding().appbar, backDropHeight).apply {
            this.duration = 300L
            this.interpolator = context.themeInterpolator(R.attr.motionInterpolatorIncoming)
        }
        getViewDataBinding().appbar.postDelayed({
            getViewDataBinding().appbar.startAnimation(resizeAnimation ?: return@postDelayed)
        }, 800L)
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
        val context = context ?: return
        val errorTipple = throwable.getErrorRequestMessage(context)
        errorState.apply {
            this.errorScreenDrawable = ContextCompat.getDrawable(
                context, R.drawable.ic_launcher_background
            )
            this.errorScreenText = errorTipple.first
            this.errorScreenTextSubTitle = errorTipple.second
            this.errorBtnText = getString(R.string.retry)
            this.errorRetryClickListener = View.OnClickListener {
            }
        }
    }

    private fun prepareTransitions() {
        val context = context ?: return
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
            interpolator = context.themeInterpolator(R.attr.motionInterpolatorPersistent)
            setPathMotion(MaterialArcMotion())
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }

        sharedElementReturnTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.rv_pets
            duration = resources.getInteger(R.integer.pet_motion_duration_small).toLong()
            interpolator = context.themeInterpolator(R.attr.motionInterpolatorPersistent)
            setPathMotion(MaterialArcMotion())
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
        }

        postponeEnterTransition()
    }

    private val menuClickListener = Toolbar.OnMenuItemClickListener {
        val context = context ?: return@OnMenuItemClickListener false
        when (it?.itemId ?: return@OnMenuItemClickListener false) {
            R.id.menu_phone -> {
                getViewModel().trackCall()
                val phoneNum = try {
                    getViewModel().phoneNumLiveData.value?.replace(", ", ",")
                        ?.split(",")?.last()
                } catch (exception: Exception) {
                    CrashlyticsExt.handleException(exception)
                    null
                } ?: return@OnMenuItemClickListener false
                context.openDialer(phoneNum)
            }
            R.id.menu_email -> {
                getViewModel().trackMail()
                context.sendEmail(
                    arrayOf(
                        getViewModel().emailLiveData.value ?: ""
                    )
                )
            }

            R.id.menu_share -> {
                getViewModel().trackShare()
                context.share(
                    getViewModel().urlLiveData.value,
                    getString(R.string.check_cute_one),
                    getString(R.string.share_with)
                )
            }
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
        hideFab()
        getViewModel().resizeAnimationRequired = false
        handleNavArgs(pair.first.id, petId)
        getViewDataBinding().appbar.setExpanded(true)
        getViewDataBinding().nsvPetDetails.fullScroll(View.FOCUS_UP)
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            getViewModel().trackPetImageSwipe()
        }

    }

}