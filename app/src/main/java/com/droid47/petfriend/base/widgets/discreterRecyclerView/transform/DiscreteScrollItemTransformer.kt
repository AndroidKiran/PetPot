package com.droid47.petfriend.base.widgets.discreterRecyclerView.transform

import android.view.View

interface DiscreteScrollItemTransformer {
    fun transformItem(item: View, position: Float)
}
