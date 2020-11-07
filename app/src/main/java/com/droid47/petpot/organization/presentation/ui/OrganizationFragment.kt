package com.droid47.petpot.organization.presentation.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import com.droid47.petpot.R
import com.droid47.petpot.base.extensions.*
import com.droid47.petpot.base.firebase.AnalyticsScreens
import com.droid47.petpot.base.widgets.BaseBindingFragment
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.anim.SpringAddItemAnimator
import com.droid47.petpot.base.widgets.centerzoom.CenterScrollListener
import com.droid47.petpot.base.widgets.centerzoom.ScrollZoomLayoutManager
import com.droid47.petpot.databinding.FragmentOrganisationBinding
import com.droid47.petpot.home.presentation.ui.HomeActivity
import com.droid47.petpot.home.presentation.viewmodels.HomeViewModel
import com.droid47.petpot.organization.data.models.OrganizationCheckableEntity
import com.droid47.petpot.organization.data.models.OrganizationFilterConstants
import com.droid47.petpot.organization.presentation.ui.adapter.PagedListOrganizationAdapter
import com.droid47.petpot.organization.presentation.viewmodel.OrganizationViewModel
import com.droid47.petpot.organization.presentation.viewmodel.OrganizationViewModel.Companion.EVENT_MAP_READY
import com.droid47.petpot.search.presentation.models.ItemPaginationState
import com.droid47.petpot.search.presentation.models.LoadingState
import com.droid47.petpot.search.presentation.ui.widgets.FilterAdapter
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import javax.inject.Inject
import kotlin.math.ceil

