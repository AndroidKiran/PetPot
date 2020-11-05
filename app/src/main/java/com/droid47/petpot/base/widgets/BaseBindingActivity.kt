package com.droid47.petpot.base.widgets

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseBindingActivity<out B : ViewDataBinding, out V : BaseAndroidViewModel> :
    AppCompatActivity() {

    private lateinit var baseViewDataBinding: B
    private lateinit var baseViewModel: V
    abstract fun getViewModel(): V

    @LayoutRes
    abstract fun getLayoutId(): Int
    abstract fun executePendingVariablesBinding()
    abstract fun injectComponent()
    abstract fun getClassName(): String
    abstract fun getScreenName() : String

    override fun onCreate(savedInstanceState: Bundle?) {
        injectComponent()
        super.onCreate(savedInstanceState)
        performDataBinding()
    }

    fun getViewDataBinding(): B = baseViewDataBinding

    private fun performDataBinding() {
        baseViewModel = getViewModel()
        baseViewDataBinding =
            DataBindingUtil.setContentView(this@BaseBindingActivity, getLayoutId())
        executePendingVariablesBinding()
        baseViewDataBinding.run {
            executePendingBindings()
        }
    }
}
