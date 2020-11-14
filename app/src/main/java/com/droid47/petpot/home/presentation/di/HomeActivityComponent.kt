package com.droid47.petpot.home.presentation.di

import com.droid47.petpot.aboutUs.presentation.ui.AboutUsFragment
import com.droid47.petpot.app.di.scopes.ActivityScope
import com.droid47.petpot.home.presentation.ui.BottomNavDrawerFragment
import com.droid47.petpot.home.presentation.ui.HomeActivity
import com.droid47.petpot.home.presentation.ui.SettingFragment
import com.droid47.petpot.organization.presentation.ui.OrganizationFragment
import com.droid47.petpot.organization.presentation.ui.OrganizationMap
import com.droid47.petpot.petDetails.presentation.ui.PetDetailsFragment
import com.droid47.petpot.petDetails.presentation.ui.SimilarPetsFragment
import com.droid47.petpot.search.presentation.ui.BookmarkFragment
import com.droid47.petpot.search.presentation.ui.FilterFragment
import com.droid47.petpot.search.presentation.ui.SearchFragment
import dagger.Subcomponent

@ActivityScope
@Subcomponent(
    modules = [AbstractHomeModule::class, ViewModelBindingModule::class]
)
interface HomeActivityComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): HomeActivityComponent
    }

    fun inject(homeActivity: HomeActivity)
    fun inject(bottomNavDrawerFragment: BottomNavDrawerFragment)
    fun inject(searchFragment: SearchFragment)
    fun inject(filterFragment: FilterFragment)
    fun inject(bookmarkFragment: BookmarkFragment)
    fun inject(aboutUsFragment: AboutUsFragment)
    fun inject(settingFragment: SettingFragment)
    fun inject(petDetailsFragment: PetDetailsFragment)
    fun inject(similarPetsFragment: SimilarPetsFragment)
    fun inject(organizationFragment: OrganizationFragment)
    fun inject(organizationMap: OrganizationMap)
}