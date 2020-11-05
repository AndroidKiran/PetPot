package com.droid47.petpot.organization.data.models

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class OrganizationClusterItem constructor(
    private val name: String, private val latLng: LatLng
) : ClusterItem {
    override fun getSnippet(): String? {
        return name
    }

    override fun getTitle(): String? {
        return name
    }

    override fun getPosition(): LatLng {
        return latLng
    }
}