package com.droid47.petgoogle.base.firebase

import android.content.Context

interface Analytics {

    fun setCollectionEnabled(status: Boolean)

    fun setCollectionEnabledOnTncStatus()

    fun getContext(): Context
}