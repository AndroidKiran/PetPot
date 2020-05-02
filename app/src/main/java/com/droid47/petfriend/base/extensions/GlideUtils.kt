package com.droid47.petfriend.base.extensions

import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

fun loadListener(progressView: View? = null, block: (loaded: Boolean) -> Unit) =
    GlideDrawableLoadListener(block, progressView)

class GlideDrawableLoadListener(private val block: (loaded: Boolean) -> Unit, private val progressView: View? = null) :
    RequestListener<Drawable> {

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        progressView?.gone()
        block(true)
        return false
    }

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        progressView?.gone()
        block(true)
        return false
    }
}
