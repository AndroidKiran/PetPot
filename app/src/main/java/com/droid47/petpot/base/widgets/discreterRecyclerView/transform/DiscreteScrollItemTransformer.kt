package com.droid47.petpot.base.widgets.discreterRecyclerView.transform

import android.view.View

interface DiscreteScrollItemTransformer {
    fun transformItem(item: View, position: Float)
}
