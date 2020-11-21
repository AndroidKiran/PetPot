package com.droid47.petpot.home.presentation.di

import com.droid47.petpot.aboutUs.presentation.ui.AboutUsFragment
import com.droid47.petpot.app.di.scopes.ActivityScope
import com.droid47.petpot.app.di.scopes.FragmentScope
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

@Subcomponent(
    modules = [AbstractHomeModule::class, ViewModelBindingModule::class]
)
@ActivityScope
interface HomeActivityComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): HomeActivityComponent
    }

    @ActivityScope
    fun inject(homeActivity: HomeActivity)

    @FragmentScope
    fun inject(bottomNavDrawerFragment: BottomNavDrawerFragment)

    @FragmentScope
    fun inject(searchFragment: SearchFragment)

    @FragmentScope
    fun inject(filterFragment: FilterFragment)

    @FragmentScope
    fun inject(bookmarkFragment: BookmarkFragment)

    @FragmentScope
    fun inject(aboutUsFragment: AboutUsFragment)

    @FragmentScope
    fun inject(settingFragment: SettingFragment)

    @FragmentScope
    fun inject(petDetailsFragment: PetDetailsFragment)

    @FragmentScope
    fun inject(similarPetsFragment: SimilarPetsFragment)

    @FragmentScope
    fun inject(organizationFragment: OrganizationFragment)

    @FragmentScope
    fun inject(organizationMap: OrganizationMap)
}