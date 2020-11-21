package com.droid47.petpot.base.widgets.components

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

class PetWebView @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attributes, defStyleAttr) {

    var scrollListener: WebViewScrollListener? = null

    override fun onScrollChanged(scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY)
        when {
            scrollY > oldScrollY -> scrollListener?.onScrollDown()
            scrollY < oldScrollY -> scrollListener?.onScrollUp()
        }
        scrollListener?.onScroll(scrollX, scrollY, oldScrollX, oldScrollY)
    }

    interface WebViewScrollListener {
        fun onScroll(scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int)
        fun onScrollDown()
        fun onScrollUp()
    }
}