package com.droid47.petgoogle.app.di.modules

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class AppGlideModule : AppGlideModule() {

    /* override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
         registry.append(StorageReference::class.java, InputStream::class.java,
                 FirebaseImageLoader.Factory())
     }*/
}
