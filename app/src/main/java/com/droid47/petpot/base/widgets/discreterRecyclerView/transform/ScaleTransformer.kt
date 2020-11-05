package com.droid47.petpot.base.widgets.discreterRecyclerView.transform

import android.view.View
import androidx.annotation.FloatRange
import kotlin.math.abs

class ScaleTransformer constructor(
    private var pivotX: Pivot = Pivot.X.CENTER.create(),
    private var pivotY: Pivot = Pivot.Y.CENTER.create(),
    private var minScale: Float = 0.8f,
    private var maxMinDiff: Float = 0.2f
) : DiscreteScrollItemTransformer {

    override fun transformItem(item: View, position: Float) {
        pivotX.setOn(item)
        pivotY.setOn(item)
        val closenessToCenter = 1f - abs(position)
        val scale = minScale + maxMinDiff * closenessToCenter
        item.scaleX = scale
        item.scaleY = scale
    }

    class Builder {
        private val transformer: ScaleTransformer = ScaleTransformer()
        private var maxScale: Float = 1f

        fun setMinScale(@FloatRange(from = 0.01) scale: Float): Builder {
            transformer.minScale = scale
            return this
        }

        fun setMaxScale(@FloatRange(from = 0.01) scale: Float): Builder {
            maxScale = scale
            return this
        }

        fun setPivotX(pivotX: Pivot.X): Builder {
            return setPivotX(pivotX.create())
        }

        fun setPivotX(pivot: Pivot): Builder {
            assertAxis(pivot, Pivot.AXIS_X)
            transformer.pivotX = pivot
            return this
        }

        fun setPivotY(pivotY: Pivot.Y): Builder {
            return setPivotY(pivotY.create())
        }

        fun setPivotY(pivot: Pivot): Builder {
            assertAxis(pivot, Pivot.AXIS_Y)
            transformer.pivotY = pivot
            return this
        }

        fun build(): ScaleTransformer {
            transformer.maxMinDiff = maxScale - transformer.minScale
            return transformer
        }

        private fun assertAxis(pivot: Pivot, @Pivot.Axis axis: Int) {
            require(pivot.axis != axis) { "You passed a Pivot for wrong axis." }
        }
    }

    init {
        pivotX = Pivot.X.CENTER.create()
        pivotY = Pivot.Y.CENTER.create()
        minScale = 0.8f
        maxMinDiff = 0.2f
    }
}