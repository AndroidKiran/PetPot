package com.droid47.petpot.base.customview

interface MvvmCustomViewModel<T: MvvmCustomViewState> {
    var state: T?
}