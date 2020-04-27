package com.droid47.petfriend.bookmark.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import com.droid47.petfriend.R
import com.droid47.petfriend.base.bindingConfig.EmptyScreenConfiguration
import com.droid47.petfriend.base.bindingConfig.ErrorViewConfiguration
import com.droid47.petfriend.base.extensions.activityViewModelProvider
import com.droid47.petfriend.base.extensions.getErrorRequestMessage
import com.droid47.petfriend.base.extensions.viewModelProvider
import com.droid47.petfriend.base.widgets.*
import com.droid47.petfriend.bookmark.presentation.ui.BookmarkFragmentDirections.Companion.toPetDetails
import com.droid47.petfriend.bookmark.presentation.viewmodel.BookmarkViewModel
import com.droid47.petfriend.databinding.FragmentBookMarkBinding
import com.droid47.petfriend.home.presentation.HomeActivity
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.presentation.widgets.PagedListPetAdapter
import com.droid47.petfriend.search.presentation.widgets.PetAdapter.Companion.BOOK_MARK
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class BookmarkFragment :
    BaseBindingFragment<FragmentBookMarkBinding, BookmarkViewModel, HomeViewModel>(),
    View.OnClickListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val emptyState = EmptyScreenConfiguration()
    private val errorState = ErrorViewConfiguration()

    private val bookmarkViewModel: BookmarkViewModel by lazy {
        viewModelProvider<BookmarkViewModel>(factory)
    }

    private val homeViewModel: HomeViewModel by lazy {
        requireActivity().activityViewModelProvider<HomeViewModel>()
    }

    override fun getLayoutId(): Int = R.layout.fragment_book_mark

    override fun getViewModel(): BookmarkViewModel = bookmarkViewModel

    override fun getParentViewModel(): HomeViewModel = homeViewModel

    override fun getFragmentNavId(): Int = R.id.navigation_favorite

    override fun injectSubComponent() {
        (activity as HomeActivity).homeComponent.inject(this@BookmarkFragment)
    }

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.lifecycleOwner = viewLifecycleOwner
            it.bookmarkViewModel = getViewModel()
            it.emptyStateConfig = emptyState
            it.errorStateConfig = errorState
        }
    }

    override fun getSnackBarAnchorView(): View =
        if (getViewDataBinding().fab.visibility == View.GONE)
            getViewDataBinding().bottomAppBar
        else
            getViewDataBinding().fab

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
        setUpBookmarkRvAdapter()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscribeToLiveData()
    }

    private fun setUpView() {
        getViewDataBinding().fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setImageResource(R.drawable.vc_delete_sweep)
            setOnClickListener(this@BookmarkFragment)
        }

        getViewDataBinding().bottomAppBar.apply {
            setNavigationIcon(R.drawable.vc_nav_menu)
            replaceMenu(R.menu.search_menu)
            menu.findItem(R.id.menu_order).isVisible = false
            setNavigationOnClickListener {
                getParentViewModel().eventLiveData.postValue(HomeViewModel.EVENT_TOGGLE_NAVIGATION)
            }
        }
        getViewDataBinding().scrim.setOnClickListener(this@BookmarkFragment)
        getViewDataBinding().layoutDeletePet.btnSecondaryAction.setOnClickListener(this@BookmarkFragment)
        getViewDataBinding().layoutDeletePet.btnPrimaryAction.setOnClickListener(this@BookmarkFragment)
    }

    private fun setUpBookmarkRvAdapter() {
        if (getPetAdapter() == null) {
            getViewDataBinding().rvPets.adapter =
                PagedListPetAdapter(requireContext(), BOOK_MARK, getViewModel())
        }
    }

    private fun getPetAdapter() = getViewDataBinding().rvPets.adapter as? PagedListPetAdapter

    private fun subscribeToLiveData() {
        getViewModel().bookmarkListLiveData.run {
            removeObserver(bookmarkDataObserver)
            observe(viewLifecycleOwner, bookmarkDataObserver)
        }

        getViewModel().navigateToAnimalDetailsAction.run {
            removeObserver(navigationObserver)
            observe(viewLifecycleOwner, navigationObserver)
        }

        getViewModel().undoBookmarkLiveData.run {
            removeObserver(bookmarkStatusObserver)
            observe(viewLifecycleOwner, bookmarkStatusObserver)
        }
    }

    private val bookmarkDataObserver = Observer<BaseStateModel<out PagedList<PetEntity>>> {
        val baseStateModel = it ?: return@Observer
        when (baseStateModel) {
            is Loading  -> {
                hideBottomBar()
                hideFab()
            }

            is Success -> {
                getPetAdapter()?.submitList(baseStateModel.data) {
                    showBottomBar()
                    showFab()
                }
            }

            is Empty -> {
                getPetAdapter()?.submitList(baseStateModel.data)
                hideFab()
                updateEmptyState()
            }

            is Failure -> {
                hideFab()
                updateErrorState(baseStateModel.error)
            }
        }
    }


    private val navigationObserver = Observer<Pair<PetEntity, View>> {
        val petViewPair = it ?: return@Observer
        if (findNavController().currentDestination?.id != R.id.navigation_pet_details) {
            val extras = FragmentNavigatorExtras(
                petViewPair.second to petViewPair.second.transitionName
            )
            findNavController().navigate(toPetDetails(petViewPair.first.id), extras)
        }
    }

    private val bookmarkStatusObserver = Observer<BaseStateModel<PetEntity>> {
        val baseStateModel = it ?: return@Observer
        when {
            baseStateModel is Success && !baseStateModel.data.bookmarkStatus ->
                showBookmarkUndoSnackBar(baseStateModel.data)

            baseStateModel is Failure -> showErrorSnackBar(baseStateModel.error)
        }
    }

    private fun updateEmptyState() {
        emptyState.apply {
            this.emptyScreenDrawable = ContextCompat.getDrawable(
                requireContext(), R.drawable.ic_launcher_background
            )
            this.emptyScreenTitleText = getString(R.string.i_am_alone)
            this.emptyScreenSubTitleText = getString(R.string.please_star)
            this.emptyScreenBtnText = getString(R.string.go_to_search)
            this.btnClickListener = View.OnClickListener {
                findNavController().navigateUp()
            }
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
        }
    }

    private fun showErrorSnackBar(throwable: Throwable) {
        val msg = throwable.getErrorRequestMessage(requireContext())
        Snackbar.make(getViewDataBinding().bottomAppBar, msg.first, Snackbar.LENGTH_LONG)
            .setAnchorView(getSnackBarAnchorId())
            .show()
    }

    private fun showBookmarkUndoSnackBar(petEntity: PetEntity) {
        val msg = getString(R.string.remove_bookmark)
        Snackbar.make(getViewDataBinding().bottomAppBar, msg, Snackbar.LENGTH_LONG)
            .setAnchorView(getSnackBarAnchorId())
            .setAction(getString(R.string.undo)) {
                getViewModel().onBookMarkClick(petEntity.apply {
                    bookmarkStatus = !petEntity.bookmarkStatus
                })
            }.show()
    }

    private fun getSnackBarAnchorId(): Int =
        if (getViewDataBinding().fab.visibility == View.VISIBLE)
            getViewDataBinding().fab.id
        else
            getViewDataBinding().bottomAppBar.id

    override fun onClick(view: View?) {
        when (view?.id ?: return) {
            R.id.btn_secondary_action,
            R.id.scrim,
            R.id.fab -> {
                toggleDeletePetView()
            }
            R.id.btn_primary_action -> {
                getViewModel().deleteAllFavoritePets()
                toggleDeletePetView()
            }
        }
    }

    private fun hideFab() {
        getViewDataBinding().fab.hide()
    }

    private fun showFab() {
        getViewDataBinding().fab.show()
    }

    private fun showBottomBar() {
        getViewDataBinding().bottomAppBar.performShow()
    }

    private fun hideBottomBar() {
        getViewDataBinding().bottomAppBar.performHide()
    }

    private fun toggleDeletePetView() {
        getViewDataBinding().fab.isExpanded = !getViewDataBinding().fab.isExpanded
        if (getViewDataBinding().fab.isExpanded) {
            hideFab()
            hideBottomBar()
        } else {
            showBottomBar()
            showFab()
        }
    }
}