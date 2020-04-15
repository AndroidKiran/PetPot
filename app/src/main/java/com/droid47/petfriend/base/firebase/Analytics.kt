package com.droid47.petfriend.base.firebase

import android.content.Context

interface Analytics {

    fun setCollectionEnabled(status: Boolean)

    fun setCollectionEnabledOnTncStatus()

    fun getContext(): Context
}