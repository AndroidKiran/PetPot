package com.droid47.petfriend.base.firebase

import com.google.firebase.perf.FirebasePerformance


object FirebasePerformance {

    fun setCollectionEnabled(status: Boolean) {
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = status
    }
}