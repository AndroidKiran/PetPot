package com.droid47.petfriend.organization.presentation.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.*
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.storage.LocalPreferencesRepository
import com.droid47.petfriend.home.presentation.ui.HomeActivity
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity
import com.droid47.petfriend.organization.data.models.OrganizationClusterItem
import com.droid47.petfriend.organization.presentation.viewmodel.OrganizationViewModel
import com.droid47.petfriend.organization.presentation.viewmodel.OrganizationViewModel.Companion.EVENT_MAP_READY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.ClusterManager
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class OrganizationMap : SupportMapFragment(), OnMapReadyCallback {

    @Inject
    lateinit var localSharedPreference: LocalPreferencesRepository
    private var clusterManager: ClusterManager<OrganizationClusterItem>? = null
    private var googleMap: GoogleMap? = null
    private val compositeDisposable = CompositeDisposable()

    private val organizationViewModel: OrganizationViewModel by lazy(LazyThreadSafetyMode.NONE) {
        requireParentFragment().parentFragmentViewModelProvider<OrganizationViewModel>()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injectSubComponent()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.getMapAsync(this@OrganizationMap)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        try {
            organizationViewModel.eventLiveData.postValue(EVENT_MAP_READY)
            this.googleMap = googleMap ?: return
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    if (localSharedPreference.fetchAppliedTheme() == DARK_MODE) R.raw.map_dark else R.raw.map_retro
                )
            )
            setUpClusterer(googleMap)
        } catch (exception: Exception) {
            CrashlyticsExt.handleException(exception)
        }
    }

    private fun injectSubComponent() {
        (activity as HomeActivity).homeComponent.inject(this@OrganizationMap)
    }

    private fun setUpClusterer(googleMap: GoogleMap) {
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(51.503186, -0.126446), 10f))
        clusterManager = ClusterManager<OrganizationClusterItem>(requireContext(), googleMap)
        with(googleMap) {
            setOnCameraIdleListener(clusterManager)
            setOnMarkerClickListener(clusterManager)
        }
    }

    private fun clearClusterAndAdd(organizationClusterItems: List<OrganizationClusterItem>) {
        if (organizationClusterItems.isEmpty()) return
        clusterManager?.run {
            addItems(organizationClusterItems)
            cluster()
        }
    }

    private fun toOrganizationClusterItemList(organizations: List<OrganizationCheckableEntity?>): List<OrganizationClusterItem> {
        if (organizations.isEmpty()) return emptyList()
        val context = context ?: return emptyList()
        return organizations.filterNotNull().map { organizationCheckableEntity ->
            val name = organizationCheckableEntity.name ?: ""
            val latLong =
                context.getLocationFromAddress(organizationCheckableEntity.getFullAddress())
            OrganizationClusterItem(name, latLong)
        }.toList()
    }

    fun clearItems() {
        compositeDisposable.clearDisposable()
        clusterManager?.clearItems()
    }

    fun addOrganizationsToMap(organizations: List<OrganizationCheckableEntity>) {
        Single.fromCallable {
            return@fromCallable toOrganizationClusterItemList(organizations)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<OrganizationClusterItem>> {
                override fun onSuccess(organizationClusterItems: List<OrganizationClusterItem>) {
                    clearClusterAndAdd(organizationClusterItems)
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onError(e: Throwable) {
                    CrashlyticsExt.handleException(e)
                }
            })
    }

}