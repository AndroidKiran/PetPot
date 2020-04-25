package com.droid47.petfriend.search.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.droid47.petfriend.R
import com.droid47.petfriend.base.bindingConfig.EmptyScreenConfiguration
import com.droid47.petfriend.base.bindingConfig.ErrorViewConfiguration
import com.droid47.petfriend.base.extensions.*
import com.droid47.petfriend.base.widgets.*
import com.droid47.petfriend.databinding.FragmentFilterBinding
import com.droid47.petfriend.home.presentation.HomeActivity
import com.droid47.petfriend.search.data.models.FilterItemEntity
import com.droid47.petfriend.search.presentation.viewmodel.FilterViewModel
import com.droid47.petfriend.search.presentation.viewmodel.FilterViewModel.Companion.EVENT_APPLY_FILTER
import com.droid47.petfriend.search.presentation.viewmodel.FilterViewModel.Companion.EVENT_APPLY_SORT_FILTER
import com.droid47.petfriend.search.presentation.viewmodel.FilterViewModel.Companion.EVENT_CLOSE_FILTER
import com.droid47.petfriend.search.presentation.viewmodel.SearchViewModel
import com.droid47.petfriend.search.presentation.widgets.FilterAdapter
import com.droid47.petfriend.search.presentation.widgets.SortPopupMenu
import com.google.android.flexbox.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import java.util.*
import javax.inject.Inject

