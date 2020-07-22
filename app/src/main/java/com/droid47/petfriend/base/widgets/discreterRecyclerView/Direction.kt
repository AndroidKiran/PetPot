package com.droid47.petfriend.base.widgets.discreterRecyclerView

enum class Direction {
    START {
        override fun applyTo(delta: Int): Int {
            return delta * -1
        }

        override fun sameAs(direction: Int): Boolean {
            return direction < 0
        }
    },
    END {
        override fun applyTo(delta: Int): Int {
            return delta
        }

        override fun sameAs(direction: Int): Boolean {
            return direction > 0
        }
    };

    abstract fun applyTo(delta: Int): Int
    abstract fun sameAs(direction: Int): Boolean

    companion object {
        fun fromDelta(delta: Int): Direction {
            return if (delta > 0) END else START
        }
    }
}