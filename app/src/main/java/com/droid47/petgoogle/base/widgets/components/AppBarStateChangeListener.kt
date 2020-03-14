package com.droid47.petgoogle.base.widgets.components

import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

abstract class AppBarStateChangeListener(private var currentState: State) :
    AppBarLayout.OnOffsetChangedListener {

    override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
        onOffsetChange(appBarLayout, i)
        val oldState = currentState
        if (abs(i) == appBarLayout.totalScrollRange) {
            currentState = State.COLLAPSED
            if (oldState != State.COLLAPSED) {
                onStateChange(appBarLayout, State.COLLAPSED)
            }
        } else {
            currentState = State.EXPANDED
            if (oldState != State.EXPANDED) {
                onStateChange(appBarLayout, State.EXPANDED)
            }
        }
    }

    abstract fun onStateChange(appBarLayout: AppBarLayout, state: State)

    abstract fun onOffsetChange(appBarLayout: AppBarLayout, offset: Int)

    enum class State {
        EXPANDED,
        COLLAPSED
    }
}