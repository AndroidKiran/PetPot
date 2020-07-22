package com.droid47.petfriend.search.presentation.ui

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
import com.droid47.petfriend.base.widgets.anim.SpringAddItemAnimator
import com.droid47.petfriend.databinding.FragmentFilterBinding
import com.droid47.petfriend.home.presentation.ui.HomeActivity
import com.droid47.petfriend.search.data.models.PetFilterCheckableEntity
import com.droid47.petfriend.search.presentation.ui.widgets.FilterAdapter
import com.droid47.petfriend.search.presentation.ui.widgets.SortPopupMenu
import com.droid47.petfriend.search.presentation.viewmodel.FilterViewModel
import com.droid47.petfriend.search.presentation.viewmodel.FilterViewModel.Companion.EVENT_APPLY_FILTER
import com.droid47.petfriend.search.presentation.viewmodel.FilterViewModel.Companion.EVENT_APPLY_SORT_FILTER
import com.droid47.petfriend.search.presentation.viewmodel.FilterViewModel.Companion.EVENT_CLOSE_FILTER
import com.droid47.petfriend.search.presentation.viewmodel.SearchViewModel
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
            R.attr.bottomSheetStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                requireContext().themeColor(R.attr.colorPrimarySurface)
            )
            elevation = requireContext().resources.getDimension(R.dimen.plane_16)
            shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
            initializeElevationOverlay(requireContext())
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
        val context = context ?: return
        sortPopMenu =
            SortPopupMenu(context, view, getViewModel(), viewLifecycleOwner).also {
                it.showPopup()
            }
    }

    private fun dismissSortPopUp() {
        sortPopMenu?.dismiss()
    }

    private fun setUpViews() {
        val context = context ?: return
        getViewDataBinding().cdlFilter.background = backGroundPrimaryColorDrawable
        with(getViewDataBinding().navigationMenuView) {
            itemBackground = navigationItemBackground(context)
            setCheckedItem(R.id.menu_filter_gender)
            setNavigationItemSelectedListener(
                navigationItemListener
            )
        }

        with(getViewDataBinding().rvFilters) {
            if (getCategoryAdapter() != null) return@with
            itemAnimator = SpringAddItemAnimator(SpringAddItemAnimator.Direction.DirectionY)
            layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW).apply {
                justifyContent = JustifyContent.CENTER
                flexWrap = FlexWrap.WRAP
                alignItems = AlignItems.STRETCH
            }
            adapter = FilterAdapter(FilterAdapter.CATEGORY_FILTER, getViewModel().onItemCheck)
        }

        with(getViewDataBinding().rvSelectedFilter) {
            if (getSelectFilterAdapter() != null) return@with
            itemAnimator = SpringAddItemAnimator(SpringAddItemAnimator.Direction.DirectionX)
            setHasFixedSize(true)
            adapter = FilterAdapter(FilterAdapter.SELECTED_FILTER, getViewModel().onItemCheck)
        }

        with(getViewDataBinding().bottomFilterBar) {
            setNavigationIcon(R.drawable.vc_close)
            setNavigationOnClickListener {
                getViewModel().closeFilter(true)
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

    private fun getCategoryAdapter(): FilterAdapter? =
        getViewDataBinding().rvFilters.adapter as? FilterAdapter

    private fun getSelectFilterAdapter(): FilterAdapter? =
        getViewDataBinding().rvSelectedFilter.adapter as? FilterAdapter

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
        val context = context ?: return
        emptyState.apply {
            this.emptyScreenDrawable = ContextCompat.getDrawable(
                context, R.drawable.ic_launcher_background
            )
            this.emptyScreenTitleText = getString(R.string.no_items_found)
        }
    }

    private fun updateErrorState(throwable: Throwable) {
        val context = context ?: return
        val errorPair = throwable.getErrorRequestMessage(context)
        errorState.apply {
            this.errorScreenDrawable = ContextCompat.getDrawable(
                context, R.drawable.ic_launcher_background
            )
            this.errorScreenText = errorPair.first
            this.errorScreenTextSubTitle = errorPair.second
            this.errorBtnText = getString(R.string.retry)
            this.errorRetryClickListener = View.OnClickListener {
                updateCategory()
            }
        }
    }

    private fun updateCategoryFilterData(list: List<PetFilterCheckableEntity>) {
        getCategoryAdapter()?.submitFilterList(list, true)
    }

    private fun updateSelectedFilterData(list: List<PetFilterCheckableEntity>) {
        getSelectFilterAdapter()?.submitFilterList(list, true)
    }

    private fun subscribeToLiveData() {
        getViewModel().filterListForSelectedCategoryLiveData.run {
            removeObserver(filterListObserver)
            observe(viewLifecycleOwner, filterListObserver)
        }

        getViewModel().selectedFilterListLiveData.run {
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

        getViewModel().appliedFilterLiveData.run {
            removeObserver(lastAppliedFilterObserver)
            observe(viewLifecycleOwner, lastAppliedFilterObserver)
        }
    }

    private val filterListObserver = Observer<BaseStateModel<List<PetFilterCheckableEntity>>> {
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

    private val selectedFilterListObserver =
        Observer<BaseStateModel<List<PetFilterCheckableEntity>>> {
            val stateModel = it ?: return@Observer
            menuRefreshItem?.isVisible = stateModel is Success
            when (stateModel) {
                is Success -> {
                    updateSelectedFilterData(stateModel.data)
                }
            }
        }

    private val eventObserver = Observer<Long> {
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

    private val lastAppliedFilterObserver =
        Observer<BaseStateModel<List<PetFilterCheckableEntity>>> {
            val stateModel = it ?: return@Observer
            if (stateModel is Success) {
                getParentViewModel().appliedFilterItemsLiveEvent.postValue(stateModel.data)
            } else {
                getParentViewModel().appliedFilterItemsLiveEvent.postValue(emptyList())
            }
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
            getViewModel().closeFilter(true)
        }
    }
}