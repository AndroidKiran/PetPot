package com.droid47.petpot.app.di.modules

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.annotation.RequiresApi
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.droid47.petpot.base.firebase.CrashlyticsExt


private val TAG: String = AppGlideModule::class.java.simpleName
private const val SMALL_INTERNAL_STORAGE_THRESHOLD_GIB = 8
private const val DISK_CACHE_SIZE_FOR_SMALL_INTERNAL_STORAGE_MIB = 2 * 1024 * 1024

// 5MB Cache Limit For Devices with internal storage < 16GB
private const val MEDIUM_INTERNAL_STORAGE_THRESHOLD_GIB = 16
private const val DISK_CACHE_SIZE_FOR_MEDIUM_INTERNAL_STORAGE_MIB = 4 * 1024 * 1024
private const val DISK_CACHE_SIZE_FOR_LARGE_INTERNAL_STORAGE_MIB = 6 * 1024 * 1024

@GlideModule
class GlideConfiguration : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        try {
            val totalGiB =
                getTotalBytesOfInternalStorage() / 1024.0 / 1024.0 / 1024.0
            val diskSize = when {
                totalGiB < SMALL_INTERNAL_STORAGE_THRESHOLD_GIB ->
                    DISK_CACHE_SIZE_FOR_SMALL_INTERNAL_STORAGE_MIB
                totalGiB < MEDIUM_INTERNAL_STORAGE_THRESHOLD_GIB ->
                    DISK_CACHE_SIZE_FOR_MEDIUM_INTERNAL_STORAGE_MIB
                else -> DISK_CACHE_SIZE_FOR_LARGE_INTERNAL_STORAGE_MIB
            }
            builder.setMemoryCache(LruResourceCache(diskSize.toLong()))
            builder.setDiskCache(InternalCacheDiskCacheFactory(context, diskSize.toLong()))
            builder.setDefaultRequestOptions(requestOptions())
        } catch (e: Exception) {
            CrashlyticsExt.handleException(e)
        }
    }

    private fun getTotalBytesOfInternalStorage(): Long {
        val stat = StatFs(Environment.getDataDirectory().path)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getTotalBytesOfInternalStorageWithStatFs(stat)
        } else {
            getTotalBytesOfInternalStorageWithStatFsPreJBMR2(stat)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun getTotalBytesOfInternalStorageWithStatFs(stat: StatFs): Long = stat.totalBytes

    private fun getTotalBytesOfInternalStorageWithStatFsPreJBMR2(stat: StatFs): Long =
        stat.blockSizeLong * stat.blockSizeLong

    private fun requestOptions(): RequestOptions =
        RequestOptions()
            .centerCrop()
            .encodeFormat(Bitmap.CompressFormat.PNG)
            .encodeQuality(100)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .format(DecodeFormat.PREFER_ARGB_8888)
            .skipMemoryCache(false)
}
