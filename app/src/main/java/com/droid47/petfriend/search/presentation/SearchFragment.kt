package com.droid47.petfriend.search.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.droid47.petfriend.R
import com.droid47.petfriend.base.bindingConfig.ContentLoadingConfiguration
import com.droid47.petfriend.base.bindingConfig.EmptyScreenConfiguration
import com.droid47.petfriend.base.bindingConfig.ErrorViewConfiguration
import com.droid47.petfriend.base.extensions.*
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.paginatedRecyclerView.PaginationScrollListener
import com.droid47.petfriend.base.widgets.BaseBindingFragment
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.base.widgets.currentLocation.*
import com.droid47.petfriend.base.widgets.snappy.SnapType
import com.droid47.petfriend.base.widgets.snappy.SnappyGridLayoutManager
import com.droid47.petfriend.databinding.FragmentSearchBinding
import com.droid47.petfriend.home.presentation.HomeActivity
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel.Companion.EVENT_TOGGLE_NAVIGATION
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.presentation.SearchFragmentDirections.Companion.toPetDetails
import com.droid47.petfriend.search.presentation.models.*
import com.droid47.petfriend.search.presentation.viewmodel.FilterViewModel.Companion.EVENT_APPLY_FILTER
import com.droid47.petfriend.search.presentation.viewmodel.FilterViewModel.Companion.EVENT_CLOSE_FILTER
import com.droid47.petfriend.search.presentation.viewmodel.PetSpinnerAndLocationViewModel
import com.droid47.petfriend.search.presentation.viewmodel.PetSpinnerAndLocationViewModel.Companion.EVENT_CURRENT_LOCATION
import com.droid47.petfriend.search.presentation.viewmodel.SearchViewModel
import com.droid47.petfriend.search.presentation.widgets.PetAdapter
import com.droid47.petfriend.search.presentation.widgets.PetAdapter.Companion.SEARCH
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject
import kotlin.random.Random

