package com.droid47.petgoogle.base.widgets

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseBindingActivity<out B : ViewDataBinding, out V : BaseAndroidViewModel> :
    DaggerAppCompatActivity() {

    private lateinit var baseViewDataBinding: B
    private lateinit var baseViewModel: V

    abstract fun getViewModel(): V
    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun executePendingVariablesBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
    }

    fun getViewDataBinding(): B = baseViewDataBinding

    private fun performDataBinding() {
        baseViewModel = getViewModel()
        baseViewDataBinding =
            DataBindingUtil.setContentView(this@BaseBindingActivity, getLayoutId())
        executePendingVariablesBinding()
        baseViewDataBinding.executePendingBindings()
    }

    private fun applyTheme() {
    }


//    fun setStatusBarColorFromResource(@ColorRes colorRes: Int) {
//        setStatusBarColor(ContextCompat.getColor(this, colorRes))
//    }
//
//    fun setStatusBarColor(color: Int) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.statusBarColor = color
//        }
//    }
//
//    private fun applySystemUiVisibilityFlag() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
////            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
////            window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
//                    SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
////            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
////            window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
//        }
//    }
}
