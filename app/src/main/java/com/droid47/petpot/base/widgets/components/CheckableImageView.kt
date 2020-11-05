package com.droid47.petpot.base.widgets.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.droid47.petpot.R


class CheckableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.imageButtonStyle
) : AppCompatImageView(context, attrs, defStyleAttr), Checkable {

    private var onCheckedChangeListener: OnCheckedChangeListener? = null
    private var checked = false

    init {
        ViewCompat.setAccessibilityDelegate(
            this,
            object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityEvent(
                    host: View,
                    event: AccessibilityEvent
                ) {
                    super.onInitializeAccessibilityEvent(host, event)
                    event.isChecked = isChecked
                }

                override fun onInitializeAccessibilityNodeInfo(
                    host: View, info: AccessibilityNodeInfoCompat
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.isCheckable = true
                    info.isChecked = isChecked
                }
            })
    }

    override fun setChecked(checked: Boolean) {
        if (this.checked != checked) {
            this.checked = checked
            refreshDrawableState()
            sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
            onCheckedChangeListener?.onCheckedChanged(this, checked)
        }
    }

    override fun isChecked(): Boolean {
        return checked
    }

    override fun toggle() {
        isChecked = !checked
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        return if (checked) {
            View.mergeDrawableStates(
                super.onCreateDrawableState(extraSpace + DRAWABLE_STATE_CHECKED.size),
                DRAWABLE_STATE_CHECKED
            )
        } else {
            super.onCreateDrawableState(extraSpace)
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(button: CheckableImageView?, isChecked: Boolean)
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        onCheckedChangeListener = listener
    }

    companion object {
        private val DRAWABLE_STATE_CHECKED =
            intArrayOf(android.R.attr.state_checked)
    }
}