package com.droid47.petpot.search.presentation.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import com.droid47.petpot.R
import com.droid47.petpot.base.bindingConfig.ContentLoadingConfiguration
import com.droid47.petpot.base.bindingConfig.EmptyScreenConfiguration
import com.droid47.petpot.base.bindingConfig.ErrorViewConfiguration
import com.droid47.petpot.base.extensions.*
import com.droid47.petpot.base.firebase.AnalyticsScreens
import com.droid47.petpot.base.widgets.BaseBindingFragment
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.anim.SpringAddItemAnimator
import com.droid47.petpot.base.widgets.currentLocation.*
import com.droid47.petpot.base.widgets.snappy.SnapType
import com.droid47.petpot.base.widgets.snappy.SnappyGridLayoutManager
import com.droid47.petpot.databinding.FragmentSearchBinding
import com.droid47.petpot.home.presentation.ui.HomeActivity
import com.droid47.petpot.home.presentation.viewmodels.HomeViewModel
import com.droid47.petpot.home.presentation.viewmodels.HomeViewModel.Companion.EVENT_TOGGLE_NAVIGATION
import com.droid47.petpot.search.data.models.search.PetEntity
import com.droid47.petpot.search.presentation.models.*
import com.droid47.petpot.search.presentation.models.PetFilterConstants.PAGE_ONE
import com.droid47.petpot.search.presentation.ui.widgets.PagedListPetAdapter
import com.droid47.petpot.search.presentation.viewmodel.FilterViewModel.Companion.EVENT_APPLY_FILTER
import com.droid47.petpot.search.presentation.viewmodel.FilterViewModel.Companion.EVENT_CLOSE_FILTER
import com.droid47.petpot.search.presentation.viewmodel.PetSpinnerAndLocationViewModel
import com.droid47.petpot.search.presentation.viewmodel.PetSpinnerAndLocationViewModel.Companion.EVENT_CURRENT_LOCATION
import com.droid47.petpot.search.presentation.viewmodel.SearchViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchFragment :
    BaseBindingFragment<FragmentSearchBinding, SearchViewModel, HomeViewModel>(),
    View.OnClickListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val loadingState = ContentLoadingConfiguration()
    private val emptyState = EmptyScreenConfiguration()
    private val errorState = ErrorViewConfiguration()
    private var resultOrderMenuItem: MenuItem? = null
    private val locationHandler = Handler(Looper.getMainLooper())

    private val springAddItemAnimator: SpringAddItemAnimator by lazy(LazyThreadSafetyMode.NONE) {
        SpringAddItemAnimator(SpringAddItemAnimator.Direction.DirectionY)
    }

    private val searchViewModel: SearchViewModel by lazy(LazyThreadSafetyMode.NONE) {
        viewModelProvider<SearchViewModel>(factory)
    }

    private val petSpinnerAndLocationViewModel: PetSpinnerAndLocationViewModel by lazy(
        LazyThreadSafetyMode.NONE
    ) {
        viewModelProvider<PetSpinnerAndLocationViewModel>(factory)
    }

    private val homeViewModel: HomeViewModel by lazy(LazyThreadSafetyMode.NONE) {
        requireActivity().activityViewModelProvider<HomeViewModel>()
    }

    private val filterFragment: FilterFragment by lazy(LazyThreadSafetyMode.NONE) {
        childFragmentManager.findFragmentById(R.id.fragment_filter) as FilterFragment
    }

    private val pagedListPetAdapter: PagedListPetAdapter by lazy(LazyThreadSafetyMode.NONE) {
        PagedListPetAdapter(
            requireContext(),
            PagedListPetAdapter.AdapterType.Search,
            getViewModel()
        )
    }

    override fun getLayoutId(): Int = R.layout.fragment_search

    override fun getViewModel(): SearchViewModel = searchViewModel

    override fun getParentViewModel(): HomeViewModel = homeViewModel

    override fun getSnackBarAnchorView(): View = getViewDataBinding().fab

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.lifecycleOwner = viewLifecycleOwner
            it.searchViewModel = searchViewModel
            it.loadingStateConfig = loadingState
            it.emptyStateConfig = emptyState
            it.errorStateConfig = errorState
            it.petSpinnerViewModelAndLocation = petSpinnerAndLocationViewModel
        }
    }

    override fun getFragmentNavId(): Int = R.id.navigation_search

    override fun injectSubComponent() {
        (activity as HomeActivity).homeComponent.inject(this)
    }

    override fun getClassName(): String = SearchFragment::class.java.simpleName

    override fun getScreenName(): String = AnalyticsScreens.SEARCH_SCREEN

    override fun onAttach(context: Context) {
        super.onAttach(context)
        subscribeToLiveData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition(1000L, TimeUnit.MILLISECONDS)
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
        setUpView()
        setupSearchRvAdapter()
    }

