package com.droid47.petpot.app.di.modules

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.droid47.petpot.base.firebase.CrashlyticsExt


private val TAG: String = AppGlideModule::class.java.simpleName

@GlideModule
class GlideConfiguration : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        try {
            builder.setDefaultRequestOptions(requestOptions())
        } catch (e: Exception) {
            CrashlyticsExt.handleException(e)
        }
    }

    private fun requestOptions(): RequestOptions =
        RequestOptions()
            .encodeFormat(Bitmap.CompressFormat.PNG)
            .encodeQuality(100)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(false)
}
