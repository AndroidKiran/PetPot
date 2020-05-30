package com.droid47.petfriend.base.customview

import androidx.lifecycle.LifecycleOwner

interface MvvmCustomView<V: MvvmCustomViewState, T: MvvmCustomViewModel<V>> {
    val viewModel: T
    fun onLifecycleOwnerAttached(lifecycleOwner: LifecycleOwner)
}