class OrganizationFragment :
    BaseBindingFragment<FragmentOrganisationBinding, OrganizationViewModel, HomeViewModel>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private var menuRefreshItem: MenuItem? = null
    private val pagedListOrganizationAdapter: PagedListOrganizationAdapter by lazy(
        LazyThreadSafetyMode.NONE
    ) {
        PagedListOrganizationAdapter(
            getViewModel().onItemSelected
        )
    }

    private val mapFragment: OrganizationMap by lazy(LazyThreadSafetyMode.NONE) {
        childFragmentManager.findFragmentById(R.id.map) as OrganizationMap
    }

    private val organizationViewModel: OrganizationViewModel by lazy(LazyThreadSafetyMode.NONE) {
        viewModelProvider<OrganizationViewModel>(factory)
    }

    private val homeViewModel: HomeViewModel by lazy(LazyThreadSafetyMode.NONE) {
        requireActivity().activityViewModelProvider<HomeViewModel>()
    }

    override fun getLayoutId(): Int = R.layout.fragment_organisation

    override fun getFragmentNavId(): Int = R.id.navigation_organisation

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.lifecycleOwner = viewLifecycleOwner
            it.organizationViewModel = getViewModel()
        }
    }

    override fun getViewModel(): OrganizationViewModel = organizationViewModel

    override fun getParentViewModel(): HomeViewModel = homeViewModel

    override fun getSnackBarAnchorView(): View {
        return getViewDataBinding().cdlOrganisation
    }

    override fun injectSubComponent() {
        (activity as HomeActivity).homeComponent.inject(this@OrganizationFragment)
    }

    override fun getClassName(): String = OrganizationFragment::class.java.simpleName

    override fun getScreenName(): String = AnalyticsScreens.ORGANIZATIONS_SCREEN

    override fun onAttach(context: Context) {
        super.onAttach(context)
        subscribeToLiveData()
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
        setUpView()
    }

    private fun setUpView() {
        with(getViewDataBinding().bottomAppBar) {
            setNavigationIcon(R.drawable.vc_close)
            setNavigationOnClickListener {
                getViewModel().closeFilter()
            }
            replaceMenu(R.menu.filter_menu)
            setOnMenuItemClickListener(menuClickListener)
            menuRefreshItem = menu.findItem(R.id.menu_refresh_filter).also {
                it.isVisible = false
            }
        }

        with(getViewDataBinding().filterFab) {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setOnClickListener {
                getViewModel().applyFilter()
            }
        }

        with(getViewDataBinding().rvSelectedOrganizations) {
            if (getSelectFilterAdapter() != null) return@with
            setHasFixedSize(true)
            adapter = FilterAdapter(FilterAdapter.SELECTED_FILTER, getViewModel().onItemSelected)
        }

        with(getViewDataBinding().rvOrganization) {
            if (getOrganizationAdapter() != null) return@with
            setHasFixedSize(true)
            itemAnimator = SpringAddItemAnimator(SpringAddItemAnimator.Direction.DirectionX)
            this.layoutManager =
                ScrollZoomLayoutManager(
                    this.context,
                    this.context.dp2px(10f)
                )
            this.addOnScrollListener(CenterScrollListener())
            adapter = pagedListOrganizationAdapter
        }
    }

    private fun getOrganizationAdapter(): PagedListOrganizationAdapter? =
        getViewDataBinding().rvOrganization.adapter as? PagedListOrganizationAdapter

    private fun getSelectFilterAdapter(): FilterAdapter? =
        getViewDataBinding().rvSelectedOrganizations.adapter as? FilterAdapter

    private fun subscribeToLiveData() {

        getViewModel().inputQueryLiveData.run {
            removeObserver(inputQueryObserver)
            observe(requireActivity(), inputQueryObserver)
        }

        getViewModel().organizationsLiveData.run {
            removeObserver(organizationsObserver)
            observe(requireActivity(), organizationsObserver)
        }

        getViewModel().itemPaginationStateLiveData.run {
            removeObserver(itemPaginationStateObserver)
            observe(requireActivity(), itemPaginationStateObserver)
        }

        getViewModel().eventLiveData.run {
            removeObserver(eventObserver)
            observe(requireActivity(), eventObserver)
        }

        getViewModel().selectedOrganizationsLiveData.run {
            removeObserver(selectedOrganizationObserver)
            observe(requireActivity(), selectedOrganizationObserver)
        }
    }

    private val inputQueryObserver = Observer<Pair<String?, String?>> {
        val inputQueryPair = it ?: return@Observer
        mapFragment.clearItems()
    }

    private val organizationsObserver = Observer<PagedList<OrganizationCheckableEntity>> {
        val pagedList = it ?: return@Observer
        getOrganizationAdapter()?.submitList(pagedList) {
            val currentList =
                getOrganizationAdapter()?.currentList ?: emptyList<OrganizationCheckableEntity>()
            if (currentList.isNotEmpty()) {
                val pageNum =
                    ceil(pagedList.size.div(OrganizationFilterConstants.PAGE_SIZE.toDouble())).toInt()
                getViewModel().updatePage(pageNum)
                mapFragment.addOrganizationsToMap(currentList)
            }
        }
    }

    private val itemPaginationStateObserver = Observer<ItemPaginationState> {
        when (it ?: return@Observer) {
            is LoadingState -> mapFragment.clearItems()
        }
    }

    private val eventObserver = Observer<Long> {
        when (it ?: return@Observer) {
            HomeViewModel.EVENT_NAVIGATE_BACK -> {
                getViewDataBinding().etName.hideKeyboard()
                getParentViewModel().eventLiveData.postValue(it)
            }
            EVENT_MAP_READY -> getViewDataBinding().root.doOnPreDraw { startPostponedEnterTransition() }
        }
    }

    private val selectedOrganizationObserver =
        Observer<BaseStateModel<List<OrganizationCheckableEntity>>> {
            val state = it ?: return@Observer
            menuRefreshItem?.isVisible = state.data?.isNotEmpty() ?: false
            getSelectFilterAdapter()?.submitFilterList(state.data ?: emptyList(), false)
        }

    private val menuClickListener = Toolbar.OnMenuItemClickListener {
        when (it?.itemId ?: return@OnMenuItemClickListener false) {
            R.id.menu_refresh_filter -> getViewModel().clearFilter()
        }
        true
    }

    private fun prepareTransitions() {
        val context = context ?: return
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
            interpolator = context.themeInterpolator(R.attr.motionInterpolatorPersistent)
            setPathMotion(MaterialArcMotion())
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
            scrimColor = Color.TRANSPARENT
        }

        sharedElementReturnTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.btn_edit_org
            duration = resources.getInteger(R.integer.pet_motion_duration_small).toLong()
            interpolator = context.themeInterpolator(R.attr.motionInterpolatorPersistent)
            setPathMotion(MaterialArcMotion())
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
        }

        postponeEnterTransition()
    }
}