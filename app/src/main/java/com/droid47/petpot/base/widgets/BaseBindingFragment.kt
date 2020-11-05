package com.droid47.petpot.base.widgets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.droid47.petpot.R
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.google.android.material.bottomappbar.BottomAppBar

interface NavigationHost {
    /** Called by BaseBindingFragment to setup it's toolbar with the navigation controller. */
    fun registerBottomAppbarWithNavigation(bottomAppBar: BottomAppBar)
}

abstract class BaseBindingFragment<out B : ViewDataBinding, out V : BaseAndroidViewModel, out PV : BaseAndroidViewModel> :
    Fragment() {

    private lateinit var baseViewDataBinding: B
    private lateinit var baseViewModel: V
    private lateinit var parentViewModel: PV

    private var navFragmentId: Int = 0
    private var navigationHost: NavigationHost? = null

    @LayoutRes
    abstract fun getLayoutId(): Int
    abstract fun getFragmentNavId(): Int
    abstract fun executePendingVariablesBinding()
    abstract fun getViewModel(): V
    abstract fun getParentViewModel(): PV
    abstract fun getSnackBarAnchorView(): View
    abstract fun injectSubComponent()
    abstract fun getClassName(): String
    abstract fun getScreenName(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseViewModel = getViewModel()
        parentViewModel = getParentViewModel()
        navFragmentId = getFragmentNavId()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        retainInstance = true
        injectSubComponent()
        navigationHost = context as? NavigationHost?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        DataBindingUtil.inflate<B>(inflater, getLayoutId(), container, false)?.also {
            baseViewDataBinding = it
        }?.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        executePendingVariablesBinding()
        baseViewDataBinding.executePendingBindings()
        registerBottomBar()
    }

    override fun onDetach() {
        navigationHost = null
        super.onDetach()
    }

    fun getViewDataBinding(): B = baseViewDataBinding

    fun trackFragment(firebaseManager: IFirebaseManager) {
        val baseActivity = requireActivity() as BaseBindingActivity<*, *>
        firebaseManager.sendScreenView(getScreenName(), getClassName(), baseActivity)
    }

    private fun registerBottomBar() {
        val bottomAppBar =
            getViewDataBinding().root.findViewById<BottomAppBar>(R.id.bottom_app_bar) ?: return
        navigationHost?.registerBottomAppbarWithNavigation(bottomAppBar)
    }
}