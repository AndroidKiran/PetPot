package com.droid47.petgoogle.base.firebase

import com.google.firebase.perf.FirebasePerformance


object FirebasePerformance {

    fun setCollectionEnabled(status: Boolean) {
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = status
    }
}