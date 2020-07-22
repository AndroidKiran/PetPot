package com.droid47.petfriend.base.widgets.discreterRecyclerView.transform

import android.view.View
import androidx.annotation.IntDef

class Pivot(
    @get:Axis
    @param:Axis val axis: Int,
    private val pivotPoint: Int
) {
    fun setOn(view: View) {
        if (axis == AXIS_X) {
            when (pivotPoint) {
                PIVOT_CENTER -> view.pivotX = view.width * 0.5f
                PIVOT_MAX -> view.pivotX = view.width.toFloat()
                else -> view.pivotX = pivotPoint.toFloat()
            }
            return
        }
        if (axis == AXIS_Y) {
            when (pivotPoint) {
                PIVOT_CENTER -> view.pivotY = view.height * 0.5f
                PIVOT_MAX -> view.pivotY = view.height.toFloat()
                else -> view.pivotY = pivotPoint.toFloat()
            }
        }
    }

    enum class X {
        LEFT {
            override fun create(): Pivot {
                return Pivot(AXIS_X, 0)
            }
        },
        CENTER {
            override fun create(): Pivot {
                return Pivot(AXIS_X, PIVOT_CENTER)
            }
        },
        RIGHT {
            override fun create(): Pivot {
                return Pivot(AXIS_X, PIVOT_MAX)
            }
        };

        abstract fun create(): Pivot
    }

    enum class Y {
        TOP {
            override fun create(): Pivot {
                return Pivot(AXIS_Y, 0)
            }
        },
        CENTER {
            override fun create(): Pivot {
                return Pivot(AXIS_Y, PIVOT_CENTER)
            }
        },
        BOTTOM {
            override fun create(): Pivot {
                return Pivot(AXIS_Y, PIVOT_MAX)
            }
        };

        abstract fun create(): Pivot
    }

    @IntDef(AXIS_X, AXIS_Y)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Axis
    companion object {
        const val AXIS_X = 0
        const val AXIS_Y = 1
        private const val PIVOT_CENTER = -1
        private const val PIVOT_MAX = -2
    }
}