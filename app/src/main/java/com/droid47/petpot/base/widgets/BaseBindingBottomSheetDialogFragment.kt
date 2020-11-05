package com.droid47.petpot.base.widgets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBindingBottomSheetDialogFragment<out B : ViewDataBinding, out V : BaseAndroidViewModel, out PV : BaseAndroidViewModel> :
    BottomSheetDialogFragment() {

    private lateinit var baseViewDataBinding: B

    @LayoutRes
    abstract fun getLayoutId(): Int
    abstract fun getFragmentNavId(): Int
    abstract fun executePendingVariablesBinding()
    abstract fun getViewModel(): V
    abstract fun getParentViewModel(): PV
    abstract fun getSnackBarAnchorView(): View
    abstract fun injectSubComponent()
    abstract fun getClassName(): String
    abstract fun getScreenName() : String

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
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        executePendingVariablesBinding()
        baseViewDataBinding.executePendingBindings()
    }

    fun trackFragment(firebaseManager: IFirebaseManager) {
        val baseActivity = requireActivity() as BaseBindingActivity<*, *>
        firebaseManager.sendScreenView(getScreenName(), getClassName(), baseActivity)
    }

    fun getViewDataBinding(): B = baseViewDataBinding

}