class FilterFragment :
    BaseBindingFragment<FragmentFilterBinding, FilterViewModel, SearchViewModel>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private var sortPopMenu: SortPopupMenu? = null
    private val emptyState = EmptyScreenConfiguration()
    private val errorState = ErrorViewConfiguration()
    private var menuRefreshItem: MenuItem? = null

    private val filterViewModel: FilterViewModel by lazy(LazyThreadSafetyMode.NONE) {
        viewModelProvider<FilterViewModel>(factory)
    }

    private val searchViewModel: SearchViewModel by lazy(LazyThreadSafetyMode.NONE) {
        requireParentFragment().parentFragmentViewModelProvider<SearchViewModel>()
    }

    private val backGroundPrimaryColorDrawable: MaterialShapeDrawable by lazy(LazyThreadSafetyMode.NONE) {
        MaterialShapeDrawable(
            requireContext(),
            null,
            R.attr.materialCardViewStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                requireContext().themeColor(R.attr.colorPrimarySurface)
            )
            elevation = requireContext().resources.getDimension(R.dimen.plane_08)
            shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
            initializeElevationOverlay(context)
            shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                .setTopRightCorner(
                    CornerFamily.ROUNDED,
                    requireContext().resources.getDimension(R.dimen.pet_small_component_corner_radius)
                )
                .setTopLeftCorner(
                    CornerFamily.ROUNDED,
                    requireContext().resources.getDimension(R.dimen.pet_small_component_corner_radius)
                )
                .setBottomRightCorner(
                    CornerFamily.ROUNDED,
                    requireContext().resources.getDimension(R.dimen.zero)
                )
                .setBottomLeftCorner(
                    CornerFamily.ROUNDED,
                    requireContext().resources.getDimension(R.dimen.zero)
                ).build()
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_filter

    override fun getFragmentNavId(): Int = R.id.navigation_filter

    override fun getSnackBarAnchorView(): View = getViewDataBinding().filterFab

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.lifecycleOwner = viewLifecycleOwner
            it.newFilterViewModel = getViewModel()
            it.emptyStateConfig = emptyState
            it.errorStateConfig = errorState
        }
    }

    override fun getViewModel(): FilterViewModel = filterViewModel

    override fun getParentViewModel(): SearchViewModel = searchViewModel

    override fun injectSubComponent() {
        (activity as HomeActivity).homeComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, closeOnBackPressed)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscribeToLiveData()
    }

    fun onFilterExpanded() {
        closeOnBackPressed.isEnabled = true
        getViewModel().onFilterActive()
        showBottomBar()
        showFab()
        updateCategory()
    }

    fun onFilterCollapsed() {
        hideFab()
        hideBottomBar()
        closeOnBackPressed.isEnabled = false
    }

    fun showSortPopup(view: View) {
        sortPopMenu =
            SortPopupMenu(requireContext(), view, getViewModel(), viewLifecycleOwner).also {
                it.showPopup()
            }
    }

    private fun dismissSortPopUp() {
        sortPopMenu?.dismiss()
    }

    private fun setUpViews() {
        getViewDataBinding().cdlFilter.background = backGroundPrimaryColorDrawable
        getViewDataBinding().navigationMenuView.apply {
            itemBackground = navigationItemBackground(context)
            setCheckedItem(R.id.menu_filter_gender)
            setNavigationItemSelectedListener(
                navigationItemListener
            )
        }

        with(getViewDataBinding().rvFilters) {
            layoutManager = FlexboxLayoutManager(requireContext(), FlexDirection.ROW).apply {
                justifyContent = JustifyContent.CENTER
                flexWrap = FlexWrap.WRAP
                alignItems = AlignItems.STRETCH
            }
            adapter = FilterAdapter(FilterAdapter.CATEGORY_FILTER, getViewModel())
        }

        with(getViewDataBinding().rvSelectedFilter) {
            setHasFixedSize(true)
            adapter = FilterAdapter(FilterAdapter.ALL_FILTER, getViewModel())
        }

        with(getViewDataBinding().bottomFilterBar) {
            setNavigationIcon(R.drawable.vc_close)
            setNavigationOnClickListener {
                getViewModel().closeFilter()
            }
            replaceMenu(R.menu.filter_menu)
            setOnMenuItemClickListener(menuClickListener)
            menuRefreshItem = menu.findItem(R.id.menu_refresh_filter)
        }

        with(getViewDataBinding().filterFab) {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
        }

        getViewDataBinding().navigationMenuView.menu.children
            .map { menuItem -> menuItem.title.toString().toLowerCase(Locale.US) }
            .toList().let { getViewModel().menuItemListLiveData.postValue(it) }
    }

    private val navigationItemListener =
        NavigationView.OnNavigationItemSelectedListener { menuItem ->
            getViewDataBinding().navigationMenuView.setCheckedItem(menuItem)
            updateCategory()
            return@OnNavigationItemSelectedListener false
        }

    private fun updateCategory() {
        getViewDataBinding().cvSearch.hideKeyboard()
        val checkedItem = getViewDataBinding().navigationMenuView.checkedItem ?: return
        getViewModel().categoryLiveData.postValue(
            checkedItem.title?.toString()?.toLowerCase(Locale.US) ?: ""
        )
    }

    private fun updateEmptyState() {
        emptyState.apply {
            this.emptyScreenDrawable = ContextCompat.getDrawable(
                requireContext(), R.drawable.ic_launcher_background
            )
            this.emptyScreenTitleText = getString(R.string.no_items_found)
        }
    }

    private fun updateErrorState(throwable: Throwable) {
        val errorPair = throwable.getErrorRequestMessage(requireContext())
        errorState.apply {
            this.errorScreenDrawable = ContextCompat.getDrawable(
                requireContext(), R.drawable.ic_launcher_background
            )
            this.errorScreenText = errorPair.first
            this.errorScreenTextSubTitle = errorPair.second
            this.errorBtnText = getString(R.string.retry)
            this.errorRetryClickListener = View.OnClickListener {
                updateCategory()
            }
        }
    }

    private fun updateCategoryFilterData(list: List<FilterItemEntity>) {
        val adapter = getViewDataBinding().rvFilters.adapter as? FilterAdapter ?: return
        adapter.submitFilterList(list, true)
    }

    private fun updateSelectedFilterData(list: List<FilterItemEntity>) {
        val adapter = getViewDataBinding().rvSelectedFilter.adapter as? FilterAdapter ?: return
        adapter.submitFilterList(list, true)
    }

    private fun subscribeToLiveData() {
        getViewModel().filterListForSelectedCategoryLiveData.run {
            removeObserver(filterListObserver)
            observe(viewLifecycleOwner, filterListObserver)
        }

        getViewModel().allSelectedFilterListLiveData.run {
            removeObserver(selectedFilterListObserver)
            observe(viewLifecycleOwner, selectedFilterListObserver)
        }

        getViewModel().searchFilterLiveData.run {
            removeObserver(searchFilterObserver)
            observe(viewLifecycleOwner, searchFilterObserver)
        }

        getViewModel().eventLiveData.run {
            removeObserver(eventObserver)
            observe(viewLifecycleOwner, eventObserver)
        }
    }

    private val filterListObserver = Observer<BaseStateModel<List<FilterItemEntity>>> {
        val stateModel = it ?: return@Observer
        when (stateModel) {
            is Success -> {
                updateCategoryFilterData(stateModel.data)
            }

            is Empty -> {
                updateEmptyState()
            }

            is Failure -> {
                updateErrorState(stateModel.error)
            }
        }
        getViewModel().clearSearch()
    }

    private val selectedFilterListObserver = Observer<BaseStateModel<List<FilterItemEntity>>> {
        val stateModel = it ?: return@Observer
        menuRefreshItem?.isVisible = stateModel is Success
        when (stateModel) {
            is Success -> {
                updateSelectedFilterData(stateModel.data)
            }
        }
    }

    private val eventObserver = Observer<Int> {
        when (it ?: return@Observer) {
            EVENT_APPLY_FILTER,
            EVENT_CLOSE_FILTER -> getParentViewModel().eventLiveData.postValue(it)
            EVENT_APPLY_SORT_FILTER -> dismissSortPopUp()
            else -> throw IllegalStateException("Invalid event")
        }
    }

    private val searchFilterObserver = Observer<String> { query ->
        val adapter = getViewDataBinding().rvFilters.adapter as? FilterAdapter ?: return@Observer
        adapter.filter.filter(query)
    }

    private fun showFab() {
        getViewDataBinding().filterFab.postDelayed({
            getViewDataBinding().filterFab.show()
        }, 400)
    }

    private fun hideFab() {
        getViewDataBinding().filterFab.hide()
    }

    private fun showBottomBar() {
        getViewDataBinding().bottomFilterBar.apply {
            visibility = View.VISIBLE
            performShow()
        }
    }

    private fun hideBottomBar() {
        getViewDataBinding().bottomFilterBar.apply {
            performHide()
            visibility = View.GONE
        }
    }

    private val menuClickListener = Toolbar.OnMenuItemClickListener {
        when (it?.itemId ?: return@OnMenuItemClickListener false) {
            R.id.menu_refresh_filter -> {
                getViewModel().resetFilter()
            }
        }
        true
    }

    private val closeOnBackPressed = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            getViewModel().closeFilter()
        }
    }
}