class SearchFragment :
    BaseBindingFragment<FragmentSearchBinding, SearchViewModel, HomeViewModel>(),
    View.OnClickListener {
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val loadingState = ContentLoadingConfiguration()
    private val emptyState = EmptyScreenConfiguration()
    private val errorState = ErrorViewConfiguration()
    private var isLoading = false
    private var isLastPage = false
    private var resultOrderMenuItem: MenuItem? = null

    private val searchViewModel: SearchViewModel by lazy {
        viewModelProvider<SearchViewModel>(factory)
    }

    private val petSpinnerAndLocationViewModel: PetSpinnerAndLocationViewModel by lazy {
        viewModelProvider<PetSpinnerAndLocationViewModel>(factory)
    }

    private val homeViewModel: HomeViewModel by lazy {
        activityViewModelProvider<HomeViewModel>(requireActivity())
    }

    private val filterFragment: FilterFragment by lazy(LazyThreadSafetyMode.NONE) {
        childFragmentManager.findFragmentById(R.id.fragment_filter) as FilterFragment
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as HomeActivity).homeComponent.inject(this)
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
        setUpView()
        setupSearchRvAdapter()
//        toggleMenuVisibility(
//            getViewDataBinding().topSearchBar.tvLocation.text.toString().isNotEmpty()
//        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscribeToLiveData()
    }

    override fun onClick(view: View?) {
        when (view?.id ?: return) {
            R.id.scrim,
            R.id.fab -> {
                updateFabStatus()
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

    private fun setUpView() {
        with(getViewDataBinding().fab) {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setImageResource(R.drawable.vc_filter)
            setOnClickListener(this@SearchFragment)
        }

        with(getViewDataBinding().bottomAppBar) {
            setNavigationIcon(R.drawable.vc_nav_menu)
            setNavigationOnClickListener(this@SearchFragment)
            setOnMenuItemClickListener(onMenuClickListener)
            resultOrderMenuItem = menu.findItem(R.id.menu_order)
            setNavigationOnClickListener {
                getParentViewModel().eventLiveData.postValue(EVENT_TOGGLE_NAVIGATION)
            }
        }

        resultOrderMenuItem?.actionView?.let { view ->
            view.setOnClickListener {
                filterFragment.showSortPopup(view)
            }
        }

        with(getViewDataBinding().scrim) {
            setOnClickListener(this@SearchFragment)
        }

        with(getViewDataBinding().fab) {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
        }
    }

    private fun setupSearchRvAdapter() {
        if (getSearchAdapter() == null) {
            getViewDataBinding().rvSearch.apply {
                layoutManager = SnappyGridLayoutManager(requireContext(), 3).apply {
                    setSnapType(SnapType.START)
                    setSnapInterpolator(DecelerateInterpolator())
                    setSnapDuration(300)
                    spanSizeLookup = gridSpanListener
                }
                adapter = PetAdapter(requireContext(), SEARCH, getViewModel())
                addOnScrollListener(onScrollListener)
            }
        }
    }

    private fun subscribeToLiveData() {
        getViewModel().searchStateLiveData.run {
            removeObserver(searchObserver)
            observe(viewLifecycleOwner, searchObserver)
        }

        getViewModel().navigateToAnimalDetailsAction.run {
            removeObserver(navigationObserver)
            observe(viewLifecycleOwner, navigationObserver)
        }

        getViewModel().filterItemLiveData.run {
            removeObserver(filterObserver)
            observe(viewLifecycleOwner, filterObserver)
        }

        petSpinnerAndLocationViewModel.locationLiveData.run {
            removeObserver(locationObserver)
            observe(viewLifecycleOwner, locationObserver)
        }

        getViewModel().eventLiveData.run {
            removeObserver(homeEventObserver)
            observe(viewLifecycleOwner, homeEventObserver)
        }

        petSpinnerAndLocationViewModel.eventLiveData.run {
            removeObserver(petSpinnerEventObserver)
            observe(viewLifecycleOwner, petSpinnerEventObserver)
        }
    }

    private val navigationObserver = Observer<Pair<PetEntity, View>> {
        val petViewPair = it ?: return@Observer
        if (findNavController().currentDestination?.id != R.id.navigation_pet_details) {
            fetchRandomPetList()
            val extras = FragmentNavigatorExtras(
                petViewPair.second to petViewPair.second.transitionName
            )
            findNavController().navigate(toPetDetails(petViewPair.first), extras)
        }
    }

    private fun fetchRandomPetList() {
        val petList = getViewModel().searchStateLiveData.value?.data ?: emptyList()
        if (petList.isEmpty()) return
        val totalItems = petList.size - 1
        val itemList = mutableListOf<PetEntity>()
        for (index in totalItems downTo 0) {
            if (itemList.size >= 10) {
                break
            } else {
                val petEntity = petList[Random.nextInt(0, totalItems)]
                if (!petEntity.bookmarkStatus && petEntity.getPetPhoto().isNotEmpty()) {
                    itemList.add(petEntity)
                }
            }
        }
        getParentViewModel().similarPetList.postValue(itemList.distinctBy { petEntity -> petEntity.id }
            .toList())
    }

    private val searchObserver = Observer<SearchState> {
        val searchState = it ?: return@Observer
        isLastPage = searchState.paginationEntity.let { pagination ->
            pagination.currentPage == pagination.totalPages
        }

        val isFirstPage = searchState.paginationEntity.currentPage == 1

//        toggleMenuVisibility((searchState is DefaultState || searchState is PaginatingState)
//                    && getViewDataBinding().topSearchBar.tvLocation.text.toString().isNotEmpty())

        when (searchState) {
            is DefaultState -> {
                clearPaginationUI()
                getSearchAdapter()?.submitList(searchState.data) {
                    if (isFirstPage) {
                        showBottomBar()
                    }
//                    showFab()
                    hideKeyboard()
                }
            }

            is LoadingState -> {
                clearPaginationUI()
//                hideBottomBar()
//                hideFab()
                getSearchAdapter()?.submitList(emptyList())
            }

            is PaginatingState -> {
                isLoading = true
                hideBottomBar()
            }

            is EmptyState -> {
                clearPaginationUI()
//                showBottomBar()
//                showFab()
                updateEmptyState()
                hideKeyboard()
            }

            is ErrorState -> {
                clearPaginationUI()
//                showBottomBar()
//                showFab()
                val error = searchState.error
                showErrorState(error)
                hideKeyboard()
            }

            is PaginationErrorState -> {
                clearPaginationUI()
                val error = searchState.error
                showErrorSnackBar(error)
            }
        }
    }

    private fun hideKeyboard() {
        Handler().postDelayed({
            getViewDataBinding().topSearchBar.cvSearch.hideKeyboard()
        }, 2000)
    }

    private val filterObserver = Observer<BaseStateModel<Filters>> {
        val baseStateModel = it ?: return@Observer
        when (baseStateModel) {
            is Success -> {
                val filters = baseStateModel.data
                getViewModel().onFilterModified(filters)
            }

            is Failure -> {
                val error = baseStateModel.error
            }
        }
    }

    private val locationObserver = Observer<String> { locationStr ->
        val location = locationStr ?: return@Observer
        val appliedLocation = getViewModel().filterItemLiveData.value?.data?.location ?: ""
        if (location != appliedLocation) {
            getViewModel().updateLocation(location)
        }
    }

    private val homeEventObserver = Observer<Int> {
        when (it ?: return@Observer) {
            EVENT_APPLY_FILTER,
            EVENT_CLOSE_FILTER -> updateFabStatus()
            else -> throw IllegalStateException("Invalid event")
        }
    }

    private val petSpinnerEventObserver = Observer<Int> {
        when (it ?: return@Observer) {
            EVENT_CURRENT_LOCATION -> {
                getCurrentLocation()
            }
        }
    }

    private fun clearPaginationUI() {
        isLoading = false
        getViewDataBinding().circularProgress.gone()
    }

    private fun getSearchAdapter() = getViewDataBinding().rvSearch.adapter as? PetAdapter

    private fun showLoadMore() {
        getViewDataBinding().circularProgress.visible()
        getViewModel().updatePage()
    }

    private fun updateEmptyState() {
        emptyState.apply {
            this.emptyScreenDrawable = ContextCompat.getDrawable(
                requireContext(), R.drawable.ic_launcher_background
            )
            this.emptyScreenTitleText = getString(R.string.no_animal_found)
            this.emptyScreenSubTitleText = getString(R.string.reset_filter)
        }
    }

    private fun showErrorState(throwable: Throwable) {
        val errorTriple = throwable.getErrorRequestMessage(requireContext())
        errorState.apply {
            this.errorScreenDrawable = ContextCompat.getDrawable(
                requireContext(), R.drawable.ic_launcher_background
            )
            this.errorScreenText = errorTriple.first
            this.errorScreenTextSubTitle = errorTriple.second
            this.errorBtnText = errorTriple.third
            this.errorRetryClickListener = View.OnClickListener {
                val baseStateModel =
                    getViewModel().filterItemLiveData.value ?: return@OnClickListener
                when (baseStateModel) {
                    is Success -> {
                        if (getString(R.string.clear) == errorTriple.third) {
                            petSpinnerAndLocationViewModel.onClearLocation()
                        } else {
                            getViewModel().onFilterModified(baseStateModel.data)
                        }
                    }

                    is Failure -> {
                        val error = baseStateModel.error
                    }
                }
            }
        }
    }

    private fun showErrorSnackBar(throwable: Throwable) {
        val errorTriple = throwable.getErrorRequestMessage(requireContext())
        Snackbar.make(getViewDataBinding().fab, errorTriple.first, Snackbar.LENGTH_LONG)
            .setAnchorView(getSnackBarAnchorId()).show()
    }

    private fun getSnackBarAnchorId(): Int =
        if (getViewDataBinding().fab.visibility == View.VISIBLE)
            getViewDataBinding().fab.id
        else
            getViewDataBinding().bottomAppBar.id

    private val onScrollListener = object : PaginationScrollListener() {
        override fun isLoading() = isLoading
        override fun loadMoreItems() = showLoadMore()
        override fun isLastPage() = isLastPage
    }

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

    private fun updateFabStatus() {
        activity?.hideKeyboard()
        if (isLoading) return
        getViewDataBinding().fab.isExpanded = !getViewDataBinding().fab.isExpanded
        if (getViewDataBinding().fab.isExpanded) {
            filterFragment.onFilterExpanded()
            hideFab()
            hideBottomBar()
        } else {
            showBottomBar()
            showFab()
            filterFragment.onFilterCollapsed()
        }
    }

//    private fun toggleMenuVisibility(show: Boolean) {
//        resultOrderMenuItem?.isVisible = show
//    }

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
        FetchCurrentLocationLiveData(
            requireContext()
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
                    val address = requireContext().getLocationAddress(location) ?: return@Observer
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

    companion object {
        const val TAG = "search_fragment"
    }
}