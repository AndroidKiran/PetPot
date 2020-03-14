package com.droid47.petgoogle.base.widgets.snappy

import android.graphics.PointF

import androidx.recyclerview.widget.LinearLayoutManager
import com.droid47.petgoogle.base.widgets.snappy.SnappySmoothScroller.ScrollVectorDetector


class LinearLayoutScrollVectorDetector(private val layoutManager: LinearLayoutManager) : ScrollVectorDetector {

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        return layoutManager.computeScrollVectorForPosition(targetPosition)
    }

}