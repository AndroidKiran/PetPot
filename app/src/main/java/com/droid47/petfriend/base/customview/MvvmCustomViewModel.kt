package com.droid47.petfriend.base.customview

interface MvvmCustomViewModel<T: MvvmCustomViewState> {
    var state: T?
}