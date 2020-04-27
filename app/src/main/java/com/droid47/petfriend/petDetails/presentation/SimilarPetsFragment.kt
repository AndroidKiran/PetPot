package com.droid47.petfriend.petDetails.presentation

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.activityViewModelProvider
import com.droid47.petfriend.base.extensions.gone
import com.droid47.petfriend.base.extensions.viewModelProvider
import com.droid47.petfriend.base.extensions.visible
import com.droid47.petfriend.base.widgets.BaseBindingFragment
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Empty
import com.droid47.petfriend.base.widgets.components.GravitySnapHelper
import com.droid47.petfriend.base.widgets.snappy.SnapType
import com.droid47.petfriend.base.widgets.snappy.SnappyLinearLayoutManager
import com.droid47.petfriend.databinding.FragmentSimilarPetsBinding
import com.droid47.petfriend.home.presentation.HomeActivity
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel
import com.droid47.petfriend.petDetails.presentation.viewmodels.PetDetailsViewModel
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.presentation.widgets.PagedListPetAdapter
import com.droid47.petfriend.search.presentation.widgets.PetAdapter
import com.droid47.petfriend.search.presentation.widgets.PetAdapter.Companion.SIMILAR
import javax.inject.Inject

class SimilarPetsFragment :
    BaseBindingFragment<FragmentSimilarPetsBinding, PetDetailsViewModel, HomeViewModel>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val petDetailsViewModel: PetDetailsViewModel by lazy {
        viewModelProvider<PetDetailsViewModel>(factory)
    }

    private val homeViewModel: HomeViewModel by lazy {
        requireActivity().activityViewModelProvider<HomeViewModel>()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSimilarRvAdapter()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscribeToLiveData()
    }

    fun subscribeToSubListSelection(selectionObserver: Observer<Pair<PetEntity, View>>) {
        getViewModel().navigateToAnimalDetailsAction.run {
            removeObserver(selectionObserver)
            observe(viewLifecycleOwner, selectionObserver)
        }
    }

    private fun setUpSimilarRvAdapter() {
        if (getPetAdapter() != null) return
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
            }
            adapter = PagedListPetAdapter(requireContext(), SIMILAR, getViewModel())
        }

    }

    private fun getPetAdapter() =
        getViewDataBinding().rvAlsoLikeList.adapter as? PagedListPetAdapter

    private fun subscribeToLiveData() {
        getViewModel().petsLiveData.run {
            removeObserver(similarPetListObserver)
            observe(viewLifecycleOwner, similarPetListObserver)
        }
    }

    private val similarPetListObserver = Observer<BaseStateModel<out PagedList<PetEntity>>> {
        val stateModel = it ?: return@Observer
        if (stateModel is Empty) {
            getViewDataBinding().clAlsoLike.gone()
        } else {
            getPetAdapter()?.submitList(stateModel.data)
            getViewDataBinding().clAlsoLike.visible()
            getViewDataBinding().rvAlsoLikeList.smoothScrollToPosition(0)
        }
    }
}