package com.droid47.petfriend.base.widgets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBindingBottomSheetDialogFragment<out B : ViewDataBinding, out V : BaseAndroidViewModel, out PV : BaseAndroidViewModel> :
    BottomSheetDialogFragment() {

    private lateinit var baseViewDataBinding: B
    private lateinit var baseViewModel: V
    private lateinit var rootView: View
    private lateinit var parentViewModel: PV

    private var navFragmentId: Int = 0

    @LayoutRes
    abstract fun getLayoutId(): Int
    abstract fun getFragmentNavId(): Int
    abstract fun executePendingVariablesBinding()
    abstract fun getViewModel(): V
    abstract fun getParentViewModel(): PV
    abstract fun getSnackBarAnchorView(): View
    abstract fun injectSubComponent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseViewModel = getViewModel()
        parentViewModel = getParentViewModel()
        navFragmentId = getFragmentNavId()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injectSubComponent()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        DataBindingUtil.inflate<B>(inflater, getLayoutId(), container, false).also {
            baseViewDataBinding = it
            rootView = baseViewDataBinding.root
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        executePendingVariablesBinding()
        baseViewDataBinding.executePendingBindings()
    }

    fun getViewDataBinding(): B = baseViewDataBinding
}