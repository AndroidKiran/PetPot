package com.droid47.petgoogle.home.presentation

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.droid47.petgoogle.R
import com.droid47.petgoogle.base.extensions.*
import com.droid47.petgoogle.base.widgets.BaseBindingFragment
import com.droid47.petgoogle.databinding.FragmentBottomNavDrawerBinding
import com.droid47.petgoogle.home.presentation.components.*
import com.droid47.petgoogle.home.presentation.viewmodels.HomeViewModel
import com.droid47.petgoogle.home.presentation.viewmodels.NavigationViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable
import javax.inject.Inject
import kotlin.math.abs

class BottomNavDrawerFragment :
    BaseBindingFragment<FragmentBottomNavDrawerBinding, NavigationViewModel, HomeViewModel>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val navigationViewModel: NavigationViewModel by lazy {
        viewModelProvider<NavigationViewModel>(factory)
    }

    private val homeViewModel: HomeViewModel by lazy {
        activityViewModelProvider<HomeViewModel>(requireActivity())
    }

    private lateinit var behavior: BottomSheetBehavior<FrameLayout>
    private val bottomSheetCallback =
        BottomNavigationDrawerCallback()
    private val sandwichSlideActions = mutableListOf<OnSandwichSlideAction>()

    private val backgroundShapeDrawable: MaterialShapeDrawable by lazy(LazyThreadSafetyMode.NONE) {
        MaterialShapeDrawable(
            requireContext(),
            null,
            R.attr.bottomSheetStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                requireContext().themeColor(R.attr.colorPrimarySurfaceVariant)
            )
            elevation = resources.getDimension(R.dimen.grid_1)
            initializeElevationOverlay(requireContext())
        }
    }

    private val foregroundShapeDrawable: MaterialShapeDrawable by lazy(LazyThreadSafetyMode.NONE) {
        MaterialShapeDrawable(
            requireContext(),
            null,
            R.attr.bottomSheetStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                requireContext().themeColor(R.attr.colorPrimarySurface)
            )
            elevation = resources.getDimension(R.dimen.plane_16)
            shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_NEVER
            initializeElevationOverlay(requireContext())
            shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                .setTopEdge(
                    SemiCircleEdgeCutoutTreatment(
                        resources.getDimension(R.dimen.grid_1),
                        resources.getDimension(R.dimen.grid_3),
                        0F,
                        resources.getDimension(R.dimen.grid_3)
                    )
                )
                .build()
        }
    }

    private var sandwichState: SandwichState = SandwichState.CLOSED
    private var sandwichAnim: ValueAnimator? = null
    private val sandwichInterp by lazy(LazyThreadSafetyMode.NONE) {
        requireContext().themeInterpolator(R.attr.motionInterpolatorPersistent)
    }

    // Progress value which drives the animation of the sandwiching account picker. Responsible
    // for both calling progress updates and state updates.
    private var sandwichProgress: Float = 0F
        set(value) {
            if (field != value) {
                onSandwichProgressChanged(value)
                val newState = when (value) {
                    0F -> SandwichState.CLOSED
                    1F -> SandwichState.OPEN
                    else -> SandwichState.SETTLING
                }
                if (sandwichState != newState) onSandwichStateChanged(newState)
                sandwichState = newState
                field = value
            }
        }

    private val closeDrawerOnBackPressed = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            close()
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_bottom_nav_drawer

    override fun getFragmentNavId(): Int = R.id.navigation_bottom

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.navigationViewModel = getViewModel()
            it.homeViewModel = getParentViewModel()
        }
    }

    override fun getViewModel(): NavigationViewModel = navigationViewModel

    override fun getParentViewModel(): HomeViewModel = homeViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as HomeActivity).homeComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, closeDrawerOnBackPressed)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setUpBottomSheet()
    }

    private fun initViews() {
        getViewDataBinding().run {
            behavior = BottomSheetBehavior.from(backgroundContainer).apply {
                addBottomSheetCallback(bottomSheetCallback)
                state = BottomSheetBehavior.STATE_HIDDEN
            }
            backgroundContainer.background = backgroundShapeDrawable
            foregroundContainer.background = foregroundShapeDrawable
            scrimView.setOnClickListener { close() }
            profileImageView.setOnClickListener { toggleSandwich() }
        }
    }

    private fun setUpBottomSheet() {
        getViewDataBinding().run {
            bottomSheetCallback.apply {
                // Scrim view transforms
                addOnSlideAction(
                    AlphaSlideAction(
                        scrimView
                    )
                )
                addOnStateChangedAction(
                    VisibilityStateAction(
                        scrimView
                    )
                )
                // Foreground transforms
                addOnSlideAction(
                    ForegroundSheetTransformSlideAction(
                        getViewDataBinding().foregroundContainer,
                        foregroundShapeDrawable,
                        getViewDataBinding().profileImageView
                    )
                )
                // Close the sandwiching account picker if open
                addOnStateChangedAction(object :
                    OnStateChangedAction {
                    override fun onStateChanged(sheet: View, newState: Int) {
                        sandwichAnim?.cancel()
                        sandwichProgress = 0F
                    }
                })
                // If the drawer is open, pressing the system back button should close the drawer.
                addOnStateChangedAction(object :
                    OnStateChangedAction {
                    override fun onStateChanged(sheet: View, newState: Int) {
                        closeDrawerOnBackPressed.isEnabled =
                            newState != BottomSheetBehavior.STATE_HIDDEN
                    }
                })
            }
        }
    }

    fun toggle() {
        when {
            sandwichState == SandwichState.OPEN -> toggleSandwich()
            behavior.state == BottomSheetBehavior.STATE_HIDDEN -> open()
            behavior.state == BottomSheetBehavior.STATE_HIDDEN
                    || behavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED
                    || behavior.state == BottomSheetBehavior.STATE_EXPANDED
                    || behavior.state == BottomSheetBehavior.STATE_COLLAPSED -> close()
        }
    }

    fun open() {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun close() {
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun addOnSlideAction(action: OnSlideAction) {
        bottomSheetCallback.addOnSlideAction(action)
    }

    fun addOnStateChangedAction(action: OnStateChangedAction) {
        bottomSheetCallback.addOnStateChangedAction(action)
    }

    fun isExpanded() =
        behavior.state == BottomSheetBehavior.STATE_EXPANDED || behavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED


    /**
     * Add actions to be run when the slide offset (animation progress) or the sandwiching account
     * picker has changed.
     */
    fun addOnSandwichSlideAction(action: OnSandwichSlideAction) {
        sandwichSlideActions.add(action)
    }

    private fun toggleSandwich() {
        val initialProgress = sandwichProgress
        val newProgress = when (sandwichState) {
            SandwichState.CLOSED -> {
                getViewDataBinding().backgroundContainer.setTag(
                    R.id.tag_view_top_snapshot,
                    getViewDataBinding().backgroundContainer.top
                )
                1F
            }
            SandwichState.OPEN -> 0F
            SandwichState.SETTLING -> return
        }
        sandwichAnim?.cancel()
        sandwichAnim = ValueAnimator.ofFloat(initialProgress, newProgress).apply {
            addUpdateListener { sandwichProgress = animatedValue as Float }
            interpolator = sandwichInterp
            duration = (abs(newProgress - initialProgress) *
                    resources.getInteger(R.integer.pet_motion_duration_medium)).toLong()
        }
        sandwichAnim?.start()
    }

    private fun onSandwichProgressChanged(progress: Float) {
        getViewDataBinding().run {
            val navProgress = lerp(
                0F,
                1F,
                0F,
                0.5F,
                progress
            )
            val accProgress = lerp(
                0F,
                1F,
                0.5F,
                1F,
                progress
            )

            foregroundContainer.translationY =
                (getViewDataBinding().foregroundContainer.height * 0.15F) * navProgress
            profileImageView.scaleX = 1F - navProgress
            profileImageView.scaleY = 1F - navProgress
            profileImageView.alpha = 1F - navProgress
            foregroundContainer.alpha = 1F - navProgress
            foregroundShapeDrawable.interpolation = 1F - navProgress

            // Animate the translationY of the backgroundContainer so just the account picker is
            // peeked above the BottomAppBar.
            backgroundContainer.translationY =
                progress * ((scrimView.bottom - resources.getDimension(R.dimen.bottom_app_bar_height)) -
                        (backgroundContainer.getTag(R.id.tag_view_top_snapshot) as Int))
        }

        // Call any actions which have been registered to run on progress changes.
        sandwichSlideActions.forEach { it.onSlide(progress) }
    }


    private fun onSandwichStateChanged(state: SandwichState) {
        // Change visibility/clickability of views which obstruct user interaction with
        // the account list.
        when (state) {
            SandwichState.OPEN -> {
                getViewDataBinding().run {
                    foregroundContainer.visibility = View.GONE
                    profileImageView.isClickable = false
                }
            }
            else -> {
                getViewDataBinding().run {
                    foregroundContainer.visibility = View.VISIBLE
                    profileImageView.isClickable = true
                }
            }
        }
    }

}