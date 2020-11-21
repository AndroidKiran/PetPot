package com.droid47.petpot.search.presentation.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import com.droid47.petpot.R
import com.droid47.petpot.base.bindingConfig.EmptyScreenConfiguration
import com.droid47.petpot.base.bindingConfig.ErrorViewConfiguration
import com.droid47.petpot.base.extensions.*
import com.droid47.petpot.base.firebase.AnalyticsScreens
import com.droid47.petpot.base.widgets.*
import com.droid47.petpot.base.widgets.anim.SpringAddItemAnimator
import com.droid47.petpot.databinding.FragmentBookMarkBinding
import com.droid47.petpot.home.presentation.ui.HomeActivity
import com.droid47.petpot.home.presentation.viewmodels.HomeViewModel
import com.droid47.petpot.search.data.models.search.PetEntity
import com.droid47.petpot.search.presentation.ui.widgets.PagedListPetAdapter
import com.droid47.petpot.search.presentation.viewmodel.BookmarkViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import javax.inject.Inject

class BookmarkFragment :
    BaseBindingFragment<FragmentBookMarkBinding, BookmarkViewModel, HomeViewModel>(),
    View.OnClickListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val emptyState = EmptyScreenConfiguration()
    private val errorState = ErrorViewConfiguration()

    private val springAddItemAnimator: SpringAddItemAnimator by lazy(LazyThreadSafetyMode.NONE) {
        SpringAddItemAnimator(SpringAddItemAnimator.Direction.DirectionY)
    }

    private val bookmarkViewModel: BookmarkViewModel by lazy(LazyThreadSafetyMode.NONE) {
        viewModelProvider<BookmarkViewModel>(factory)
    }

    private val homeViewModel: HomeViewModel by lazy(LazyThreadSafetyMode.NONE) {
        requireActivity().activityViewModelProvider<HomeViewModel>()
    }

    private val pagedListPetAdapter: PagedListPetAdapter by lazy(LazyThreadSafetyMode.NONE) {
        PagedListPetAdapter(
            requireContext(),
            PagedListPetAdapter.AdapterType.Favorite,
            getViewModel()
        )
    }

    private val backGroundPrimaryColorDrawable: MaterialShapeDrawable by lazy(LazyThreadSafetyMode.NONE) {
        MaterialShapeDrawable(
            requireContext(),
            null,
            R.attr.bottomSheetStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                requireContext().themeColor(R.attr.colorSurface)
            )
            elevation = requireContext().resources.getDimension(R.dimen.plane_16)
            shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
            initializeElevationOverlay(requireContext())
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_book_mark

    override fun getViewModel(): BookmarkViewModel = bookmarkViewModel

    override fun getParentViewModel(): HomeViewModel = homeViewModel

    override fun getFragmentNavId(): Int = R.id.navigation_favorite

    override fun injectSubComponent() {
        (activity as HomeActivity).homeComponent.inject(this@BookmarkFragment)
    }

    override fun getClassName(): String = BookmarkFragment::class.java.simpleName

    override fun getScreenName(): String = AnalyticsScreens.FAVOURITE_PETS_SCREEN

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        subscribeToLiveData()
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        initTransition()
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
        setUpView()
    }

    override fun onResume() {
        super.onResume()
        trackFragment(getViewModel().firebaseManager)
    }

    override fun onStop() {
        springAddItemAnimator.endAnimations()
        super.onStop()
    }

    private fun initTransition() {
        enterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
        }

        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.pet_motion_duration_small).toLong()
        }

        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
        }
    }

    private fun setUpView() {
        getViewDataBinding().layoutDeletePet.root.background = backGroundPrimaryColorDrawable
        with(getViewDataBinding().fab) {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setImageResource(R.drawable.vc_delete_sweep)
            setOnClickListener(this@BookmarkFragment)
        }

        with(getViewDataBinding().bottomAppBar) {
            replaceMenu(R.menu.search_menu)
            menu.findItem(R.id.menu_order).isVisible = false
            setNavigationOnClickListener {
                getParentViewModel().eventLiveData.postValue(HomeViewModel.EVENT_TOGGLE_NAVIGATION)
            }
        }

        getViewDataBinding().btnNavSearch.setOnClickListener {
            getParentViewModel().eventLiveData.postValue(HomeViewModel.EVENT_TOGGLE_NAVIGATION)
        }
        getViewDataBinding().scrim.setOnClickListener(this@BookmarkFragment)
        getViewDataBinding().layoutDeletePet.btnSecondaryAction.setOnClickListener(this@BookmarkFragment)
        getViewDataBinding().layoutDeletePet.btnPrimaryAction.setOnClickListener(this@BookmarkFragment)

        with(getViewDataBinding().rvPets) {
            if (getPetAdapter() != null) return@with
            itemAnimator = springAddItemAnimator
            getViewDataBinding().rvPets.adapter = pagedListPetAdapter
        }
    }

    private fun getPetAdapter() = getViewDataBinding().rvPets.adapter as? PagedListPetAdapter

    private fun subscribeToLiveData() {
        getViewModel().bookmarkListLiveData.run {
            removeObserver(bookmarkDataObserver)
            observe(requireActivity(), bookmarkDataObserver)
        }

        getViewModel().navigateToAnimalDetailsAction.run {
            removeObserver(navigationObserver)
            observe(requireActivity(), navigationObserver)
        }

        getViewModel().undoBookmarkLiveData.run {
            removeObserver(bookmarkStatusObserver)
            observe(requireActivity(), bookmarkStatusObserver)
        }
    }

    private val bookmarkDataObserver = Observer<BaseStateModel<out PagedList<PetEntity>>> {
        val baseStateModel = it ?: return@Observer
        when (baseStateModel) {
            is Loading -> {
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
        val extras = FragmentNavigatorExtras(
            petViewPair.second to petViewPair.second.transitionName
        )
        getParentViewModel().homeNavigator.toPetDetailsFromFavorite(petViewPair.first.id, extras)
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
        val context = context ?: return
        emptyState.apply {
            this.emptyScreenDrawable = ContextCompat.getDrawable(
                context, R.drawable.ic_launcher_background
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
        val context = context ?: return
        val errorPair = throwable.getErrorRequestMessage(context)
        errorState.apply {
            this.errorScreenDrawable = ContextCompat.getDrawable(
                context, R.drawable.ic_launcher_background
            )
            this.errorScreenText = errorPair.first
            this.errorScreenTextSubTitle = errorPair.second
        }
    }

    private fun showErrorSnackBar(throwable: Throwable) {
        val context = context ?: return
        val msg = throwable.getErrorRequestMessage(context)
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
            R.id.scrim,
            R.id.btn_secondary_action -> {
                getViewModel().trackDeleteAll(false)
                performMaterialTransitionFor(
                    getViewDataBinding().layoutDeletePet.root,
                    getViewDataBinding().fab
                )
            }
            R.id.fab -> {
                performMaterialTransitionFor(
                    getViewDataBinding().fab,
                    getViewDataBinding().layoutDeletePet.root
                )
            }

            R.id.btn_primary_action -> {
                getViewModel().deleteAllFavoritePets()
                performMaterialTransitionFor(
                    getViewDataBinding().layoutDeletePet.root,
                    getViewDataBinding().fab
                )
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

    private fun performMaterialTransitionFor(startView: View, endView: View) {
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
            this.addTarget(endView)
            this.isElevationShadowEnabled = isExpanding
        }.addListener(object : TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                updateViewAfterTransition(startView, endView)
                super.onTransitionEnd(transition)
            }

            override fun onTransitionCancel(transition: Transition) {
                updateViewAfterTransition(startView, endView)
                super.onTransitionCancel(transition)
            }
        })
        TransitionManager.beginDelayedTransition(getViewDataBinding().cdlMain, transition)
    }

    private fun isExpanding(startView: View) = startView is FloatingActionButton

    private fun updateViewAfterTransition(startView: View, endView: View) {
        startView.invisible()
        endView.visible()
        if (isExpanding(startView)) {
            hideBottomBar()
            getViewDataBinding().scrim.visible()
        } else {
            getViewDataBinding().scrim.invisible()
            showBottomBar()
        }
    }
}