//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        getViewModel().listState?.let { state ->
//            getViewDataBinding().rvPets.layoutManager?.onRestoreInstanceState(state)
//        }
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        getViewModel().listState = getViewDataBinding().rvPets.layoutManager?.onSaveInstanceState()
//    }

    override fun onResume() {
        super.onResume()
        trackFragment(getViewModel().firebaseManager)
    }

    override fun onPause() {
        springAddItemAnimator.endAnimations()
        super.onPause()
    }

    override fun onStop() {
        cancelPaginationRequest()
        super.onStop()
    }

    override fun onClick(view: View?) {
        when (view?.id ?: return) {
            R.id.scrim -> performMaterialTransitionFor(
                filterFragment.getViewDataBinding().root,
                getViewDataBinding().fab
            )

            R.id.fab -> {
                getViewModel().trackSearchToFilter()
                performMaterialTransitionFor(
                    getViewDataBinding().fab,
                    filterFragment.getViewDataBinding().root
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        }
    }

    private fun initTransition() {
        enterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.pet_motion_default_large).toLong()
        }

        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
        }
    }

    private fun setUpView() {
        with(getViewDataBinding().fab) {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setImageResource(R.drawable.vc_filter)
            setOnClickListener(this@SearchFragment)
        }

        with(getViewDataBinding().bottomAppBar) {
            setOnMenuItemClickListener(onMenuClickListener)
            resultOrderMenuItem = menu.findItem(R.id.menu_order)
            setNavigationOnClickListener {
                getParentViewModel().eventLiveData.postValue(EVENT_TOGGLE_NAVIGATION)
            }
        }

        getViewDataBinding().btnNavSearch.setOnClickListener {
            getParentViewModel().eventLiveData.postValue(EVENT_TOGGLE_NAVIGATION)
        }

        resultOrderMenuItem?.actionView?.let { view ->
            view.setOnClickListener {
                filterFragment.showSortPopup(view)
            }
        }

        getViewDataBinding().scrim.setOnClickListener(this@SearchFragment)

        with(getViewDataBinding().fab) {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
        }

        getViewDataBinding().topSearchBar.btnEditOrg.setOnClickListener {
            val view = it ?: return@setOnClickListener

            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.pet_motion_duration_small).toLong()
            }

            val extras = FragmentNavigatorExtras(
                view to view.transitionName
            )
            getParentViewModel().homeNavigator.toOrganizationFromSearch(extras)
        }
    }

    private fun setupSearchRvAdapter() {
//        if (getPetAdapter() != null) return
        getViewDataBinding().rvPets.apply {
            itemAnimator = springAddItemAnimator
//            GravitySnapHelper(Gravity.TOP).attachToRecyclerView(this)
            layoutManager = SnappyGridLayoutManager(requireContext(), 3).apply {
                setSnapType(SnapType.START)
                setSnapInterpolator(FastOutSlowInInterpolator())
                setSeekDuration(resources.getInteger(R.integer.pet_motion_default_large))
                spanSizeLookup = gridSpanListener
            }
            adapter = pagedListPetAdapter
        }
    }

    private fun subscribeToLiveData() {
        getViewModel().petsLiveData.run {
            removeObserver(petsObserver)
            observe(requireActivity(), petsObserver)
        }

        getViewModel().itemPaginationStateLiveData.run {
            removeObserver(searchObserver)
            observe(requireActivity(), searchObserver)
        }

        getViewModel().navigateToAnimalDetailsAction.run {
            removeObserver(navigationObserver)
            observe(requireActivity(), navigationObserver)
        }

        petSpinnerAndLocationViewModel.locationLiveData.run {
            removeObserver(locationObserver)
            observe(requireActivity(), locationObserver)
        }

        getViewModel().eventLiveData.run {
            removeObserver(homeEventObserver)
            observe(requireActivity(), homeEventObserver)
        }

        petSpinnerAndLocationViewModel.eventLiveData.run {
            removeObserver(petSpinnerEventObserver)
            observe(requireActivity(), petSpinnerEventObserver)
        }
    }

    private val navigationObserver = Observer<Pair<PetEntity, View>> {
        val petViewPair = it ?: return@Observer
        getViewModel().trackSearchToDetails()

        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.pet_motion_duration_small).toLong()
        }
        val extras = FragmentNavigatorExtras(
            petViewPair.second to petViewPair.second.transitionName
        )
        getParentViewModel().homeNavigator.toPetDetailsFromSearch(petViewPair.first.id, extras)
    }

    private val petsObserver = Observer<BaseStateModel<out PagedList<PetEntity>>> {
        springAddItemAnimator.endAnimations()
        val baseStateModel = it ?: return@Observer
        pagedListPetAdapter.submitList(baseStateModel.data) {
            val paginationEntity =
                getViewModel().itemPaginationStateLiveData.value?.paginationEntity
                    ?: return@submitList
            if (PAGE_ONE == paginationEntity.currentPage) {
                showBottomBar()
            }
        }
    }

    private val searchObserver = Observer<ItemPaginationState> {
        val searchState = it ?: return@Observer

        when (searchState) {
            is DefaultState -> {
                hidePaginationProgress()
            }

            is LoadingState -> {
                hidePaginationProgress()
                hideBottomBar()
                getViewDataBinding().appbar.setExpanded(true, true)
            }

            is PaginatingState -> {
                showPaginationProgress()
                hideBottomBar()
            }

            is EmptyState -> {
                hidePaginationProgress()
                updateEmptyState()
                hideKeyboard()
                showBottomBar()
            }

            is ErrorState -> {
                hidePaginationProgress()
                val error = searchState.error
                showErrorState(error)
                hideKeyboard()
                showBottomBar()
            }

            is PaginationErrorState -> {
                hidePaginationProgress()
                val error = searchState.error
                showErrorSnackBar(error)
            }
        }
    }

    private fun hideKeyboard() {
        Handler(Looper.getMainLooper()).postDelayed({
            getViewDataBinding().topSearchBar.cvSearch.hideKeyboard()
        }, 2000)
    }

    private val locationObserver = Observer<String> { locationStr ->
        val location = locationStr ?: return@Observer
        val locationRunnable = Runnable {
            getViewModel().updateLocation(location)
        }
        locationHandler.removeCallbacks(locationRunnable)
        locationHandler.postDelayed(locationRunnable, 600L)
    }

    private val homeEventObserver = Observer<Long> {
        when (it ?: return@Observer) {
            EVENT_APPLY_FILTER,
            EVENT_CLOSE_FILTER -> performMaterialTransitionFor(
                filterFragment.getViewDataBinding().root,
                getViewDataBinding().fab
            )
            else -> throw IllegalStateException("Invalid event")
        }
    }

    private val petSpinnerEventObserver = Observer<Long> {
        when (it ?: return@Observer) {
            EVENT_CURRENT_LOCATION -> {
                getCurrentLocation()
            }
        }
    }

    private fun hidePaginationProgress() {
        getViewDataBinding().circularProgress.gone()
    }

    private fun showPaginationProgress() {
        getViewDataBinding().circularProgress.show()
    }

