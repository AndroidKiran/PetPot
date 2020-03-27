package com.droid47.petgoogle.petDetails.presentation

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.droid47.petgoogle.R
import com.droid47.petgoogle.base.extensions.activityViewModelProvider
import com.droid47.petgoogle.base.extensions.gone
import com.droid47.petgoogle.base.extensions.viewModelProvider
import com.droid47.petgoogle.base.extensions.visible
import com.droid47.petgoogle.base.widgets.BaseBindingFragment
import com.droid47.petgoogle.base.widgets.components.GravitySnapHelper
import com.droid47.petgoogle.base.widgets.snappy.SnapType
import com.droid47.petgoogle.base.widgets.snappy.SnappyLinearLayoutManager
import com.droid47.petgoogle.databinding.FragmentSimilarPetsBinding
import com.droid47.petgoogle.home.presentation.HomeActivity
import com.droid47.petgoogle.home.presentation.viewmodels.HomeViewModel
import com.droid47.petgoogle.petDetails.presentation.viewmodels.PetDetailsViewModel
import com.droid47.petgoogle.search.data.models.search.PetEntity
import com.droid47.petgoogle.search.presentation.widgets.PetAdapter
import com.droid47.petgoogle.search.presentation.widgets.PetAdapter.Companion.SIMILAR
import javax.inject.Inject

class SimilarPetsFragment :
    BaseBindingFragment<FragmentSimilarPetsBinding, PetDetailsViewModel, HomeViewModel>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val petDetailsViewModel: PetDetailsViewModel by lazy {
        viewModelProvider<PetDetailsViewModel>(factory)
    }

    private val homeViewModel: HomeViewModel by lazy {
        activityViewModelProvider<HomeViewModel>(requireActivity())
    }

    override fun getLayoutId(): Int = R.layout.fragment_similar_pets

    override fun getFragmentNavId(): Int = R.id.similar_pets

    override fun executePendingVariablesBinding() {
        getViewDataBinding().executePendingBindings()
    }

    override fun getViewModel(): PetDetailsViewModel = petDetailsViewModel

    override fun getParentViewModel(): HomeViewModel = homeViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
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
        if (getPetAdapter() == null) {
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
                adapter = PetAdapter(requireContext(), SIMILAR, getViewModel())
            }
        }
    }

    private fun getPetAdapter() = getViewDataBinding().rvAlsoLikeList.adapter as? PetAdapter

    private fun subscribeToLiveData() {
        getParentViewModel().similarPetList.run {
            removeObserver(similarPetListObserver)
            observe(viewLifecycleOwner, similarPetListObserver)
        }
    }

    private val similarPetListObserver = Observer<List<PetEntity>> {
        val similarPetList = it ?: emptyList()
        if (similarPetList.isEmpty()) {
            getViewDataBinding().clAlsoLike.gone()
        } else {
            getPetAdapter()?.submitList(similarPetList)
            getViewDataBinding().clAlsoLike.visible()
            getViewDataBinding().rvAlsoLikeList.smoothScrollToPosition(0)
        }
    }
}