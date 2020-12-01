package com.droid47.petpot.petDetails.presentation.ui

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.droid47.petpot.R
import com.droid47.petpot.base.extensions.activityViewModelProvider
import com.droid47.petpot.base.extensions.gone
import com.droid47.petpot.base.extensions.viewModelProvider
import com.droid47.petpot.base.extensions.visible
import com.droid47.petpot.base.firebase.AnalyticsScreens
import com.droid47.petpot.base.widgets.BaseBindingFragment
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Empty
import com.droid47.petpot.base.widgets.components.GravitySnapHelper
import com.droid47.petpot.base.widgets.snappy.SnapType
import com.droid47.petpot.base.widgets.snappy.SnappyLinearLayoutManager
import com.droid47.petpot.databinding.FragmentSimilarPetsBinding
import com.droid47.petpot.home.presentation.ui.HomeActivity
import com.droid47.petpot.home.presentation.viewmodels.HomeViewModel
import com.droid47.petpot.petDetails.presentation.viewmodels.PetDetailsViewModel
import com.droid47.petpot.search.data.models.search.PetEntity
import com.droid47.petpot.search.presentation.ui.widgets.PagedListPetAdapter
import javax.inject.Inject

class SimilarPetsFragment :
    BaseBindingFragment<FragmentSimilarPetsBinding, PetDetailsViewModel, HomeViewModel>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val petDetailsViewModel: PetDetailsViewModel by lazy(LazyThreadSafetyMode.NONE) {
        viewModelProvider<PetDetailsViewModel>(factory)
    }

    private val homeViewModel: HomeViewModel by lazy(LazyThreadSafetyMode.NONE) {
        requireActivity().activityViewModelProvider<HomeViewModel>()
    }

    private val pagedListPetAdapter: PagedListPetAdapter by lazy(LazyThreadSafetyMode.NONE) {
        PagedListPetAdapter(
            requireContext(),
            PagedListPetAdapter.AdapterType.Similar,
            getViewModel()
        )
    }

    override fun getLayoutId(): Int = R.layout.fragment_similar_pets

    override fun getFragmentNavId(): Int = R.id.similar_pets

    override fun executePendingVariablesBinding() {
        getViewDataBinding().executePendingBindings()
    }

    override fun getViewModel(): PetDetailsViewModel = petDetailsViewModel

    override fun getParentViewModel(): HomeViewModel = homeViewModel

    override fun getSnackBarAnchorView(): View = getViewDataBinding().clAlsoLike

    override fun injectSubComponent() {
        (activity as HomeActivity).homeComponent.inject(this)
    }

    override fun getClassName(): String = SimilarPetsFragment::class.java.simpleName

    override fun getScreenName(): String = AnalyticsScreens.SIMILAR_PETS_SCREEN

    override fun onAttach(context: Context) {
        super.onAttach(context)
        subscribeToLiveData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSimilarRvAdapter()
    }

    fun subscribeToSubListSelection(selectionObserver: Observer<Pair<PetEntity, View>>) {
        getViewModel().navigateToAnimalDetailsAction.run {
            removeObserver(selectionObserver)
            observe(viewLifecycleOwner, selectionObserver)
        }
    }

    private fun setUpSimilarRvAdapter() {
        with(getViewDataBinding().rvAlsoLikeList) {
            GravitySnapHelper(Gravity.START).attachToRecyclerView(this)
            layoutManager = SnappyLinearLayoutManager(
                requireContext(),
                RecyclerView.HORIZONTAL,
                false
            ).apply {
                setSnapType(SnapType.START)
                setSnapInterpolator(DecelerateInterpolator())
                setSeekDuration(300)
                initialPrefetchItemCount = 14
                setHasFixedSize(true)
                setItemViewCacheSize(14)
            }
            adapter = pagedListPetAdapter
        }
    }

    private fun getPetAdapter() =
        getViewDataBinding().rvAlsoLikeList.adapter as? PagedListPetAdapter

    private fun subscribeToLiveData() {
        getViewModel().petsLiveData.run {
            removeObserver(similarPetListObserver)
            observe(requireActivity(), similarPetListObserver)
        }
    }

    private val similarPetListObserver = Observer<BaseStateModel<out PagedList<PetEntity>>> {
        val stateModel = it ?: return@Observer
        if (stateModel is Empty) {
            getViewDataBinding().clAlsoLike.gone()
        } else {
            getPetAdapter()?.submitList(stateModel.data)
            getViewDataBinding().clAlsoLike.visible()
        }
    }
}