//    private fun getPetAdapter() = getViewDataBinding().rvPets.adapter as? PagedListPetAdapter

    private fun updateEmptyState() {
        val context = context ?: return
        emptyState.apply {
            this.emptyScreenDrawable = ContextCompat.getDrawable(
                context, R.drawable.ic_launcher_background
            )
            this.emptyScreenTitleText = getString(R.string.no_animal_found)
            this.emptyScreenSubTitleText = getString(R.string.reset_filter)
        }
    }

    private fun showErrorState(throwable: Throwable) {
        val context = context ?: return
        val errorTriple = throwable.getErrorRequestMessage(context)
        errorState.apply {
            this.errorScreenDrawable = ContextCompat.getDrawable(
                context, R.drawable.ic_launcher_background
            )
            this.errorScreenText = errorTriple.first
            this.errorScreenTextSubTitle = errorTriple.second
            this.errorBtnText = errorTriple.third
            this.errorRetryClickListener = View.OnClickListener {
                if (getString(R.string.clear) == errorTriple.third) {
                    petSpinnerAndLocationViewModel.onClearLocation()
                } else {
                    getViewModel().retryPagination()
                }
            }
        }
    }

    private fun showErrorSnackBar(throwable: Throwable) {
        val context = context ?: return
        val errorTriple = throwable.getErrorRequestMessage(context)
        Snackbar.make(getViewDataBinding().fab, errorTriple.first, Snackbar.LENGTH_LONG)
            .setAnchorView(getSnackBarAnchorId()).show()
    }

    private fun getSnackBarAnchorId(): Int =
        if (getViewDataBinding().fab.visibility == View.VISIBLE)
            getViewDataBinding().fab.id
        else
            getViewDataBinding().bottomAppBar.id

    private fun hideFab() {
        getViewDataBinding().fab.hide()
    }

    private fun showFab() {
        getViewDataBinding().fab.show()
    }

    private fun hideBottomBar() {
        getViewDataBinding().bottomAppBar.performHide()
    }

    private fun showBottomBar() {
        getViewDataBinding().bottomAppBar.performShow()
    }

    private val gridSpanListener = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int =
            when {
                position % 31 == 0 -> 3
                else -> 1
            }
    }

    private val onMenuClickListener = Toolbar.OnMenuItemClickListener {
        val menuItem = it ?: return@OnMenuItemClickListener false
        when (menuItem.itemId) {

            else -> false
        }
    }

    private fun getCurrentLocation() {
        val context = context ?: return
        FetchCurrentLocationLiveData(
            context
        ).run {
            removeObserver(locationUpdateObserver)
            observe(viewLifecycleOwner, locationUpdateObserver)
        }
    }

    private val locationUpdateObserver =
        Observer<CurrentLocationState> {
            val currentLocationState = it ?: return@Observer
            when (currentLocationState) {
                is EnableLocationState -> {
                    Snackbar.make(
                        getViewDataBinding().fab,
                        "Turn on location",
                        Snackbar.LENGTH_LONG
                    )
                        .setAnchorView(getSnackBarAnchorId())
                        .setAction(getString(R.string.to_settings)) {
                            requireActivity().startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        }.show()
                }

                is RequestLocationPermissionState -> {
                    requestPermissions()
                }

                is UpdatedLocationState -> {
                    val location = currentLocationState.location ?: return@Observer
                    val address = context?.getAddressFromLocation(location) ?: return@Observer
                    petSpinnerAndLocationViewModel.locationLiveData.postValue(address)
                }
            }
        }

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }

    private fun performMaterialTransitionFor(startView: View, endView: View) {
        activity?.hideKeyboard()
        val isExpanding = isExpanding(startView)
        val transition = MaterialContainerTransform().apply {
            this.startView = startView
            this.endView = endView
            this.scrimColor = Color.TRANSPARENT
            this.drawingViewId = getViewDataBinding().cdlMain.id
            this.duration = resources.getInteger(
                if (isExpanding)
                    R.integer.pet_motion_duration_medium
                else
                    R.integer.pet_motion_duration_small
            ).toLong()
            this.interpolator = FastOutSlowInInterpolator()
            this.setPathMotion(MaterialArcMotion())
            this.fadeMode =
                if (isExpanding) MaterialContainerTransform.FADE_MODE_IN else MaterialContainerTransform.FADE_MODE_OUT
            this.addTarget(startView)
            this.isElevationShadowEnabled = isExpanding
        }.addListener(object : TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                updateViewOnTransitionComplete(startView, endView)
            }

            override fun onTransitionCancel(transition: Transition) {
                super.onTransitionCancel(transition)
                updateViewOnTransitionComplete(startView, endView)
            }
        })
        endView.visible()
        TransitionManager.beginDelayedTransition(getViewDataBinding().cdlMain, transition)
    }

    private fun isExpanding(startView: View) = startView is FloatingActionButton

    private fun updateViewOnTransitionComplete(startView: View, endView: View) {
        if (isExpanding(startView)) {
            hideBottomBar()
            if (getViewModel().itemPaginationStateLiveData.value is PaginatingState) {
                hidePaginationProgress()
            }
            startView.gone()
            getViewDataBinding().scrim.visible()
            filterFragment.onFilterExpanded()
        } else {
            showBottomBar()
            filterFragment.onFilterCollapsed()
            getViewDataBinding().scrim.gone()
            startView.gone()
            if (getViewModel().itemPaginationStateLiveData.value is PaginatingState) {
                showPaginationProgress()
            }
        }
    }

    private fun cancelPaginationRequest() {
        getViewModel().cancelPagination()
        hidePaginationProgress()
    }

    companion object {
        const val TAG = "search_fragment"
    }
}